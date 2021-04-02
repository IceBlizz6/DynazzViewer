<template>
	<li>
		<div @click="toggleChildren()" class="tree-item-header directory-node">
			<img class="tree-icon" src="@/assets/FolderContentAvailable.png">
			<span>{{ label }}</span>
		</div>
		<ul class="tree-children-list" v-if="showChildren">
			<FileTree
				v-for="node in childrenDirectories"
				:nodes="node.children"
				:label="node.name"
				:key="node.name">
			</FileTree>
			<li v-for="node in childrenFiles" :key="node.videoFile.fileName.name">
				<div class="tree-item-header file-node">
					<img v-if="node.videoFile.viewStatus == 'None'" class="tree-icon" src="@/assets/videofiles/Neutral.png">
					<img v-if="node.videoFile.viewStatus == 'Viewed'" class="tree-icon" src="@/assets/videofiles/Viewed.png">
					<img v-if="node.videoFile.viewStatus == 'Skipped'" class="tree-icon" src="@/assets/videofiles/Skipped.png">
					<span>{{ node.videoFile.fileName.name }}</span>
					<span class="toolbar-action" @click="playVideo(node)">
						<img class="tree-icon" src="@/assets/videofiles/Play.png">
						Play
					</span>
					<span class="toolbar-action" @click="setStatusNone(node)">
						<img class="tree-icon" src="@/assets/videofiles/Neutral.png">
						Undo
					</span>
					<span class="toolbar-action" @click="setStatusViewed(node)">
						<img class="tree-icon" src="@/assets/videofiles/Viewed.png">
						Viewed
					</span>
					<span class="toolbar-action" @click="setStatusSkipped(node)">
						<img class="tree-icon" src="@/assets/videofiles/Skipped.png">
						Skipped
					</span>
					<span class="toolbar-action" @click="showExplorer(node)">
						<img class="tree-icon" src="@/assets/FolderOpen.png">
						Show
					</span>
				</div>
			</li>
		</ul>
	</li>
</template>

<script lang="ts">
import { Component, Prop,  Vue } from 'vue-property-decorator'
import { TreeNode } from '@/lib/TreeNode'
import { ViewStatus } from '@/graph/schema'
import FileView from '@/views/FileView.vue'

@Component
export default class FileTree extends Vue {
	@Prop({required: true})
	private readonly label!: string

	@Prop({required: true})
	private readonly nodes!: TreeNode[]
	
	private showChildren = true
	
	private get childrenDirectories(): TreeNode[] {
		return this.nodes.filter(el => el.children != null).sort((a: TreeNode, b: TreeNode) => (a.name > b.name)? 1 : -1)
	}

	private get childrenFiles(): TreeNode[] {
		return this.nodes
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
	}
	
	private toggleChildren(): void {
		this.showChildren = !this.showChildren
	}

	private showExplorer(node: TreeNode): void {
		if (node.videoFile == null) {
			throw new Error("Node is not a video file")
		} else {
			this.parentFileView.showExplorer(node.videoFile)
		}
	}

	private setStatusViewed(node: TreeNode): void {
		this.setViewed(node, ViewStatus.Viewed)
	}

	private setStatusSkipped(node: TreeNode): void {
		this.setViewed(node, ViewStatus.Skipped)
	}

	private setStatusNone(node: TreeNode): void {
		this.setViewed(node, ViewStatus.None)
	}

	private setViewed(node: TreeNode, status: ViewStatus): void {
		this.parentFileView.setViewed(node, status)
	}

	private playVideo(node: TreeNode): void {
		if (node.videoFile == null) {
			throw new Error("Node is not a video")
		} else {
			this.parentFileView.playVideo(node.videoFile)
		}
	}

	private get parentFileView(): FileView {
		const parent = this.$parent
		if (parent instanceof FileView) {
			return parent
		} else if (parent instanceof FileTree) {
			return parent.parentFileView
		} else {
			throw new Error("Unknown parent: " + parent)
		}
	}
}
</script>

<style>
.file-node {
}

.file-node:hover {
	background-color: lightgray;
}
</style>
