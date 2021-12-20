<template>
	<div>
		<MenuHeader />
		<main>
			<router-view />
		</main>
	</div>
</template>

<script lang="ts">
import { Options, Vue } from "vue-class-component"
import MenuHeader from "@/components/MenuHeader.vue"

@Options({
	components: {
		MenuHeader
	}
})
export default class App extends Vue {
}
</script>

<style>
main {
	margin-top: 30px;
}

:root {
	--oruga-tooltip-background-color: red;
	--oruga-tooltip-color: white;
}
</style>
