<template>
	<header>
		<router-link to="/">
			Files
		</router-link> |
		<router-link to="/media">
			Media
		</router-link> |
		<router-link to="/media-search">
			Search
		</router-link> |
		<router-link to="/anime-season">
			Anime seasons
		</router-link> |
	</header>
</template>

<script lang="ts">
import { Vue } from "vue-class-component"

export default class MenuHeader extends Vue {
}
</script>

<style scoped>
header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	background-color: lightblue;
	margin-bottom: 15px;
	padding-left: 20px;
	border-style: solid;
}
</style>
