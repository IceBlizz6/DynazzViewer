export class FileLinkRow {
	public constructor(
		public fileName: string,
		public mediaName: string | null,
		public seasonNumber: number | null,
		public episodeNumber: number | null
	) {}
}
