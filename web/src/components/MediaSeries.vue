<template>
	<section class="series-item" @click="selectSeries(source)">
		<span class="series-header">{{ source.name }}</span>
		<div>
			<img v-if="source.images.length > 0" :src="source.images[0].url">
		</div>
		<dl>
			<dt>Episodes watched</dt>
			<dd>{{ episodesWatched }}</dd>
			<dt>Episodes aired</dt>
			<dd>{{ episodesAired }}</dd>
			<dt>Episodes announced</dt>
			<dd>{{ episodesAnnounced }}</dd>
		</dl>
	</section>
</template>


<script lang="ts">
import { Component, Prop,  Vue } from 'vue-property-decorator';
import { TreeNode } from '@/lib/TreeNode'
import { VideoFile, ViewStatus, MediaUnit } from '@/graph/schema'
import FileView from '@/views/FileView.vue'
import moment from 'moment'
import MediaView from '@/views/MediaView.vue';

@Component
export default class MediaSeries extends Vue {
    @Prop({required: true})
    public source!: MediaUnit

    selectSeries() {
        const parent = this.$parent
        if (parent instanceof MediaView) {
            parent.selectSeries(this.source)
        } else {
            throw new Error("Unknown parent: " + parent)
        }
    }

    get episodesWatched() {
        return this.source.children!
            .flatMap(season => season!.children)
            .filter(ep => ep!.status == ViewStatus.Viewed)
            .length
    }

    get episodesAired() {
        const today = moment().format("YYYY-MM-DD")
        return this.source.children!
            .flatMap(season => season!.children)
            .filter(ep => ep!.aired != null && today > ep!.aired)
            .length
    }

    get episodesAnnounced() {
        return this.source.children!
            .flatMap(season => season!.children)
            .length;
    }
}
</script>
