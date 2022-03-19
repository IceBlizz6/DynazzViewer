<template>
	<div>
		<MenuHeader />
		<main>
			<router-view />
		</main>
	</div>
</template>

<script setup lang="ts">
import MenuHeader from "@/components/MenuHeader.vue"
</script>

<style>
main {
	margin-top: 80px;
}

:root {
	--primary: rgb(187 187 187 / 100%);
	--primary-invert: rgb(28 28 28 / 100%);
	--primary-invert-highlight: rgb(99 99 99 / 100%);
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

.o-radio {
	padding: 5px;
	margin-left: 5px;
	margin-top: 5px;
	margin-bottom: 5px;
	background: var(--primary-invert-highlight);
	border-radius: 8px;
}

.o-radio input {
	border: solid;
	border-color: var(--primary-invert-highlight);
	background: var(--primary-invert);
}

/* stylelint-disable-next-line selector-class-pattern */
.o-radio--checked input {
	border: solid;
	border-color: var(--primary);
	background: var(--primary);
}

body {
	background-color: var(--primary-invert);
}

p,
a,
dt,
dd,
span,
li,
h1 {
	color: var(--primary);
	font-size: 1.05em;
}
</style>
