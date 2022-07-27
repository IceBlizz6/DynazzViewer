<template>
	<article>
		<article class="series-list">
			<MediaSeries
				v-for="mediaItem in state.source"
				:key="mediaItem.id"
				:source="mediaItem"
				@click="selectSeries(mediaItem)"
			/>
		</article>
		<o-modal v-model:active="state.activeModal">
			<article
				v-if="state.selected != null"
				class="series-modal"
			>
				<div class="series-modal-img">
					<img
						v-if="state.selected.images.length > 0"
						:src="state.selected.images[0].url"
					>
				</div>
				<span class="series-title">{{ state.selected.name }}</span>
				<div class="series-season-list">
					<section
						v-for="season in state.selected.children"
						:key="season.id"
						class="series-season"
					>
						<p>{{ season.name }}</p>
						<ul>
							<li
								v-for="episode in season.children"
								:key="episode.id"
								class="episode-item"
							>
								<img
									v-if="episode.status == stateNone"
									class="tree-icon"
									src="@/assets/videofiles/Neutral.png"
								>
								<img
									v-if="episode.status == stateViewed"
									class="tree-icon"
									src="@/assets/videofiles/Viewed.png"
								>
								<img
									v-if="episode.status == stateSkipped"
									class="tree-icon"
									src="@/assets/videofiles/Skipped.png"
								>
								<img
									v-if="isLinked(episode)"
									class="tree-icon"
									src="@/assets/Link.png"
								>

								{{ episodeFormat(episode.episodeNumber) }} | {{ episode.name }}
								<div class="toolbar">
									<span
										class="toolbar-action"
										@click="setEpisodeWatch(episode, stateNone)"
									>
										<img
											class="tree-icon"
											src="@/assets/videofiles/Neutral.png"
										>
										Undo
									</span>
									<span
										class="toolbar-action"
										@click="setEpisodeWatch(episode, stateViewed)"
									>
										<img
											class="tree-icon"
											src="@/assets/videofiles/Viewed.png"
										>
										Viewed
									</span>
									<span
										class="toolbar-action"
										@click="setEpisodeWatch(episode, stateSkipped)"
									>
										<img
											class="tree-icon"
											src="@/assets/videofiles/Skipped.png"
										>
										Skipped
									</span>
								</div>
							</li>
						</ul>
					</section>
				</div>
			</article>
		</o-modal>
	</article>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive } from "vue"
import { MediaUnitSort, SortOrder, ViewStatus } from '@/zeus'
import MediaSeries from '@/components/MediaSeries.vue'
import numeral from 'numeral'
import queries, { MediaUnit, MediaPart } from "@/lib/Queries"
import { graphClient } from "@/lib/GraphClient"
import { BatchRequester } from "@/lib/BatchRequester"

const mediaUnitBatchSize = 10
const loadOnScrollBottomPercent = 90

class State {
	public source: MediaUnit[] = []
	public selected: MediaUnit | null = null
	public activeModal = false
}
const state = reactive(new State())

const stateNone = ViewStatus.None
const stateViewed = ViewStatus.Viewed
const stateSkipped = ViewStatus.Skipped

function episodeFormat(value: number | undefined): string {
	if (value === undefined) {
		throw new Error("Not supported")
	} else {
		return "E" + numeral(value).format("00")
	}
}

function isLinked(mediaPart: MediaPart): boolean {
	return mediaPart.mediaFile != null
}

function selectSeries(item: MediaUnit): void {
	state.selected = item
	state.activeModal = true
}

async function setEpisodeWatch(episode: MediaPart, status: ViewStatus): Promise<void> {
	const response = await graphClient.mutation(
		{
			setEpisodeWatchState: [
				{
					mediaPartId: episode.id,
					status: status
				},
				true
			]
		}
	)
	const success = response
	if (success) {
		episode.status = status
	} else {
		throw new Error("Operation failed")
	}
}

async function requestBatch(skip: number, take: number): Promise<MediaUnit[]> {
	const response = await queries.listMediaUnits(skip, take, SortOrder.DESCENDING, MediaUnitSort.LAST_EPISODE_AIRED)
	return response.listMediaUnits
}

const batchRequester = new BatchRequester<MediaUnit>(
	mediaUnitBatchSize,
	requestBatch,
	(list) => {
		state.source.push(...list)
	}
)

function onScroll(): void {
	const scrollingElement = document.scrollingElement
	if (scrollingElement !== null) {
		const current = scrollingElement.scrollTop
		const max = scrollingElement.scrollHeight - scrollingElement.clientHeight
		const percent = Math.floor((current / max) * 100)
		if (percent >= loadOnScrollBottomPercent && batchRequester.isNextAvailable) {
			batchRequester.fetchNext()
		}
	}
}

onMounted(() => {
	batchRequester.fetchNext()
	state.selected = null
	document.addEventListener("scroll", onScroll)
})

onUnmounted(() => {
	document.removeEventListener("scroll", onScroll)
})
</script>

<style>
.series-list {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 500px));
}

.series-modal {
	padding: 20px;
	overflow-y: auto;
}

.series-season {
	border-top-style: solid;
	border-top-color: var(--primary);
	margin-bottom: 15px;
}

.series-title {
	font-size: 30px;
}

.tree-icon {
	max-height: 18px;
	vertical-align: middle;
}

.toolbar {
	display: inline;
}

.toolbar-action {
	visibility: hidden;
}

.toolbar-action:hover {
	cursor: pointer;
	border-style: solid;
}

.episode-item:hover .toolbar-action {
	visibility: visible;
}

.episode-item:hover {
	background-color: var(--primary-invert-highlight);
}
</style>
