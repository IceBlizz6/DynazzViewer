<template>
	<article class="media-search-root">
		<h1>Media search</h1>
		<input type="text" v-model="searchText" placeholder="media name" v-on:keyup.enter="runSearch" />
		<button v-on:click="runSearch">Search</button>
		<p>{{ searchStatus }}</p>
		<section class="media-search-results">
			<ul class="media-search-result-list">
				<li v-for="item in searchResults" :key="item.inner.extDbCode" class="search-result-item">
					<div class="result-item-img">
						<img :src="item.inner.imageUrl" />
					</div>
					<div class="result-item-info">
						<p><strong>{{ item.inner.name }}</strong></p>
						<dl>
							<dt>Source</dt>
							<dd>{{ item.inner.extDb }}</dd>
							<dt>Code</dt>
							<dd>{{ item.inner.extDbCode }}</dd>
						</dl>
					</div>
					<button v-if="item.state == stateNew" class="result-item-action" v-on:click="addOrUpdate(item)">Add</button>
					<button v-if="item.state == stateSaved" class="result-item-action" v-on:click="addOrUpdate(item)">Update</button>
					<button v-if="item.state == stateSaving" class="result-item-action" disabled>Saving...</button>
				</li>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import graphClient from '@/lib/graph-client'
import { ExtDatabase, MediaSearchResultItem } from '@/graph/schema'

enum ResultItemStatus {
	NEW,
	SAVED,
	SAVING
}

class ResultHeaderItem {
	public inner: MediaSearchResultItem
	public state: ResultItemStatus

	public constructor(inner: MediaSearchResultItem) {
		this.inner = inner

		if (inner.saved) {
			this.state = ResultItemStatus.SAVED
		} else {
			this.state = ResultItemStatus.NEW
		}
	}
}

@Component
export default class MediaSearchView extends Vue {
	public searchText = ""
	public searchStatus = "Ready"
	public searchResults: ResultHeaderItem[] = []

	public stateNew = ResultItemStatus.NEW
	public stateSaved = ResultItemStatus.SAVED
	public stateSaving = ResultItemStatus.SAVING

	public runSearch(): void {
		if (this.searchText.length == 0) {
			this.searchStatus = "Unable to start search, empty query"
		} else {
			this.searchResults = []
			this.searchStatus = "Searching..."
			this.query()
		}
	}

	public addOrUpdate(item: ResultHeaderItem): void {
		item.state = ResultItemStatus.SAVING

		graphClient.mutation(
			{
				externalMediaAdd: [{ db: item.inner.extDb, code: item.inner.extDbCode }]
			},
			data => {
				const success: boolean = data.externalMediaAdd
				if (success) {
					item.state = ResultItemStatus.SAVED
				} else {
					item.state = ResultItemStatus.NEW
				}

			}
		)
	}

	private query(): void {
		graphClient.query(
			{
				externalMediaSearch: [
					{ 
						db: ExtDatabase.MyAnimeList, 
						name: this.searchText
					}, 
					{
						name: 1,
						extDb: 1,
						extDbCode: 1,
						imageUrl: 1,
						saved: 1
					}
				]
			},
			data => {
				this.searchStatus = "Search complete"
				const rawResults = data.externalMediaSearch
				this.searchResults = rawResults.map(el => new ResultHeaderItem(el))
			}
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
	background-color: aliceblue;
	margin: 10px;
	padding: 10px;
	border-style: ridge;
	border-color: gray;
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
