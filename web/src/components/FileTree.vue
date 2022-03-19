<template>
	<li>
		<div class="tree-item-header directory-node">
			<span @click="toggleChildren">
				<img
					class="tree-icon"
					src="@/assets/FolderContentAvailable.png"
				>
				<span class="tree-item-name">{{ label }}</span>
			</span>
			<span
				class="toolbar-action"
				@click="detectLink(parentNode)"
			>
				<img
					class="tree-icon"
					src="@/assets/Link.png"
				>
				Detect/Link
			</span>
			<span
				v-if="isRoot"
				class="toolbar-action"
				@click="removeRoot"
			>
				<img
					class="tree-icon"
					src="@/assets/Remove.png"
				>
				Remove
			</span>
		</div>
		<ul
			v-if="state.showChildren"
			class="tree-children-list"
		>
			<FileTree.default
				v-for="node in childrenDirectories"
				:key="node.name"
				:parent-node="node"
				:nodes="node.children!"
				:label="node.name"
				:tree="tree"
				:is-root="false"
				:on-detect-link="onDetectLink"
			/>
			<li
				v-for="node in childrenFiles"
				:key="node.localId"
			>
				<div
					v-if="node.videoFile != null"
					class="tree-item-header file-node"
				>
					<img
						v-if="node.videoFile.viewStatus == 'None'"
						class="tree-icon"
						src="@/assets/videofiles/Neutral.png"
					>
					<img
						v-if="node.videoFile.viewStatus == 'Viewed'"
						class="tree-icon"
						src="@/assets/videofiles/Viewed.png"
					>
					<img
						v-if="node.videoFile.viewStatus == 'Skipped'"
						class="tree-icon"
						src="@/assets/videofiles/Skipped.png"
					>
					<img
						v-if="isLinked(node.videoFile)"
						class="tree-icon"
						src="@/assets/Link.png"
					>

					<span class="tree-item-name">{{ node.videoFile.fileName.name }}</span>
					<span
						class="toolbar-action toolbar-action-play"
						@click="playVideo(node)"
					>
						<o-tooltip
							label="Play video"
							position="top"
						>
							<img
								class="tree-icon"
								src="@/assets/videofiles/Play.png"
							>
						</o-tooltip>
					</span>
					<span
						class="toolbar-action"
						@click="setStatusViewed(node)"
					>
						<o-tooltip
							label="Flag viewed"
							position="top"
						>
							<img
								class="tree-icon"
								src="@/assets/videofiles/Viewed.png"
							>
						</o-tooltip>
					</span>
					<span
						class="toolbar-action"
						@click="setStatusSkipped(node)"
					>
						<o-tooltip
							label="Flag skipped"
							position="top"
						>
							<img
								class="tree-icon"
								src="@/assets/videofiles/Skipped.png"
							>
						</o-tooltip>
					</span>
					<span
						class="toolbar-action"
						@click="setStatusNone(node)"
					>
						<o-tooltip
							label="Reset status"
							position="top"
						>
							<img
								class="tree-icon"
								src="@/assets/Undo.png"
							>
						</o-tooltip>
					</span>
					<span
						class="toolbar-action toolbar-action-exporer"
						@click="showExplorer(node)"
					>
						<o-tooltip
							label="Show in explorer"
							position="top"
						>
							<img
								class="tree-icon"
								src="@/assets/FolderOpen.png"
							>
						</o-tooltip>
					</span>
				</div>
			</li>
		</ul>
	</li>
</template>

<script setup lang="ts">
import { computed, reactive } from "vue"
import { TreeNode } from "@/lib/TreeNode"
import { ViewStatus } from "@/zeus"
import { VideoFile } from "@/lib/Queries"
import { Tree } from "@/lib/Tree"
import * as FileTree from "@/components/FileTree.vue"

interface Props {
	label: string
	nodes: TreeNode[]
	parentNode: TreeNode
	tree: Tree
	isRoot: boolean
	onDetectLink(videoFiles: VideoFile[]): void
}

const props = defineProps<Props>()

class State {
	public showChildren: boolean = true
}
const state = reactive(new State())

function removeRoot(): void {
	props.tree.removeRoot(props.parentNode)
}

function toggleChildren(): void {
	state.showChildren = !state.showChildren
}

function isLinked(videoFile: VideoFile): boolean {
	return videoFile.linkedMediaPartId != null
}

function setStatusViewed(node: TreeNode): void {
	setViewed(node, ViewStatus.Viewed)
}

function setStatusSkipped(node: TreeNode): void {
	setViewed(node, ViewStatus.Skipped)
}

function setStatusNone(node: TreeNode): void {
	setViewed(node, ViewStatus.None)
}

function showExplorer(node: TreeNode): void {
	if (node.videoFile == null) {
		throw new Error("Node is not a video file")
	} else {
		props.tree.showExplorer(node.videoFile)
	}
}

function setViewed(node: TreeNode, status: ViewStatus): void {
	props.tree.setViewed(node, status)
}

function playVideo(node: TreeNode): void {
	if (node.videoFile == null) {
		throw new Error("Node is not a video")
	} else {
		props.tree.playVideo(node.videoFile)
	}
}

function detectLink(node: TreeNode): void {
	if (node.children == null) {
		throw new Error("Node is not a directory")
	} else {
		const videoFiles = node.children
			.map(el => el.videoFile)
			.filter(notEmpty)
		props.onDetectLink(videoFiles)
	}
}

function notEmpty<TValue>(value: TValue | null | undefined): value is TValue {
	return value !== null && value !== undefined
}

const childrenDirectories = computed((): TreeNode[] => {
	return props.nodes
		.filter(el => el.children != null)
		.sort(
			(a: TreeNode, b: TreeNode) => (a.name > b.name)? 1 : -1
		)
})

const childrenFiles = computed((): TreeNode[] => {
	return props.nodes
		.filter(el => el.videoFile != null)
		.sort(
			(a, b) => {
				if (a.videoFile == null || b.videoFile == null) {
					throw new Error("Sort encountered node without video")
				} else {
					return (a.videoFile.fileName.name > b.videoFile.fileName.name)? 1 : -1
				}
			}
		)
})
</script>

<style scoped>
.file-node:hover {
	background-color: var(--primary-invert-highlight);
}

:root {
	--toolbar-item-margin: 10px;
}

.tree-item-name {
	padding-left: 10px;
}

.toolbar-action-exporer {
	margin-left: var(--toolbar-item-margin);
}

.toolbar-action-play {
	margin-right: var(--toolbar-item-margin);
}
</style>
