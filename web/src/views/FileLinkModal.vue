<template>
	<article class="modal-body">
		<section v-if="modalState == 1">
			<h1>Detected media names</h1>
			<table>
				<tr>
					<th />
					<th>File name</th>
					<th>Media name</th>
					<th>S</th>
					<th>Ep</th>
				</tr>
				<tr
					v-for="result in detectedFileResults"
					:key="result.fileName"
				>
					<td>
						<input
							type="checkbox"
							:checked="result.mediaName != null"
						>
					</td>
					<td>{{ result.fileName }}</td>
					<td>
						<input
							type="text"
							:value="result.mediaName"
						>
					</td>
					<td>
						<input
							type="number"
							:value="result.seasonNumber"
						>
					</td>
					<td>
						<input
							type="number"
							:value="result.episodeNumber"
						>
					</td>
				</tr>
			</table>
			<b-button
				class="next-button"
				@click="moveToQuerySelection"
			>
				NEXT
			</b-button>
		</section>

		<section v-if="modalState == 2">
			<h1>Link to media</h1>
			<hr>
			<ul>
				<li
					v-for="mediaName in mediaNames"
					:key="mediaName.mediaName"
				>
					{{ mediaName.mediaName }} ({{ mediaName.files.length }} files)
				</li>
			</ul>
			<template v-if="!querying">
				<b-button @click="useMyLibrary">
					My library
				</b-button>
				<b-button @click="useMyAnimeList">
					MyAnimeList
				</b-button>
			</template>
		</section>

		<section v-if="modalState == 3">
			<h1>Verify result</h1>
			<hr>
			<ul>
				<li
					v-for="result in queryResults"
					:key="result.mediaName.mediaName"
				>
					{{ result.mediaName.mediaName }}
					<b-select v-model="result.selectedResult">
						<option
							v-for="(opt, index) in result.results"
							:key="index"
							:value="opt"
						>
							{{ opt.name }} ({{ opt.extDbCode }}:{{ opt.extDbCode }})
						</option>
					</b-select>
				</li>
			</ul>
			<b-button @click="finalizeSelection">
				Finalize
			</b-button>
		</section>
	</article>
</template>

<script lang="ts">
import { Vue, Prop } from 'vue-property-decorator'
import { FileLinkRow } from "@/lib/FileLinkRow"
import { ExtDatabase, Gql } from '@/zeus'
import { MediaPartCollection } from "@/lib/Queries"

interface MediaName {
	mediaName: string

	files: FileLinkRow[]
}

interface SearchResult {
	mediaName: MediaName

	results: QueryResult[]

	selectedResult: QueryResult | null
}

interface QueryResult {
	name: string

	extDb: ExtDatabase

	extDbCode: string

	saved: boolean
}

export default class FileLinkModal extends Vue {
	@Prop({ required: true })
	private readonly detectedFileResults!: FileLinkRow[]
	
	private modalState = 1
	private mediaNames: MediaName[] = []
	private querying = false
	private queryResults: SearchResult[] = []

	private moveToQuerySelection(): void {
		this.mediaNames = []
		const resultWithName = this.detectedFileResults.filter(e => e.mediaName != null)
		const map = this.groupBy<string, FileLinkRow>(resultWithName, e => {
			if (e.mediaName == null) {
				throw new Error("Media name cannot be empty")
			} else {
				return e.mediaName
			}
		})
		for (const [key, value] of map.entries()) {
			this.mediaNames.push({ mediaName: key, files: value })
		}
		this.modalState = 2
	}

	private useMyLibrary(): void {
		this.querying = true
		const promises = this.mediaNames.map(e => this.internalMediaSearch(e))
		Promise.all(promises).then(
			results => {
				this.handleQueryResults(results)
			}
		)
	}

	private useMyAnimeList(): void {
		this.querying = true
		const promises = this.mediaNames.map(e => this.myAnimeListSearch(e))
		Promise.all(promises).then(
			results => {
				this.handleQueryResults(results)
			}
		)
	}

