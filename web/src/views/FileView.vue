<template>
	<article>
		<section v-for="root in this.roots" :key="root.id">
			<ul class="tree-children-list">
				<FileTree
					:label="root.name"
					:nodes="root.children">
				</FileTree>
			</ul>
		</section>
	</article>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { TreeNode } from '@/lib/TreeNode'
import graphClient from '@/lib/graph-client'
import { VideoFile, ViewStatus } from '@/graph/schema'
import FileTree from "@/components/FileTree.vue"

@Component({
	components: {
		FileTree
	}
})
export default class FileView extends Vue {
	private videoFiles: VideoFile[] = []
	private roots: TreeNode[] = []
	
	protected mounted(): void {
		graphClient.query(
			{
				listVideoFiles: {
					root: 1,
					files: {
						fileName: {
							name: 1
						},
						filePath: {
							path: 1
						},
						mediaFileId: 1,
						viewStatus: 1
					}
				}
			},
			data => {
				const sourceVideoFiles = data.listVideoFiles
				this.roots = sourceVideoFiles.map(el => this.filePathsToTree(el.root, el.files))
				this.videoFiles = sourceVideoFiles.flatMap(el => el.files)
			}
		)
	}

	public playVideo(node: VideoFile): void {
		graphClient.mutation(
			{
				playVideo: [{ path: node.filePath.path }]
			},
			data => {
				if (!data.playVideo) {
					throw new Error("Request failed")
				}
			}
		)
	}
	
	public showExplorer(node: VideoFile): void {
		graphClient.mutation(
			{
				showExplorer: [{ path: node.filePath.path }]
			},
			data => {
				if (!data.showExplorer) {
					throw new Error("Request failed")
				}
			}
		)
	}
	
	private lookupChildren(parent: TreeNode, childName: string): TreeNode {
		const children = parent.children
		if (children == null) {
			throw new Error("Node does not support children")
		} else {
			for (let i = 0;i<children.length;i++) {
				if (children[i].name == childName) {
					return children[i]
				}
			}
			const newChild = TreeNode.directoryNode(childName)
			children.push(newChild)
			return newChild
		}
	}
	
	private pushNode(treeRoot: TreeNode, pathList: string[], childObject: VideoFile): void {
		let current = treeRoot
		for (let i = 1;i<pathList.length - 1;i++) {
			current = this.lookupChildren(current, pathList[i])
		}
		const videoFileNode = TreeNode.videoFileNode(childObject)
		if (current.children == null) {
			throw new Error("Node does not support children")
		} else {
			current.children.push(videoFileNode)
		}
	}
	
	private filePathsToTree(rootPath: string, filePaths: VideoFile[]): TreeNode {
		const rootNode = TreeNode.directoryNode(rootPath)
		for (let i = 0;i<filePaths.length;i++) {
			const paths = this.resolvePath(rootPath, filePaths[i].filePath.path)
			this.pushNode(rootNode, paths, filePaths[i])
		}
		return rootNode
	}
	
	private resolvePath(rootPath: string, filePath: string): string[] {
		if (filePath.startsWith(rootPath)) {
			const pathList = []
			pathList.push(rootPath)
			filePath.substring(rootPath.length).split("/").forEach(el => { if (el.length > 0) {pathList.push(el) } })
			return pathList
		} else {
			throw new Error(filePath + " does not start with " + rootPath)
		}
	}
	
	private videoFilesByName(nodeName: string): VideoFile[] {
		return this.videoFiles.filter(el => el.fileName.name == nodeName)
	}

	public setViewed(node: TreeNode, updatedViewStatus: ViewStatus): void {
		if (node.videoFile == null) {
			throw new Error("Node is not a video, may be a directory")
		} else {
			graphClient.mutation(
				{
					setViewStatus: [
						{ 
							status: updatedViewStatus,
							videoFilePaths: [ node.videoFile.filePath.path ]
						}
					]
				},
				data => {
					const responseItems: Map<string, number> = data.setViewStatus
					for (const [fileName, mediaFileId] of Object.entries(responseItems)) {
						const nodes: VideoFile[] = this.videoFilesByName(fileName)
						for (const node of nodes) {
							node.mediaFileId = mediaFileId
							node.viewStatus = updatedViewStatus
						}
					}

				}
			)
		}
	}
}
</script>

<style>
.directory-node {
	cursor: pointer;
}

.directory-node:hover {
	background-color: lightgray;
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
}

.toolbar-action:hover {
	cursor: pointer;
	border-style: solid;
}

.tree-item-header:hover .toolbar-action {
	visibility: visible;
}

</style>
