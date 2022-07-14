import { MalYearSeason, ZeusHook } from '@/zeus'
import { graphClient } from './GraphClient'

/* eslint-disable @typescript-eslint/explicit-function-return-type */
const queries = () => {
	const listMediaUnits = () => {
		return graphClient.query({
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
		return graphClient.query({
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
	const animeSeasonList = () => {
		return graphClient.query({
			animeSeasonList: {
				year: true,
				season: true,
			}
		})
	}
	const animeSeasonSeries = (year: number, season: MalYearSeason) => {
		return graphClient.query({
			animeSeason: [
				{
					year: year,
					season: season
				},
				{
					malId: true,
					title: true,
					flag: true,
					imageUrl: true,
					url: true,
					episodes: true,
					score: true,
					type: true,
					saved: true
				}
			]
		})
	}
	return {
		listMediaUnits,
		listVideoFiles,
		animeSeasonList,
		animeSeasonSeries
	}
}

export type MediaUnitResponse = ZeusHook<typeof queries, "listMediaUnits">
export type MediaUnit = MediaUnitResponse["listMediaUnits"][number]
export type MediaPartCollection = MediaUnit["children"][number]
export type MediaPart = MediaPartCollection["children"][number]

export type VideoFileResponse = ZeusHook<typeof queries, "listVideoFiles">
export type VideoFile = VideoFileResponse["listVideoFiles"][number]["files"][number]

export type AnimeSeasonListResponse = ZeusHook<typeof queries, "animeSeasonList">
export type MalSeasonIdentifier = AnimeSeasonListResponse["animeSeasonList"][number]

export type AnimeSeasonSeriesResponse = ZeusHook<typeof queries, "animeSeasonSeries">
export type AnimeSeasonSeries = AnimeSeasonSeriesResponse["animeSeason"][number]

export default queries()
