import { VideoFile } from "@/lib/Queries"

export class TreeNode {
	private static idCounter = 1

	public localId = TreeNode.idCounter++

	private constructor(
		public name: string,
		public parent: TreeNode | null,
		public children: TreeNode[] | null,
		public videoFile: VideoFile | null
	) {}

	public get fullPath(): string {
		const names = [ this.name ]
		let current: TreeNode | null = this.parent
		while (current !== null) {
			names.unshift(current.name)
			current = current.parent
		}
		return names.join("/")
	}

	public childByPath(fullPath: string): TreeNode {
		const pathSegments = TreeNode.resolvePathSegments(this.fullPath, fullPath)
			.splice(1)
		if (pathSegments.length < 1) {
			throw new Error("Error on path segment, empty or had only root: fullPath")
		} else {
			let current: TreeNode = this.childByName(pathSegments[0])
			for (const pathSegment of pathSegments.splice(1)) {
				current = current.childByName(pathSegment)
			}
			return current
		}
	}

	public childByName(childName: string): TreeNode {
		if (this.children === null) {
			throw new Error("Node has no children")
		} else {
			const match = this.children.find(e => e.name === childName)
			if (match === undefined) {
				throw new Error("Node has no child with name " + childName)
			} else {
				return match
			}
		}
	}

	public static resolvePathSegments(rootPath: string, fullPath: string): string[] {
		if (fullPath.startsWith(rootPath)) {
			const pathList = []
			pathList.push(rootPath)
			fullPath
				.substring(rootPath.length)
				.split("/")
				.forEach(el => { if (el.length > 0) {pathList.push(el) } })
			return pathList
		} else {
			throw new Error(fullPath + " does not start with " + rootPath)
		}
	}

	public static videoFileNode(parent: TreeNode, videoFile: VideoFile): TreeNode {
		return new TreeNode(videoFile.fileName.name, parent, null, videoFile)
	}

	public static directoryNode(parent: TreeNode | null, name: string): TreeNode {
		return new TreeNode(name, parent, [], null)
	}
}
