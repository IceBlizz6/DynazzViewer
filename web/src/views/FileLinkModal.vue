<template>
	<article class="modal-body">
		<section v-if="modalState == 1">
			<h1>Detected media names</h1>
			<table>
				<tr>
					<th></th>
					<th>File name</th>
					<th>Media name</th>
					<th>S</th>
					<th>Ep</th>
				</tr>
				<tr v-for="result in detectedFileResults" :key="result.fileName">
					<td>
						<input type="checkbox" :checked="result.mediaName != null" />
					</td>
					<td>{{ result.fileName }}</td>
					<td>
						<input type="text" :value="result.mediaName" />
					</td>
					<td>
						<input type="number" :value="result.seasonNumber" />
					</td>
					<td>
						<input type="number" :value="result.episodeNumber" />
					</td>
				</tr>
			</table>
			<b-button class="next-button" @click="moveToQuerySelection">NEXT</b-button>
		</section>

		<section v-if="modalState == 2">
			<h1>Link to media</h1>
			<hr>
			<ul>
				<li v-for="mediaName in mediaNames" :key="mediaName.mediaName">
					{{ mediaName.mediaName }} ({{ mediaName.files.length }} files)
				</li>
			</ul>
			<template v-if="!querying">
				<b-button @click="useMyLibrary">My library</b-button>
				<b-button @click="useMyAnimeList">MyAnimeList</b-button>
			</template>
			
		</section>

		<section v-if="modalState == 3">
			<h1>Verify result</h1>
			<hr>
			<ul>
				<li v-for="result in queryResults" :key="result.mediaName.mediaName">
					{{ result.mediaName.mediaName }}
					<b-select v-model="result.selectedResult">
						<option v-for="(opt, index) in result.results" :key="index" :value="opt">
							{{ opt.name }} ({{ opt.extDbCode }}:{{ opt.extDbCode }})
						</option>
					</b-select>
				</li>
			</ul>
			<b-button @click="finalizeSelection">Finalize</b-button>
		</section>
	</article>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import { FileLinkRow } from "@/lib/FileLinkRow"
import graphClient from '@/lib/graph-client'
import { ExtDatabase, MediaPartCollection } from '@/graph/schema'
import { PromiseQueue } from '@/lib/PromiseQueue'

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

@Component
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

	private internalMediaSearch(mediaName: MediaName): Promise<SearchResult> {
		return graphClient.queryWithReturn(
			{
				internalMediaSearch: [
					{
						name: mediaName.mediaName
					},
					{
						id: 1,
						name: 1,
						databaseEntry: {
							mediaDatabase: 1,
							code: 1,
						}
					}
				],
			},
			data => {
				const results = data.internalMediaSearch.map(result => {
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
		)
	}

	private myAnimeListSearch(mediaName: MediaName): Promise<SearchResult> {
		return graphClient.queryWithReturn(
			{
				externalMediaSearch: [
					{
						name: mediaName.mediaName,
						db: ExtDatabase.MyAnimeList
					},
					{
						name: 1,
						saved: 1,
						extDb: 1,
						extDbCode: 1,
					}
				]
			},
			data => {
				const results = data.externalMediaSearch.map(result => {
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
		)
	}

	private handleQueryResults(results: SearchResult[]): void {
		this.queryResults = results
		this.modalState = 3
		this.querying = false
	}

	private finalizeSelection(): void {
		this.addUnsavedMedia().then(
			success => {
				if (success) {
					const queue = new PromiseQueue()
					for (const result of this.queryResults) {
						if (result.selectedResult != null) {
							const selectedResult = result.selectedResult
							queue.add(() => this.queryForLink(result.mediaName, selectedResult))
						}
					}
					queue.run()
					this.$emit("finalized")
				}
			}
		)
		
	}

	private addUnsavedMedia(): Promise<boolean> {
		const queue = new PromiseQueue()
		for (const result of this.queryResults) {
			const selectedResult = result.selectedResult
			if (selectedResult != null && !selectedResult.saved) {
				queue.add(
					() => {
						return graphClient.mutationWithReturn(
							{
								externalMediaAdd: [
									{
										db: selectedResult.extDb,
										code: selectedResult.extDbCode
									}
								]
							},
							data => {
								const success = data.externalMediaAdd
								if (!success) {
									throw new Error("Adding media " + selectedResult.name + " has failed")
								}
								return success
							}
						)
					}
				)
			}
		}
		return queue.run()
	}

	private queryForLink(mediaName: MediaName, result: QueryResult): Promise<boolean> {
		return graphClient.queryWithReturn(
			{
				internalMediaLookup: [
					{
						db: result.extDb,
						code: result.extDbCode
					},
					{
						id: 1,
						name: 1,
						seasonNumber: 1,
						children: {
							id: 1,
							episodeNumber: 1,
							name: 1,
						}
					}
				]
			},
			data => {
				const entry = data.internalMediaLookup
				if (entry == null) {
					throw new Error("Unable to find match for " + result.extDbCode)
				}
				return entry

			}
		).then(entry => {
			return this.linkCollection(mediaName, entry)
		})
	}

	private linkCollection(mediaName: MediaName, partCollection: MediaPartCollection): Promise<boolean> {
		const queue = new PromiseQueue()
		for (const file of mediaName.files) {
			if (file.seasonNumber == partCollection.seasonNumber) {
				const episode = partCollection.children
					.find(episode => episode.episodeNumber == file.episodeNumber)
				if (episode !== undefined) {
					queue.add(() => this.linkEpisode(file.fileName, episode.id))
				}
			} else {
				throw new Error("Unable to link " + file.fileName + " on " + partCollection.name + 
					" season " + partCollection.seasonNumber + ", season number does not match")
			}
		}
		return queue.run()
	}

	private linkEpisode(fileName: string, mediaPartId: number): Promise<boolean> {
		return graphClient.mutationWithReturn(
			{
				linkVideoFileWithName: [
					{
						mediaFileName: fileName,
						mediaPartId: mediaPartId
					}
				]
			},
			data => {
				if (!data.linkVideoFileWithName) {
					throw new Error("Link failed for " + fileName + " on " + mediaPartId)
				} else {
					console.log("Linked " + fileName + " on " + mediaPartId)
				}
				return data.linkVideoFileWithName
			}
		)
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
