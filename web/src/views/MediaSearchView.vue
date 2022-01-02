<template>
	<article class="media-search-root">
		<h1>Media search</h1>
		<o-input
			v-model="searchText"
			type="text"
			placeholder="name..."
			@keyup.enter="runSearch"
		/>
		<div class="block">
			<o-radio v-model="apiSelection" :native-value="apiAnime">
				MyAnimeList
			</o-radio>
			<o-radio v-model="apiSelection" :native-value="apiImdb">
				Imdb
			</o-radio>
		</div>
		<o-button @click="runSearch">
			Search
		</o-button>
		<p>{{ searchStatus }}</p>
		<section class="media-search-results">
			<ul class="media-search-result-list">
				<li
					v-for="item in searchResults"
					:key="item.extDbCode"
					class="search-result-item"
				>
					<div class="result-item-img">
						<img :src="item.imageUrl">
					</div>
					<div class="result-item-info">
						<p><strong>{{ item.name }}</strong></p>
						<dl>
							<dt>Source</dt>
							<dd>{{ item.extDb }}</dd>
							<dt>Code</dt>
							<dd>{{ item.extDbCode }}</dd>
						</dl>
					</div>
					<o-button
						v-if="item.state == stateNew"
						class="result-item-action"
						@click="addOrUpdate(item)"
					>
						Add
					</o-button>
					<o-button
						v-if="item.state == stateSaved"
						class="result-item-action"
						@click="addOrUpdate(item)"
					>
						Update
					</o-button>
					<o-button
						v-if="item.state == stateSaving"
						class="result-item-action"
						disabled
					>
						Saving...
					</o-button>
				</li>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Vue } from 'vue-property-decorator'
import { ExtDatabase, Gql } from '@/zeus'

enum ResultItemStatus {
	NEW,
	SAVED,
	SAVING
}

class ResultHeaderItem {
	public state: ResultItemStatus

	public constructor(
		saved: boolean,
		public extDb: ExtDatabase,
		public extDbCode: string,
		public imageUrl: string,
		public name: string,
	) {
		if (saved) {
			this.state = ResultItemStatus.SAVED
		} else {
			this.state = ResultItemStatus.NEW
		}
	}
}

export default class MediaSearchView extends Vue {
	public searchText = ""
	public searchStatus = "Ready"
	public searchResults: ResultHeaderItem[] = []

	public stateNew = ResultItemStatus.NEW
	public stateSaved = ResultItemStatus.SAVED
	public stateSaving = ResultItemStatus.SAVING

	private apiAnime = ExtDatabase.MyAnimeList
	private apiImdb = ExtDatabase.TvMaze
	private apiSelection = this.apiAnime

	public runSearch(): void {
		if (this.searchText.length == 0) {
			this.searchStatus = "Unable to start search, empty query"
		} else {
			this.searchResults = []
			this.searchStatus = "Searching..."
			this.query()
		}
	}

	public async addOrUpdate(item: ResultHeaderItem): Promise<void> {
		item.state = ResultItemStatus.SAVING
		const { externalMediaAdd } = await Gql("mutation")({
			externalMediaAdd: [
				{
					db: item.extDb,
					code: item.extDbCode
				},
				true
			]
		})
		if (externalMediaAdd) {
			item.state = ResultItemStatus.SAVED
		} else {
			item.state = ResultItemStatus.NEW
		}
	}

	private async query(): Promise<void> {
		const response = await Gql("query")({
			externalMediaSearch: [
				{
					db: this.apiSelection, 
					name: this.searchText
				},
				{
					name: true,
					extDb: true,
					extDbCode: true,
					imageUrl: true,
					saved: true
				}
			]
		})
		this.searchStatus = "Search complete"
		const rawResults = response.externalMediaSearch
		this.searchResults = rawResults.map(
			el => new ResultHeaderItem(
				el.saved,
				el.extDb,
				el.extDbCode,
				el.imageUrl,
				el.name
			)
		)
	}
}
</script>

<style>
.media-search-root {
	margin-left: 25px;
}

.media-search-result-list {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 500px));
}

.search-result-item {
	background-color: var(--primary-invert);
	margin: 10px;
	padding: 10px;
	border-style: ridge;
	border-color: var(--primary-invert-highlight);
	display: grid;
	grid-template-columns: 50% 50%;
	grid-template-rows: auto 30px;
	row-gap: 10px;
	column-gap: 10px;
}

.result-item-img {
	grid-column: 1;
	grid-row: 1;
	margin-left: auto;
	margin-right: auto;
}

.result-item-info {
	grid-column: 2;
	grid-row: 1;
}

.result-item-action {
	grid-column: 1;
	grid-row: 2;
}
</style>
