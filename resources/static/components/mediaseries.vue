<template>
	<section class="series-item" @click="$root.selectSeries(source)">
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
<script>
module.exports = {
	name: 'media-series',
	props: [ 'source' ],
	data: function() {
		return {
		}
	},
	computed: {
		episodesWatched: function() {
			return this.source.children
				.flatMap(season => season.children)
				.filter(ep => ep.status == "Viewed")
				.length
		},
		episodesAired: function() {
			let today = moment().format("YYYY-MM-DD");
			return this.source.children
				.flatMap(season => season.children)
				.filter(ep => ep.aired != null && today > ep.aired)
				.length
		},
		episodesAnnounced: function() {
			return this.source.children
				.flatMap(season => season.children)
				.length;
		},
	},
}
</script>
