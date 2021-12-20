import { Chain, Gql, ViewStatus, ZeusHook } from '@/zeus'

const queries = () => {
	const listMediaUnits = () => {
		return Gql("query")({
			listMediaUnits: {
				id: true,
				name: true,
				images: {
					url: true
				},
				children: {
					id: true,
					name: true,
					seasonNumber: true,
					sortOrder: true,
					children: {
						id: true,
						episodeNumber: true,
						name: true,
						status: true,
						sortOrder: true,
						aired: true,
						mediaFile: {
							id: true,
						}
					}
				}
			}
		})
  	}
	const listVideoFiles = () => {
		return Gql("query")({
			listVideoFiles: {
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
		})
	}
	return { listMediaUnits, listVideoFiles }
}

export type MediaUnitResponse = ZeusHook<typeof queries, "listMediaUnits">
export type MediaUnit = MediaUnitResponse["listMediaUnits"][number]
export type MediaPartCollection = MediaUnit["children"][number]
export type MediaPart = MediaPartCollection["children"][number]

export type VideoFileResponse = ZeusHook<typeof queries, "listVideoFiles">
export type VideoFile = VideoFileResponse["listVideoFiles"][number]["files"][number]

export default queries()
