<template>
	<article>
		<section
			v-for="root in state.tree.roots"
			:key="root.name"
		>
			<ul class="tree-children-list">
				<FileTree
					:parent-node="root"
					:label="root.name"
					:nodes="root.children!"
					:tree="state.tree"
					:is-root="true"
					:on-detect-link="detectLink"
				/>
			</ul>
		</section>
		<o-modal
			v-model:active="state.activeModal"
			class="link-modal"
			width="80%"
			destroy-on-hide
		>
			<FileLinkModal
				:detected-file-results="state.detectedFileResults"
				@finalized="closeModal"
			/>
		</o-modal>

		<o-button @click="addRoot">
			Add
		</o-button>
	</article>
</template>

<script setup lang="ts">
import { Gql } from '@/zeus'
import FileTree from "@/components/FileTree.vue"
import FileLinkModal from "@/views/FileLinkModal.vue"
import queries, { VideoFile } from "@/lib/Queries"
import { Tree, TreeImpl } from "@/lib/Tree"
import { onMounted, reactive } from "vue"
import { FileLinkRow } from '@/lib/FileLinkRow'

class State {
	public tree: Tree = new TreeImpl()
	public activeModal = false
	public detectedFileResults: FileLinkRow[] = []
}
const state = reactive(new State())

function addRoot(): void {
	Gql("mutation")({
		addRootDirectoryInteractively: true
	})
}

function closeModal(): void {
	state.activeModal = false
}

async function detectLink(videoFiles: VideoFile[]): Promise<void> {
	state.activeModal = false
	const { parseFileNames } = await Gql("query")({
		parseFileNames: [
			{
				fileNames: videoFiles.map(el => el.fileName.name)
			}, 
			{
				fileName: true,
				name: true,
				season: true,
				episode: true
			}
		]
	})
	const fileResults = parseFileNames
	state.detectedFileResults = videoFiles.map(videoFile => {
		const videoFileName = videoFile.fileName.name
		const match = fileResults.find(el => el.fileName == videoFileName)
		if (match != undefined) {
			return new FileLinkRow(videoFileName, match.name, match.season ?? null, match.episode ?? null)
		} else {
			return new FileLinkRow(videoFileName, null, null, null)
		}
	}).sort((a, b) => (a.fileName > b.fileName) ? 1 : -1)
	state.activeModal = true
}

onMounted(async() => {
	const response = await queries.listVideoFiles()
	state.tree.load(response)
})
</script>

<style>
.directory-node {
	cursor: pointer;
}

.directory-node:hover {
	background-color: var(--primary-invert-highlight);
}

.tree-icon {
	max-height: 18px;
	vertical-align: middle;
}

.tree-children-list {
	list-style-type: none;
	padding-left: 15px;
}

.toolbar-action {
	visibility: hidden;
	border-style: solid;
	border-color: transparent;
}

.toolbar-action:hover {
	cursor: pointer;
	border-style: solid;
	border-color: var(--primary);
}

.tree-item-header:hover .toolbar-action {
	visibility: visible;
}

.modal-body {
	padding-left: 25px;
	min-width: 90%;
}

.next-button {
	margin-top: 35px;
	margin-bottom: 25px;
}

.link-header-section {
	display: grid;
	grid-template-columns: 50% 50;
	column-gap: 15px;
}

.media-name-list {
	grid-column: 1;
}

.media-name-options {
	grid-column: 2;
}

.result-section {
	padding-bottom: 25px;
}
</style>
