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
					<ToolbarAction
						label="Play video"
						@click="playVideo(node)"
					>
						<img
							class="tree-icon"
							src="@/assets/videofiles/Play.png"
						>
					</ToolbarAction>
					<ToolbarAction
						label="Flag viewed"
						@click="setStatusViewed(node)"
					>
						<img
							class="tree-icon"
							src="@/assets/videofiles/Viewed.png"
						>
					</ToolbarAction>
					<ToolbarAction
						label="Flag skipped"
						@click="setStatusSkipped(node)"
					>
						<img
							class="tree-icon"
							src="@/assets/videofiles/Skipped.png"
						>
					</ToolbarAction>
					<ToolbarAction
						label="Reset status"
						@click="setStatusNone(node)"
					>
						<img
							class="tree-icon"
							src="@/assets/Undo.png"
						>
					</ToolbarAction>
					<ToolbarAction
						label="Show in explorer"
						@click="showExplorer(node)"
					>
						<img
							class="tree-icon"
							src="@/assets/FolderOpen.png"
						>
					</ToolbarAction>
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
import ToolbarAction from "@/components/ToolbarAction.vue"

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
	public showChildren: boolean = false
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

.tree-item-name {
	padding-left: 10px;
}
</style>
