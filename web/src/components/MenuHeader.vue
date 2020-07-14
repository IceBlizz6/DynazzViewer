<template>
	<header>
		<router-link to="/">Files</router-link> |
		<router-link to="/media">Media</router-link> |
		<router-link to="/media-search">Search</router-link>
	</header>
</template>

<script lang="ts">
import { Component, Prop,  Vue } from 'vue-property-decorator';

@Component
export default class MenuHeader extends Vue {
}
</script>

<style scoped>
header {
    background-color: lightblue;
    margin-bottom: 15px;
    padding-left: 20px;
}
</style>