	private async internalMediaSearch(mediaName: MediaName): Promise<SearchResult> {
		const response = await Gql("query")({
			internalMediaSearch: [
				{
					name: mediaName.mediaName
				},
				{
					id: true,
					name: true,
					databaseEntry: {
						mediaDatabase: true,
						code: true,
					}
				}
			]
		})
		const results = response.internalMediaSearch.map(result => {
			if (result.databaseEntry == null) {
				throw new Error("Result has no DB entry")
			} else {
				const t: QueryResult = {
					extDb: result.databaseEntry.mediaDatabase,
					extDbCode: result.databaseEntry.code,
					saved: true,
					name: result.name
				}
				return t
			}
		})
		const t: SearchResult = {
			mediaName: mediaName,
			results: results,
			selectedResult: null,
		}
		return t
	}

	private async myAnimeListSearch(mediaName: MediaName): Promise<SearchResult> {
		const response = await Gql("query")({
			externalMediaSearch: [
				{
					name: mediaName.mediaName,
					db: ExtDatabase.MyAnimeList
				},
				{
					name: true,
					saved: true,
					extDb: true,
					extDbCode: true,
				}
			]
		})
		const results = response.externalMediaSearch.map(result => {
			const ret: QueryResult = {
				extDb: result.extDb,
				extDbCode: result.extDbCode,
				saved: result.saved,
				name: result.name,
			}
			return ret
		})
		const r: SearchResult = {
			mediaName: mediaName,
			results: results,
			selectedResult: null,
		}
		return r
	}

	private handleQueryResults(results: SearchResult[]): void {
		this.queryResults = results
		this.modalState = 3
		this.querying = false
	}

	private async finalizeSelection(): Promise<void> {
		const success = await this.addUnsavedMedia()
		for (const result of this.queryResults) {
			if (result.selectedResult != null) {
				const selectedResult = result.selectedResult
				await this.queryForLink(result.mediaName, selectedResult)
			}
		}
		this.$emit("finalized")
	}

	private async addUnsavedMedia(): Promise<void> {
		for (const result of this.queryResults) {
			const selectedResult = result.selectedResult
			if (selectedResult != null && !selectedResult.saved) {
				const { externalMediaAdd } = await Gql("mutation")({
					externalMediaAdd: [
						{
							db: selectedResult.extDb,
							code: selectedResult.extDbCode
						},
						true
					]
				})
				if (!externalMediaAdd) {
					throw new Error("Adding media " + selectedResult.name + " has failed")
				}
			}
		}
	}

	private async queryForLink(mediaName: MediaName, result: QueryResult): Promise<void> {
		const response = await Gql("query")({
			internalMediaLookup: [
				{
					db: result.extDb,
					code: result.extDbCode
				},
				{
					id: true,
					name: true,
					seasonNumber: true,
					children: {
						id: true,
						episodeNumber: true,
						name: true,
					}
				}
			]
		})
		const match = response.internalMediaLookup
		if (match == null) {
			throw new Error("Unable to find match for " + result.extDbCode)
		} else {
			return this.linkCollection(mediaName, match)
		}
	}

	private async linkCollection(mediaName: MediaName, partCollection: MediaPartCollection): Promise<void> {
		for (const file of mediaName.files) {
			if (file.seasonNumber == partCollection.seasonNumber) {
				const episode = partCollection.children
					.find(episode => episode.episodeNumber == file.episodeNumber)
				if (episode !== undefined) {
					await this.linkEpisode(file.fileName, episode.id)
				}
			} else {
				throw new Error("Unable to link " + file.fileName + " on " + partCollection.name + 
					" season " + partCollection.seasonNumber + ", season number does not match")
			}
		}
	}

	private async linkEpisode(fileName: string, mediaPartId: number): Promise<void> {
		const { linkVideoFileWithName } = await Gql("mutation")({
			linkVideoFileWithName: [
				{
					mediaFileName: fileName,
					mediaPartId: mediaPartId
				},
				true
			]
		})
		const success = linkVideoFileWithName
		if (!success) {
			throw new Error("Link failed for " + fileName + " on " + mediaPartId)
		}
	}

	private groupBy<TKey, TItem>(list: TItem[], keyGetter: (item: TItem) => TKey): Map<TKey, TItem[]> {
		const map = new Map()
		list.forEach((item) => {
			const key = keyGetter(item)
			const collection = map.get(key)
			if (!collection) {
				map.set(key, [item])
			} else {
				collection.push(item)
			}
		})
		return map
	}
}
</script>
