import { ListFunc } from './ListFunc'
import { VideoFile, VideoFileResponse } from './Queries'
import { TreeNode } from '@/lib/TreeNode'
import { ViewStatus } from '@/zeus'
import { graphClient } from './GraphClient'

export interface Tree {
	roots: TreeNode[]

	load(response: VideoFileResponse): void
	setViewed(node: TreeNode, updatedViewStatus: ViewStatus): Promise<void>
	removeRoot(node: TreeNode): Promise<void>
	refreshDirectory(node: TreeNode): Promise<void>
	showExplorer(node: VideoFile): Promise<void>
	playVideo(node: VideoFile): Promise<void>
}

export class TreeImpl implements Tree {
	public roots: TreeNode[] = []

	public load(response: VideoFileResponse): void {
		const sourceVideoFiles = response.listVideoFiles
		const factory = new TreeNodeFactory()
		this.roots = sourceVideoFiles.map(el => factory.buildRoot(el.root, el.files))
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
			}
		}
	}

	private refreshSubDirectory(rootNode: TreeNode, refreshedPath: string, files: VideoFile[]): void {
		const node = rootNode.childByPath(refreshedPath)
		const parent = node.parent
		if (parent === null) {
			throw new Error("Child node must have a parent")
		} else if (parent.children === null) {
			throw new Error("Parent must support children")
		} else {
			const factory = new TreeNodeFactory()
			const createdNode = factory.buildSubDirectory(parent, node.name, files)
			ListFunc.remove(parent.children, node)
			parent.children.push(createdNode)
		}
	}

	private refreshRoot(rootNode: TreeNode, files: VideoFile[]): void {
		const factory = new TreeNodeFactory()
		const createdNode = factory.buildRoot(rootNode.name, files)
		ListFunc.remove(this.roots, rootNode)
		this.roots.push(createdNode)
	}

	public async refreshDirectory(node: TreeNode): Promise<void> {
		const { refreshDirectory } = await graphClient.mutation({
			refreshDirectory: [
				{
					path: node.fullPath
				},
				{
					root: true,
					files: {
						fileName: {
							name: true
						},
						filePath: {
							path: true
						},
						mediaFileId: true,
						viewStatus: true,
						linkedMediaPartId: true,
					}
				}
			]
		})
		const relevantRoots = refreshDirectory.filter(root => node.fullPath.startsWith(root.root)) 
		for (const root of relevantRoots) {
			const rootNode = this.roots.find(e => e.fullPath === root.root)
			if (rootNode === undefined) {
				throw new Error("No match for root path " + root.root)
			} else {
				const isRootNode = (rootNode.name === node.fullPath)
				if (isRootNode) {
					this.refreshRoot(rootNode, root.files)
				} else {
					this.refreshSubDirectory(rootNode, node.fullPath, root.files)
				}
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

	private videoFilesByName(nodeName: string): VideoFile[] {
		return this.roots
			.flatMap(e => this.videoFilesFromParent(e))
			.filter(el => el.fileName.name == nodeName)
	}
}

class TreeNodeFactory {
	/**
	 * @param rootPath Full path of root directory that was selected by user
	 * @param files List of all video files detected
	 */
	public buildRoot(rootPath: string, files: VideoFile[]): TreeNode {
		const rootNode = TreeNode.directoryNode(null, rootPath)
		this.insertFilesToParent(rootNode, files)
		return rootNode
	}

	public buildSubDirectory(directoryParent: TreeNode, directoryName: string, files: VideoFile[]): TreeNode {
		const directory = TreeNode.directoryNode(directoryParent, directoryName)
		this.insertFilesToParent(directory, files)
		return directory
	}

	private insertFilesToParent(ancestor: TreeNode, files: VideoFile[]): void {
		for (const file of files) {
			const pathSegments = TreeNode.resolvePathSegments(ancestor.fullPath, file.filePath.path)
			const pathSegmentsToDirectory = pathSegments.slice(1, pathSegments.length - 1)
			const parent = this.getOrCreateSubDirectory(ancestor, pathSegmentsToDirectory)
			if (parent.children == null) {
				throw new Error("Node does not support children")
			} else {
				const videoFileNode = TreeNode.videoFileNode(parent, file)
				parent.children.push(videoFileNode)
			}
		}
	}

	/**
	 * 
	 * @param rootNode Root node to search through
	 * @param pathSegments List of names that follows to sub directory
	 */
	private getOrCreateSubDirectory(rootNode: TreeNode, pathSegments: string[]): TreeNode {
		let node = rootNode
		for (const segment of pathSegments) {
			node = this.getOrCreateChildDirectory(node, segment)
		}
		return node
	}

	/**
	 * 
	 * @param parent Direct parent node of directory
	 * @param directoryName Name of directory to retrieve
	 */
	private getOrCreateChildDirectory(parent: TreeNode, directoryName: string): TreeNode {
		const children = parent.children
		if (children == null) {
			throw new Error("Node does not support children")
		} else {
			const child = children.find(e => e.name === directoryName)
			if (child === undefined) {
				const newChild = TreeNode.directoryNode(parent, directoryName)
				children.push(newChild)
				return newChild
			} else {
				return child
			}
		}
	}
}
