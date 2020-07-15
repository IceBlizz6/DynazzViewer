<template>
	<article class="anime-parent-section">
		<section>
			<label>Year</label>
			<input type="number" placeholder="year" v-model="yearInput" />

			<label>Season</label>
			<select v-model="seasonInput">
				<option :value="seasonWinter">Winter</option>
				<option :value="seasonSpring">Spring</option>
				<option :value="seasonSummer">Summer</option>
				<option :value="seasonFall">Fall</option>
			</select>
			<button v-on:click="addAnimeSeason">Add</button>
		</section>
		<hr>
		<section>
			<ul>
				<li class="seasonHeader"
					v-for="seasonHeader in seasonHeaders"
					:key="`${seasonHeader.year}-${seasonHeader.season}`"
					v-on:click="selectHeader(seasonHeader)"
				>
					{{ seasonHeader.year }} - {{ seasonHeader.season }}
				</li>
			</ul>
		</section>
		<hr>
		<section v-if="selected != null">
			<h1>{{ selected.year }} - {{ selected.season }}</h1>
			<div>
				<input type="checkbox" v-model="enableWatch" />
				<label>Watch</label>
				|
				<input type="checkbox" v-model="enableSkip" />
				<label>Skip</label>
				|
				<input type="checkbox" v-model="enableNone" />
				<label>None</label>
			</div>
			<ul class="selectedSeriesList">
				<li class="seriesItem" v-for="series in seriesFilteredList" 
					:key="series.malId"
				>
					<a class="seriesTitle" 
						target="_blank" 
						:href="series.url">{{ series.title }}</a>
					<img class="seriesImage" :src="series.imageUrl" />
					<dl class="seriesInfo">
						<dt>ID</dt>
						<dd>{{ series.malId }}</dd>
						<dt>Episodes</dt>
						<dd>{{ series.episodes }}</dd>
						<dt>Score</dt>
						<dd>{{ series.score }}</dd>
						<dt>Type</dt>
						<dd>{{ series.type }}</dd>
						<dt>Flag</dt>
						<dd>{{ series.flag }}</dd>
					</dl>
					<div class="seriesAction">
						<button v-on:click="flagWatch(series)">Watch</button>
						<button v-on:click="flagSkip(series)">Skip</button>
						<button v-on:click="flagNone(series)">Clear</button>
					</div>
					<div>
						<p v-if="series.saved == true"><strong>[In library]</strong></p>
						<button 
							v-on:click="addMediaSeries(series)"
							v-if="series.saved == false">Add to library</button>
					</div>
				</li>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Component, Prop,  Vue } from 'vue-property-decorator'
import { MalSeasonIdentifier, AnimeSeasonSeries, AnimeSeasonFlagState, MalYearSeason, ExtDatabase } from '@/graph/schema'
import graphClient from '@/lib/graph-client'

class AnimeSeason {

}

@Component
export default class AnimeSeasonView extends Vue {
	public seasonHeaders: MalSeasonIdentifier[] = []
	public selected: MalSeasonIdentifier | null = null
	public selectedSeriesList: AnimeSeasonSeries[] = []

	public enableWatch = false
	public enableSkip = false
	public enableNone = false

	public seasonWinter = MalYearSeason.WINTER
	public seasonSpring = MalYearSeason.SPRING
	public seasonSummer = MalYearSeason.SUMMER
	public seasonFall = MalYearSeason.FALL

	public yearInput: number | null = null
	public seasonInput: MalYearSeason = MalYearSeason.WINTER

	mounted() {
		this.refreshHeaders()
	}

	refreshHeaders() {
		graphClient.query({
			animeSeasonList: {
				year: 1,
				season: 1,
			}
		}).then(response => {
			const seasonList = response.data!.animeSeasonList
			this.seasonHeaders = seasonList
		})
	}

