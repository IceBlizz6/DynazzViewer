<template>
	<header>
		<router-link to="/">
			Files
		</router-link>
		<router-link to="/media">
			Media
		</router-link>
		<router-link to="/media-search">
			Search
		</router-link>
		<router-link to="/anime-season">
			Anime seasons
		</router-link>
	</header>
</template>

<script setup lang="ts">
</script>

<style scoped>
header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	background-color: var(--primary-invert);
	padding-bottom: 0;
	padding-left: 30px;
	border-bottom: solid;
	border-bottom-color: var(--primary-invert-highlight);
	z-index: 100;
}

header a {
	display: block;
	float: left;
	padding-left: 10px;
	padding-right: 10px;
	font-size: 1.2em;
	padding-top: 15px;
	padding-bottom: 15px;
	margin-bottom: 0;
}

header a:hover {
	background-color: var(--primary-invert-highlight);
}
</style>
