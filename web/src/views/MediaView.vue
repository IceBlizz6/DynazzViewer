<template>
	<article>
		<article class="series-list">
			<MediaSeries
					v-for="mediaItem in this.source"
					:key="mediaItem.id"
					:source="mediaItem">
			</MediaSeries>
		</article>
		<article class="series-sidebar" v-if="this.selected != null">
			<span class="series-header">{{ selected.name }}</span>
			<div>
				<img v-if="selected.images.length > 0" :src="selected.images[0].url">
			</div>
			<section v-for="season in selected.children" :key="season.id">
				<p>{{ season.name }}</p>
				<ul>
					<li class="episode-item" v-for="episode in season.children" :key="episode.id">
						<img v-if="episode.status == stateNone" class="tree-icon" src="@/assets/videofiles/Neutral.png">
						<img v-if="episode.status == stateViewed" class="tree-icon" src="@/assets/videofiles/Viewed.png">
						<img v-if="episode.status == stateSkipped" class="tree-icon" src="@/assets/videofiles/Skipped.png">
						{{ episode.name }}
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
		</article>
	</article>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import graphClient from '@/lib/graph-client'
import { VideoFile, ViewStatus, MediaUnit, MediaPart } from '@/graph/schema'
import MediaSeries from '@/components/MediaSeries.vue'

@Component({
	components: {
		MediaSeries
	}
})
export default class MediaView extends Vue {
	public source: MediaUnit[] = []
	public selected: MediaUnit | null = null

	public stateNone = ViewStatus.None
	public stateViewed = ViewStatus.Viewed
	public stateSkipped = ViewStatus.Skipped


	mounted() {
		this.mediaQuery()
	}

	mediaQuery() {
		graphClient.query({
			listMediaUnits: {
				id: 1,
				name: 1,
				images: {
					url: 1
				},
				children: {
					id: 1,
					name: 1,
					seasonNumber: 1,
					sortOrder: 1,
					children: {
						id: 1,
						episodeNumber: 1,
						name: 1,
						status: 1,
						sortOrder: 1,
						aired: 1
					}
				}
			}
		}).then(response => {
			const data = response.data!.listMediaUnits!
			this.source = data.map(el => el!)
			this.selected = null
		})
	}

	selectSeries(item: MediaUnit) {
		if (this.selected == item) {
			this.selected = null;
		} else {
			this.selected = item;
		}
    }
			
	setEpisodeWatch(episode: MediaPart, status: ViewStatus) {
		graphClient.mutation({
			setEpisodeWatchState: [{ mediaPartId: episode.id, status: status }]
		}).then(response => {
			episode.status = status;
		})
    }
}
</script>

<style>
.series-item:hover {
	background-color: lightgray;
}

.series-header {
	font-size: 30px;
}

.series-list {
	width: 50%;
}

.series-sidebar {
	border-left: solid;
	overflow-y: scroll;
	position: fixed;
	right: 0px;
	top: 0px;
	bottom: 0px;
	width: 50%;
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
