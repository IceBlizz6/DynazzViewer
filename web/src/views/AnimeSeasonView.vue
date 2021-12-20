<template>
	<article class="anime-parent-section">
		<section>
			<label>Year</label>
			<input
				v-model="yearInput"
				type="number"
				placeholder="year"
			>

			<label>Season</label>
			<select v-model="seasonInput">
				<option :value="seasonWinter">
					Winter
				</option>
				<option :value="seasonSpring">
					Spring
				</option>
				<option :value="seasonSummer">
					Summer
				</option>
				<option :value="seasonFall">
					Fall
				</option>
			</select>
			<button @click="addAnimeSeason">
				Add
			</button>
		</section>
		<hr>
		<section>
			<ul>
				<li
					v-for="seasonHeader in seasonHeaders"
					:key="`${seasonHeader.year}-${seasonHeader.season}`"
					class="seasonHeader"
					@click="selectHeader(seasonHeader)"
				>
					{{ seasonHeader.year }} - {{ seasonHeader.season }}
				</li>
			</ul>
		</section>
		<hr>
		<section v-if="selected != null">
			<h1>{{ selected.year }} - {{ selected.season }}</h1>
			<div>
				<input
					v-model="enableWatch"
					type="checkbox"
				>
				<label>Watch</label>
				|
				<input
					v-model="enableSkip"
					type="checkbox"
				>
				<label>Skip</label>
				|
				<input
					v-model="enableNone"
					type="checkbox"
				>
				<label>None</label>
			</div>
			<ul class="selectedSeriesList">
				<li
					v-for="series in seriesFilteredList"
					:key="series.malId" 
					class="seriesItem"
				>
					<a
						class="seriesTitle" 
						target="_blank" 
						:href="series.url"
					>{{ series.title }}</a>
					<img
						class="seriesImage"
						:src="series.imageUrl"
					>
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
						<button @click="flagWatch(series)">
							Watch
						</button>
						<button @click="flagSkip(series)">
							Skip
						</button>
						<button @click="flagNone(series)">
							Clear
						</button>
					</div>
					<div>
						<p v-if="series.saved == true">
							<strong>[In library]</strong>
						</p>
						<button 
							v-if="series.saved == false"
							@click="addMediaSeries(series)"
						>
							Add to library
						</button>
					</div>
				</li>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Vue } from 'vue-property-decorator'
import { AnimeSeasonFlagState, MalYearSeason, ExtDatabase } from '@/zeus'

export default class AnimeSeasonView extends Vue {
	private seasonHeaders: MalSeasonIdentifier[] = []
	private selected: MalSeasonIdentifier | null = null
	private selectedSeriesList: AnimeSeasonSeries[] = []

	private enableWatch = false
	private enableSkip = false
	private enableNone = false

	private seasonWinter = MalYearSeason.WINTER
	private seasonSpring = MalYearSeason.SPRING
	private seasonSummer = MalYearSeason.SUMMER
	private seasonFall = MalYearSeason.FALL

	private yearInput: number | null = null
	private seasonInput: MalYearSeason = MalYearSeason.WINTER

	public mounted(): void {
		this.refreshHeaders()
	}

	private refreshHeaders(): void {
		graphClient.query(
			{
				animeSeasonList: {
					year: 1,
					season: 1,
				}
			},
			data => {
				const seasonList = data.animeSeasonList
				this.seasonHeaders = seasonList
			}
		)
	}

	private querySelectedSeries(item: MalSeasonIdentifier): void {
		graphClient.query(
			{
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
			},
			data => {
				const results = data.animeSeason
				this.selectedSeriesList = results
			}
		)
	}

	private flagWatch(item: AnimeSeasonSeries): void {
		this.setFlagState(item, AnimeSeasonFlagState.Watch)
	}

	private flagSkip(item: AnimeSeasonSeries): void {
		this.setFlagState(item, AnimeSeasonFlagState.Skip)
	}

	private flagNone(item: AnimeSeasonSeries): void {
		this.setFlagState(item, AnimeSeasonFlagState.None)
	}

	private setFlagState(item: AnimeSeasonSeries, state: AnimeSeasonFlagState): void {
		graphClient.mutation(
			{
				animeSeasonMark: [{ malId: item.malId, flag: state }]
			},
			data => {
				const success = data.animeSeasonMark
				if (success) {
					item.flag = state
				}
			}
		)
	}

	private get seriesFilteredList(): AnimeSeasonSeries[] {
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

	private selectHeader(item: MalSeasonIdentifier): void {
		this.selectedSeriesList = []
		if (this.selected == item) {
			this.selected = null
		} else {
			this.selected = item
			this.querySelectedSeries(item)
		}
	}

	private addAnimeSeason(): void {
		if (this.yearInput == null) {
			console.error("Missing year")
		} else {
			graphClient.mutation(
				{
					animeSeasonAdd: [
						{ year: this.yearInput, season: this.seasonInput }
					]
				},
				data => {
					const success = data.animeSeasonAdd
					if (success) {
						this.selected = null
						this.selectedSeriesList = []
						this.refreshHeaders()
					}
				}
			)
		}
	}

	private addMediaSeries(item: AnimeSeasonSeries): void {
		graphClient.mutation(
			{
				externalMediaAdd: [{ db: ExtDatabase.MyAnimeList, code: item.malId.toString() }]
			},
			data => {
				const success: boolean = data.externalMediaAdd
				if (success) {
					item.saved = true
				}
			}
		)
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
