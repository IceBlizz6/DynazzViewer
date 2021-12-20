<template>
	<article>
		<section
			v-for="root in roots"
			:key="root.name"
		>
			<ul class="tree-children-list">
				<FileTree
					:parent-node="root"
					:label="root.name"
					:nodes="root.children"
					:file-view="fileView"
					:is-root="true"
				/>
			</ul>
		</section>
		<o-modal
			v-model:active="activeModal"
			class="link-modal"
			width="80%"
			destroy-on-hide
		>
			<FileLinkModal
				:detected-file-results="detectedFileResults"
				@finalized="closeModal"
			/>
		</o-modal>
	</article>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-property-decorator'
import { TreeNode } from '@/lib/TreeNode'
import { Gql, ViewStatus } from '@/zeus'
import FileTree from "@/components/FileTree.vue"
import FileLinkModal from "@/views/FileLinkModal.vue"
import { FileLinkRow } from "@/lib/FileLinkRow"
import queries, { VideoFile } from "@/lib/Queries"
import { ListFunc } from "@/lib/ListFunc"

@Options({
	components: {
		FileTree,
		FileLinkModal
	}
})
export default class FileView extends Vue {
	private videoFiles: VideoFile[] = []
	private roots: TreeNode[] = []
	private activeModal = false
	private detectedFileResults: FileLinkRow[] = []

	public async mounted(): Promise<void> {
		const response = await queries.listVideoFiles()
		const sourceVideoFiles = response.listVideoFiles
		this.roots = sourceVideoFiles.map(el => this.filePathsToTree(el.root, el.files))
		this.videoFiles = sourceVideoFiles.flatMap(el => el.files)
	}

	public async playVideo(node: VideoFile): Promise<void> {
		const success = await Gql("mutation")({
			playVideo: [
				{
					path: node.filePath.path
				},
				true
			]
		})
		if (!success) {
			throw new Error("Unable to play video file")
		}
	}

	private closeModal(): void {
		this.activeModal = false
	}

	public async showExplorer(node: VideoFile): Promise<void> {
		const { showExplorer } = await Gql("mutation")({
			showExplorer: [
				{
					path: node.filePath.path
				},
				true
			]
		})
		if (!showExplorer) {
			throw new Error("Request failed")
		}
	}

	private get fileView(): FileView {
		return this
	}

	private videoFilesFromParent(node: TreeNode): VideoFile[] {
		if (node.videoFile != null) {
			return [ node.videoFile ]
		} else if (node.children != null) {
			return node.children.flatMap(e => this.videoFilesFromParent(e))
		} else {
			return []
		}
	}

	public async removeRoot(node: TreeNode): Promise<void> {
		const included = this.roots.includes(node)
		if (!included) {
			throw new Error("Node is not a root directory")
		} else {
			const { removeRootDirectory } = await Gql("mutation")({
				removeRootDirectory: [
					{
						rootPath: node.name
					},
					true
				]
			})
			if (!removeRootDirectory) {
				throw new Error("Unable to remove root directory")
			} else {
				ListFunc.remove(this.roots, node)
				const videoFiles = this.videoFilesFromParent(node)
				this.videoFiles = this.videoFiles.filter(e => !videoFiles.includes(e))
			}
		}
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

	public async detectLink(videoFiles: VideoFile[]): Promise<void> {
		this.activeModal = false
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
		this.detectedFileResults = videoFiles.map(videoFile => {
			const videoFileName = videoFile.fileName.name
			const match = fileResults.find(el => el.fileName == videoFileName)
			if (match != undefined) {
				return new FileLinkRow(videoFileName, match.name, match.season ?? null, match.episode ?? null)
			} else {
				return new FileLinkRow(videoFileName, null, null, null)
			}
		}).sort((a, b) => (a.fileName > b.fileName) ? 1 : -1)
		this.activeModal = true
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

	public async setViewed(node: TreeNode, updatedViewStatus: ViewStatus): Promise<void> {
		if (node.videoFile == null) {
			throw new Error("Node is not a video, may be a directory")
		} else {
			const response = await Gql("mutation")({
				setViewStatus: [
					{
						status: updatedViewStatus,
						videoFilePaths: [ node.videoFile.filePath.path ]
					},
					true
				]
			})
			const responseItems: Map<string, number> = response.setViewStatus
			for (const [fileName, mediaFileId] of Object.entries(responseItems)) {
				const nodes: VideoFile[] = this.videoFilesByName(fileName)
				for (const node of nodes) {
					node.mediaFileId = mediaFileId
					node.viewStatus = updatedViewStatus
				}
			}
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

.modal-body {
	background-color: whitesmoke;
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
