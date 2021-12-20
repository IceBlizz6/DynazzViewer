<template>
	<article class="anime-parent-section">
		<section>
			<o-field label="Year">
				<o-input
					v-model="yearInput"
					type="number"
					placeholder="year"
				/>
			</o-field>

			<o-field label="Season">
				<o-select v-model="seasonInput">
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
				</o-select>
			</o-field>

			<o-button @click="addAnimeSeason">
				Add
			</o-button>
		</section>
		<hr>
		<section>
			<ul>
				<li
					v-for="seasonHeader in seasonHeaders"
					:key="`${seasonHeader.year}-${seasonHeader.season}`"
					class="season-header"
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
				<o-checkbox
					v-model="enableWatch"
					type="checkbox"
				>
					Watch
				</o-checkbox>
				|
				<o-checkbox
					v-model="enableSkip"
					type="checkbox"
				>
					Skip
				</o-checkbox>
				|
				<o-checkbox
					v-model="enableNone"
					type="checkbox"
				>
					None
				</o-checkbox>
			</div>
			<ul class="selected-series-list">
				<li
					v-for="series in seriesFilteredList"
					:key="series.malId" 
					class="series-item"
				>
					<a
						class="series-title" 
						target="_blank" 
						:href="series.url"
					>{{ series.title }}</a>
					<img
						class="series-image"
						:src="series.imageUrl"
					>
					<dl class="series-info">
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
					<div class="series-action">
						<o-button @click="flagWatch(series)">
							Watch
						</o-button>
						<o-button @click="flagSkip(series)">
							Skip
						</o-button>
						<o-button @click="flagNone(series)">
							Clear
						</o-button>
					</div>
					<div>
						<p v-if="series.saved == true">
							<strong>[In library]</strong>
						</p>
						<o-button 
							v-if="series.saved == false"
							@click="addMediaSeries(series)"
						>
							Add to library
						</o-button>
					</div>
				</li>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Vue } from 'vue-property-decorator'
import { AnimeSeasonFlagState, MalYearSeason, ExtDatabase, Gql } from '@/zeus'
import queries, { AnimeSeasonSeries, MalSeasonIdentifier } from "@/lib/Queries"

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

	private async refreshHeaders(): Promise<void> {
		const seasonList = await queries.animeSeasonList()
		this.seasonHeaders = seasonList.animeSeasonList
	}

	private async querySelectedSeries(item: MalSeasonIdentifier): Promise<void> {
		const { animeSeason } = await queries.animeSeasonSeries(item.year, item.season)
		this.selectedSeriesList = animeSeason
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

	private async setFlagState(item: AnimeSeasonSeries, state: AnimeSeasonFlagState): Promise<void> {
		const data = await Gql("mutation")({
			animeSeasonMark: [
				{ malId: item.malId, flag: state },
				true
			]
		})
		const success = data.animeSeasonMark
		if (success) {
			item.flag = state
		}
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

	private async addAnimeSeason(): Promise<void> {
		if (this.yearInput == null) {
			console.error("Missing year")
		} else {
			const { animeSeasonAdd } = await Gql("mutation")({
				animeSeasonAdd: [
					{ year: this.yearInput, season: this.seasonInput },
					true
				]
			})
			const success = animeSeasonAdd
			if (success) {
				this.selected = null
				this.selectedSeriesList = []
				this.refreshHeaders()
			}
		}
	}

	private async addMediaSeries(item: AnimeSeasonSeries): Promise<void> {
		const { externalMediaAdd } = await Gql("mutation")({
			externalMediaAdd: [
				{
					db: ExtDatabase.MyAnimeList,
					code: item.malId.toString(),
				},
				true
			]
		})
		const success: boolean = externalMediaAdd
		if (success) {
			item.saved = true
		}
	}
}
</script>

<style scoped>
.anime-parent-section {
	margin-top: 45px;
	margin-left: 15px;
}

.season-header {
	background-color: var(--primary-invert);
}

.season-header:hover {
	background-color: var(--primary-invert-highlight);
}

.selected-series-list {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 500px));
}

.series-item {
	background-color: var(--primary-invert);
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

.series-title {
	grid-column: 2;
	grid-row: 1;
}

.series-image {
	grid-column: 1;
	grid-row: 1 / 3;
}

.series-info {
	grid-column: 2;
	grid-row: 2 / 3;
}

.series-action {
	grid-column: 1;
	grid-row: 3;
}

dt {
	font-weight: bold;
	text-decoration: underline;
}

dd {
	margin: 0;
	padding: 0 0 0.5em;
}
</style>
