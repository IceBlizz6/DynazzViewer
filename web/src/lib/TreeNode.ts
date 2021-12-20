import { VideoFile } from "@/lib/Queries"

export class TreeNode {
	private static idCounter = 1

	public localId = TreeNode.idCounter++

	private constructor(
		public name: string,
		public children: TreeNode[] | null,
		public videoFile: VideoFile | null
	) {}

	public static videoFileNode(videoFile: VideoFile) {
		return new TreeNode(videoFile.fileName.name, null, videoFile)
	}

	public static directoryNode(name: string) {
		return new TreeNode(name, [], null)
	}
}
