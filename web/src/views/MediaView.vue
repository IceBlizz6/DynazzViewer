<template>
	<article>
		<article class="series-list">
			<MediaSeries
				v-for="mediaItem in this.source"
				:key="mediaItem.id"
				:source="mediaItem">
			</MediaSeries>
		</article>
		<b-modal v-model:active="activeModal">
			<article class="series-modal" v-if="this.selected != null">
				<div class="series-modal-img">
					<img v-if="selected.images.length > 0" :src="selected.images[0].url">
				</div>
				<span class="series-title">{{ selected.name }}</span>
				<div class="series-season-list">
					<section class="series-season" v-for="season in selected.children" :key="season.id">
						<p>{{ season.name }}</p>
						<ul>
							<li class="episode-item" v-for="episode in season.children" :key="episode.id">
								<img v-if="episode.status == stateNone" class="tree-icon" src="@/assets/videofiles/Neutral.png">
								<img v-if="episode.status == stateViewed" class="tree-icon" src="@/assets/videofiles/Viewed.png">
								<img v-if="episode.status == stateSkipped" class="tree-icon" src="@/assets/videofiles/Skipped.png">
								<img v-if="isLinked(episode)" class="tree-icon" src="@/assets/Link.png">

								{{ episodeFormat(episode.episodeNumber) }} | {{  episode.name }}
								<div class="toolbar">
									<span class="toolbar-action" @click="setEpisodeWatch(episode, stateNone)">
										<img class="tree-icon" src="@/assets/videofiles/Neutral.png">
										Undo
									</span>
									<span class="toolbar-action" @click="setEpisodeWatch(episode, stateViewed)">
										<img class="tree-icon" src="@/assets/videofiles/Viewed.png">
										Viewed
									</span>
									<span class="toolbar-action" @click="setEpisodeWatch(episode, stateSkipped)">
										<img class="tree-icon" src="@/assets/videofiles/Skipped.png">
										Skipped
									</span>
								</div>
							</li>
						</ul>
					</section>
				</div>
			</article>
		</b-modal>
	</article>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import { Chain, Gql, ViewStatus, ZeusHook } from '@/zeus'
import MediaSeries from '@/components/MediaSeries.vue'
import numeral from 'numeral'
import queries, { MediaUnit, MediaPart } from "@/lib/Queries"

@Options({
	components: {
		MediaSeries
	}
})
export default class MediaView extends Vue {
	public source: MediaUnit[] = []
	public selected: MediaUnit | null = null
	public activeModal = false

	public stateNone = ViewStatus.None
	public stateViewed = ViewStatus.Viewed
	public stateSkipped = ViewStatus.Skipped

	public async mounted(): Promise<void> {
		const response = await queries.listMediaUnits()
		this.source = response.listMediaUnits
		this.selected = null
	}

	private episodeFormat(value: number): string {
		return "E" + numeral(value).format("00")
	}

	private isLinked(mediaPart: MediaPart): boolean {
		return mediaPart.mediaFile != null
	}

	public selectSeries(item: MediaUnit): void {
		this.selected = item
		this.activeModal = true
	}
			
	private async setEpisodeWatch(episode: MediaPart, status: ViewStatus): Promise<void> {
		const response = await Gql("mutation")(
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
}
</script>

<style>
.series-list {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 500px));
}

.series-modal {
	background-color:white;
	padding: 20px;
	overflow-y: auto;
}

.series-season {
	border-top-style: solid;
	border-top-color: darkblue;
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

.episode-item:hover .toolbar-action {
	visibility: visible;
}

.episode-item:hover {
	background-color: lightgray;
}

.toolbar-action:hover {
	cursor: pointer;
	border-style: solid;
}
</style>
