<template>
	<article class="anime-parent-section">
		<section>
			<o-field label="Year">
				<o-input
					v-model="state.yearInput"
					type="number"
					placeholder="year"
				/>
			</o-field>

			<o-field label="Season">
				<o-select v-model="state.seasonInput">
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
					v-for="seasonHeader in orderedSeasonHeader"
					:key="`${seasonHeader.year}-${seasonHeader.season}`"
					class="season-header"
					@click="selectHeader(seasonHeader)"
				>
					{{ seasonHeader.year }} - {{ seasonHeader.season }}
				</li>
			</ul>
		</section>
		<hr>
		<section v-if="state.selected != null">
			<h1>{{ state.selected.year }} - {{ state.selected.season }}</h1>
			<div>
				<o-checkbox
					v-model="state.enableWatch"
					type="checkbox"
				>
					Watch
				</o-checkbox>
				|
				<o-checkbox
					v-model="state.enableMaybe"
					type="checkbox"
				>
					Maybe
				</o-checkbox>
				|
				<o-checkbox
					v-model="state.enableSkip"
					type="checkbox"
				>
					Skip
				</o-checkbox>
				|
				<o-checkbox
					v-model="state.enableNone"
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
						<o-button @click="flagMaybe(series)">
							Maybe
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

<script setup lang="ts">
import { computed, onMounted, reactive } from "vue"
import { AnimeSeasonFlagState, MalYearSeason, ExtDatabase } from '@/zeus'
import queries, { AnimeSeasonSeries, MalSeasonIdentifier } from "@/lib/Queries"
import moment from 'moment'
import { graphClient } from "@/lib/GraphClient"

const seasonWinter = MalYearSeason.WINTER
const seasonSpring = MalYearSeason.SPRING
const seasonSummer = MalYearSeason.SUMMER
const seasonFall = MalYearSeason.FALL

class State {
	public seasonHeaders: MalSeasonIdentifier[] = []
	public selected: MalSeasonIdentifier | null = null
	public selectedSeriesList: AnimeSeasonSeries[] = []

	public enableWatch = false
	public enableMaybe = false
	public enableSkip = false
	public enableNone = false

	public yearInput: string | null = null
	public seasonInput: MalYearSeason = MalYearSeason.WINTER
}
const state = reactive(new State())

function flagWatch(item: AnimeSeasonSeries): void {
	setFlagState(item, AnimeSeasonFlagState.Watch)
}

function flagSkip(item: AnimeSeasonSeries): void {
	setFlagState(item, AnimeSeasonFlagState.Skip)
}

function flagMaybe(item: AnimeSeasonSeries): void {
	setFlagState(item, AnimeSeasonFlagState.Maybe)
}

function flagNone(item: AnimeSeasonSeries): void {
	setFlagState(item, AnimeSeasonFlagState.None)
}

async function refreshHeaders(): Promise<void> {
	const seasonList = await queries.animeSeasonList()
	state.seasonHeaders = seasonList.animeSeasonList
}

async function setFlagState(item: AnimeSeasonSeries, state: AnimeSeasonFlagState): Promise<void> {
	const data = await graphClient.mutation({
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

function selectHeader(item: MalSeasonIdentifier): void {
	state.selectedSeriesList = []
	if (state.selected == item) {
		state.selected = null
	} else {
		state.selected = item
		querySelectedSeries(item)
	}
}

async function addAnimeSeason(): Promise<void> {
	if (state.yearInput == null) {
		console.error("Missing year")
	} else {
		const { animeSeasonAdd } = await graphClient.mutation({
			animeSeasonAdd: [
				{ year: parseInt(state.yearInput), season: state.seasonInput },
				true
			]
		})
		const success = animeSeasonAdd
		if (success) {
			state.selected = null
			state.selectedSeriesList = []
			refreshHeaders()
		}
	}
}

async function querySelectedSeries(item: MalSeasonIdentifier): Promise<void> {
	const { animeSeason } = await queries.animeSeasonSeries(item.year, item.season)
	state.selectedSeriesList = animeSeason
}

async function addMediaSeries(item: AnimeSeasonSeries): Promise<void> {
	const { externalMediaAdd } = await graphClient.mutation({
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

const orderedSeasonHeader = computed((): MalSeasonIdentifier[] => {
	const seasonOrder: MalYearSeason[] = [
		MalYearSeason.WINTER,
		MalYearSeason.SPRING,
		MalYearSeason.SUMMER,
		MalYearSeason.FALL
	]
	const copy = [ ...state.seasonHeaders ]
	return copy.sort(
		(a, b): number => {
			if (a.year != b.year) {
				return a.year - b.year
			} else {
				const aIndex = seasonOrder.indexOf(a.season) 
				const bIndex = seasonOrder.indexOf(b.season) 
				return aIndex - bIndex
			}
		}
	)
})

const seriesFilteredList = computed((): AnimeSeasonSeries[] => {
	return state.selectedSeriesList.filter(el => {
		if (!state.enableWatch && !state.enableSkip && !state.enableNone && !state.enableMaybe) {
			return true
		} else if (state.enableWatch && state.enableSkip && state.enableNone && state.enableMaybe) {
			return true
		} else if (state.enableWatch && el.flag == AnimeSeasonFlagState.Watch) {
			return true
		} else if (state.enableSkip && el.flag == AnimeSeasonFlagState.Skip) {
			return true
		} else if (state.enableNone && el.flag == AnimeSeasonFlagState.None) {
			return true
		} else if (state.enableMaybe && el.flag == AnimeSeasonFlagState.Maybe) {
			return true
		} else {
			return false
		}
	})
})

onMounted(() => {
	refreshHeaders()
	const now = moment()
	state.yearInput = now.year().toString()
})
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
