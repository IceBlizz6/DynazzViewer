<template>
	<section
		class="series-item"
	>
		<span class="series-header">{{ source.name }}</span>
		<div class="series-img">
			<img
				v-if="source.images.length > 0"
				:src="source.images[0].url"
			>
		</div>
		<dl class="series-details">
			<dt>Episodes watched</dt>
			<dd>{{ episodesWatched }}</dd>
			<dt>Episodes aired</dt>
			<dd>{{ episodesAired }}</dd>
			<dt>Episodes announced</dt>
			<dd>{{ episodesAnnounced }}</dd>
		</dl>
	</section>
</template>

<script setup lang="ts">
import moment from 'moment'
import { ViewStatus } from "@/zeus"
import { MediaUnit } from "@/lib/Queries"
import { computed } from "vue"

interface Props {
	source: MediaUnit
}

const props = defineProps<Props>()

const episodesWatched = computed((): number => {
	return props.source.children
		.flatMap(season => season.children)
		.filter(ep => ep.status == ViewStatus.Viewed)
		.length
})

const episodesAired = computed((): number => {
	const today = moment().format("YYYY-MM-DD")
	return props.source.children
		.flatMap(season => season.children)
		.filter(ep => ep.aired != null && today > ep.aired)
		.length
})

const episodesAnnounced = computed((): number => {
	return props.source.children
		.flatMap(season => season.children)
		.length
})
</script>

<style scoped>
.series-header {
	font-size: 20px;
	grid-column: 2;
	grid-row: 1;
}

.series-img {
	grid-column: 1;
	grid-row: 1 / 2;
}

.series-details {
	grid-column: 2;
	grid-row: 2;
}

.series-item {
	display: grid;
	grid-template-columns: 50% 50%;
	grid-template-rows: 10% 1fr;
	column-gap: 10px;
	padding: 5px;
	background-color: var(--primary-inverted);
	margin: 10px;
	border-style: solid;
	border-color: transparent;
}

.series-item:hover {
	background-color: var(--primary-inverted-highlight);
	border-style: solid;
	border-color: var(--primary);
}
</style>
