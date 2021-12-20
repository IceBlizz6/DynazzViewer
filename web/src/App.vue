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
	margin-top: 80px;
}

:root {
	--primary: rgba(187, 187, 187, 1);
	--primary-invert: rgba(28, 28, 28, 1);
	--primary-invert-highlight: rgba(99, 99, 99, 1);

	--oruga-tooltip-background-color: var(--primary-invert);
	--oruga-tooltip-color: var(--primary);
	--oruga-modal-content-background-color: background-color: var(--primary-invert);
	--oruga-modal-content-fullscreen-background-color: background-color: var(--primary-invert);
	--oruga-modal-overlay-background-color: var(--primary-invert);

	--oruga-button-background-color: var(--primary-invert-highlight);
	--oruga-button-color: var(--primary);
	--oruga-button-border: 1px solid var(--primary-invert);

	--oruga-checkbox-active-background-color: var(--primary-invert-highlight);
	--oruga-checkbox-background-color: var(--primary-invert-highlight);
	--oruga-checkbox-border-color: var(--primary-invert-highlight);
	--oruga-checkbox-checkmark-color: var(--primary);

	--oruga-field-label-color: var(--primary);
}

.o-btn:hover {
	border-color: var(--primary);
}

body {
	background-color: var(--primary-invert);
}

p, a, dt, dd, span, li, h1 {
	color: var(--primary);
	font-size: 1.05em;
	
}
</style>