	querySelectedSeries(item: MalSeasonIdentifier) {
		graphClient.query({
			animeSeason: [
				{ 
					year: item.year, 
					season: item.season 
				}, 
				{
					malId: 1,
					title: 1,
					flag: 1,
					imageUrl: 1,
					url: 1,
					episodes: 1,
					score: 1,
					type: 1,
					saved: 1
				}
			]
		}).then(response => {
			const results = response.data!.animeSeason
			this.selectedSeriesList = results
		})
	}

	flagWatch(item: AnimeSeasonSeries) {
		this.setFlagState(item, AnimeSeasonFlagState.Watch)
	}

	flagSkip(item: AnimeSeasonSeries) {
		this.setFlagState(item, AnimeSeasonFlagState.Skip)
	}

	flagNone(item: AnimeSeasonSeries) {
		this.setFlagState(item, AnimeSeasonFlagState.None)
	}

	private setFlagState(item: AnimeSeasonSeries, state: AnimeSeasonFlagState) {
		graphClient.mutation({
			animeSeasonMark: [{ malId: item.malId, flag: state }]
		}).then(response => {
			const success = response.data!.animeSeasonMark
			if (success) {
				item.flag = state
			}
		})
	}

	get seriesFilteredList(): AnimeSeasonSeries[] {
		const enableWatch = this.enableWatch
		const enableSkip = this.enableSkip
		const enableNone = this.enableNone

		return this.selectedSeriesList.filter(el => {
			if (!enableWatch && !enableSkip && !enableNone) {
				return true
			} else if (enableWatch && enableSkip && enableNone) {
				return true
			} else if (enableWatch && el.flag == AnimeSeasonFlagState.Watch) {
				return true
			} else if (enableSkip && el.flag == AnimeSeasonFlagState.Skip) {
				return true
			} else if (enableNone && el.flag == AnimeSeasonFlagState.None) {
				return true
			} else {
				return false
			}
		})
	}

	selectHeader(item: MalSeasonIdentifier) {
		this.selectedSeriesList = []
		if (this.selected == item) {
			this.selected = null
		} else {
			this.selected = item
			this.querySelectedSeries(item)
		}
	}

	addAnimeSeason() {
		if (this.yearInput == null) {
			console.error("Missing year")
		} else {
			graphClient.mutation({
				animeSeasonAdd: [{ year: this.yearInput, season: this.seasonInput }]
			}).then(response => {
				const success = response.data!.animeSeasonAdd
				if (success) {
					this.selected = null
					this.selectedSeriesList = []
					this.refreshHeaders()
				}
			})
		}
	}

	addMediaSeries(item: AnimeSeasonSeries) {
		graphClient.mutation({
			externalMediaAdd: [{ db: ExtDatabase.MyAnimeList, code: item.malId.toString() }]
		}).then(response => {
			const success: boolean = response.data!.externalMediaAdd
			if (success) {
				item.saved = true
			}
		})
	}
}
</script>

<style scoped>
.anime-parent-section {
	margin-top: 45px;
	margin-left: 15px;
}

.seasonHeader {
	background-color: aliceblue;
}

.seasonHeader:hover {
	background-color: lightblue;
}

.selectedSeriesList {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 500px));
}

.seriesItem {
	background-color: whitesmoke;
	border-style: solid;
	display: grid;
	grid-template-columns: 50% 45%;
	grid-template-rows: 20% auto auto;
	column-gap: 15px;
	padding-bottom: 40px;
	padding-left: 15px;
	padding-top: 15px;
	margin: 10px;
}

.seriesTitle {
	grid-column: 2;
	grid-row: 1;
}

.seriesImage {
	grid-column: 1;
	grid-row-start: 1;
	grid-row-end: 3;
}

.seriesInfo {
	grid-column: 2;
	grid-row-start: 2;
	grid-row-end: 3;
}

.seriesAction {
	grid-column: 1;
	grid-row: 3;
}

dt {
	font-weight: bold;
	text-decoration: underline;
}

dd {
	margin: 0;
	padding: 0 0 0.5em 0;
}
</style>
