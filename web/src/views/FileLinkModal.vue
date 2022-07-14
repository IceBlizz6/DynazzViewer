<template>
	<article class="modal-body">
		<section v-if="state.modalState == 1">
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
			<o-button
				class="next-button"
				@click="moveToQuerySelection"
			>
				NEXT
			</o-button>
		</section>

		<section v-if="state.modalState == 2">
			<h1>Link to media</h1>
			<hr>
			<ul>
				<li
					v-for="mediaName in state.mediaNames"
					:key="mediaName.mediaName"
				>
					{{ mediaName.mediaName }} ({{ mediaName.files.length }} files)
				</li>
			</ul>
			<template v-if="!state.querying">
				<o-button @click="useMyLibrary">
					My library
				</o-button>
				<o-button @click="useMyAnimeList">
					MyAnimeList
				</o-button>
			</template>
		</section>

		<section v-if="state.modalState == 3">
			<h1>Verify result</h1>
			<hr>
			<ul>
				<li
					v-for="result in state.queryResults"
					:key="result.mediaName.mediaName"
				>
					{{ result.mediaName.mediaName }}
					<o-select v-model="result.selectedResult">
						<option
							v-for="(opt, index) in result.results"
							:key="index"
							:value="opt"
						>
							{{ opt.name }} ({{ opt.extDbCode }}:{{ opt.extDbCode }})
						</option>
					</o-select>
				</li>
			</ul>
			<o-button @click="finalizeSelection">
				Finalize
			</o-button>
		</section>
	</article>
</template>

<script setup lang="ts">
import { reactive } from "vue"
import { FileLinkRow } from "@/lib/FileLinkRow"
import { ExtDatabase } from '@/zeus'
import { graphClient } from "@/lib/GraphClient"

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

interface Props {
	detectedFileResults: FileLinkRow[]
}
const props = defineProps<Props>()

class State {
	public modalState: number = 1
	public mediaNames: MediaName[] = []
	public querying = false
	public queryResults: SearchResult[] = []
}
const state = reactive(new State())

interface Emits {
	(event: "finalized"): void
}
const emit = defineEmits<Emits>()

function moveToQuerySelection(): void {
	state.mediaNames = []
	const resultWithName = props.detectedFileResults.filter(e => e.mediaName != null)
	const map = groupBy<string, FileLinkRow>(resultWithName, e => {
		if (e.mediaName == null) {
			throw new Error("Media name cannot be empty")
		} else {
			return e.mediaName
		}
	})
	for (const [key, value] of map.entries()) {
		state.mediaNames.push({ mediaName: key, files: value })
	}
	state.modalState = 2
}

function useMyLibrary(): void {
	state.querying = true
	const promises = state.mediaNames.map(e => internalMediaSearch(e))
	Promise.all(promises).then(
		results => {
			handleQueryResults(results)
		}
	)
}

function useMyAnimeList(): void {
	state.querying = true
	const promises = state.mediaNames.map(e => myAnimeListSearch(e))
	Promise.all(promises).then(
		results => {
			handleQueryResults(results)
		}
	)
}

async function internalMediaSearch(mediaName: MediaName): Promise<SearchResult> {
	const response = await graphClient.query({
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

async function myAnimeListSearch(mediaName: MediaName): Promise<SearchResult> {
	const response = await graphClient.query({
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

function handleQueryResults(results: SearchResult[]): void {
	state.queryResults = results
	state.modalState = 3
	state.querying = false
}

async function finalizeSelection(): Promise<void> {
	await addUnsavedMedia()
	for (const result of state.queryResults) {
		if (result.selectedResult != null) {
			const selectedResult = result.selectedResult
			await queryForLink(result.mediaName, selectedResult)
		}
	}
	emit("finalized")
}

async function queryForLink(mediaName: MediaName, result: QueryResult): Promise<void> {
	const response = await graphClient.query({
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
		for (const file of mediaName.files) {
			if (file.seasonNumber == match.seasonNumber) {
				const episode = match.children
					.find(episode => episode.episodeNumber == file.episodeNumber)
				if (episode !== undefined) {
					await linkEpisode(file.fileName, episode.id)
				}
			} else {
				throw new Error("Unable to link " + file.fileName + " on " + match.name + 
					" season " + match.seasonNumber + ", season number does not match")
			}
		}
	}
}

async function addUnsavedMedia(): Promise<void> {
	for (const result of state.queryResults) {
		const selectedResult = result.selectedResult
		if (selectedResult != null && !selectedResult.saved) {
			const { externalMediaAdd } = await graphClient.mutation({
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

async function linkEpisode(fileName: string, mediaPartId: number): Promise<void> {
	const { linkVideoFileWithName } = await graphClient.mutation({
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

function groupBy<TKey, TItem>(list: TItem[], keyGetter: (item: TItem) => TKey): Map<TKey, TItem[]> {
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
</script>
