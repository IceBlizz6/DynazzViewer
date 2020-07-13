import { VideoFile } from '@/graph/schema'

export class TreeNode {
	public name: string
	public children: TreeNode[] | null
	public videoFile: VideoFile | null

	private constructor(name: string, children: TreeNode[] | null, videoFile: VideoFile | null) {
		this.name = name
		this.children = children
		this.videoFile = videoFile
	}

	public static videoFileNode(videoFile: VideoFile) {
		return new TreeNode(videoFile.fileName!.name, null, videoFile)
	}

	public static directoryNode(name: string) {
		return new TreeNode(name, [], null)
	}
}
