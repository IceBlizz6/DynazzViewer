<template>
	<span
		class="toolbar-action"
		@click="emit('click')"
	>
		<o-tooltip
			:label="props.label"
			position="top"
		>
			<slot />
		</o-tooltip>
	</span>
</template>

<script setup lang="ts">
interface Props {
	label: string
}

interface Emits {
	(event: "click"): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
</script>
