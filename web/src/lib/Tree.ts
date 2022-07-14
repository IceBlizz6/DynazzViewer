import { ListFunc } from './ListFunc'
import { VideoFile, VideoFileResponse } from './Queries'
import { TreeNode } from '@/lib/TreeNode'
import { ViewStatus } from '@/zeus'
import { graphClient } from './GraphClient'

export interface Tree {
	roots: TreeNode[]
	videoFiles: VideoFile[]

	load(response: VideoFileResponse): void
	setViewed(node: TreeNode, updatedViewStatus: ViewStatus): Promise<void>
	removeRoot(node: TreeNode): Promise<void>
	showExplorer(node: VideoFile): Promise<void>
	playVideo(node: VideoFile): Promise<void>
}

export class TreeImpl implements Tree {
	public roots: TreeNode[] = []
	public videoFiles: VideoFile[] = []

	public load(response: VideoFileResponse): void {
		const sourceVideoFiles = response.listVideoFiles
		this.roots = sourceVideoFiles.map(el => this.filePathsToTree(el.root, el.files))
		this.videoFiles = sourceVideoFiles.flatMap(el => el.files)
	}

	public async setViewed(node: TreeNode, updatedViewStatus: ViewStatus): Promise<void> {
		if (node.videoFile == null) {
			throw new Error("Node is not a video, may be a directory")
		} else {
			const response = await graphClient.mutation({
				setViewStatus: [
					{
						status: updatedViewStatus,
						videoFilePaths: [ node.videoFile.filePath.path ]
					},
					{
						fileName: true,
						mediaFileId: true
					}
				]
			})
			for (const entry of response.setViewStatus) {
				const nodes: VideoFile[] = this.videoFilesByName(entry.fileName)
				for (const node of nodes) {
					node.mediaFileId = entry.mediaFileId
					node.viewStatus = updatedViewStatus
				}
			}
		}
	}

	public async removeRoot(node: TreeNode): Promise<void> {
		const included = this.roots.includes(node)
		if (!included) {
			throw new Error("Node is not a root directory")
		} else {
			const { removeRootDirectory } = await graphClient.mutation({
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

	public async showExplorer(node: VideoFile): Promise<void> {
		const { showExplorer } = await graphClient.mutation({
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

	public async playVideo(node: VideoFile): Promise<void> {
		const success = await graphClient.mutation({
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

	private videoFilesFromParent(node: TreeNode): VideoFile[] {
		if (node.videoFile != null) {
			return [ node.videoFile ]
		} else if (node.children != null) {
			return node.children.flatMap(e => this.videoFilesFromParent(e))
		} else {
			return []
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
}
