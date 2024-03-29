import { createWebHistory, createRouter, RouteRecordRaw } from "vue-router"
import FileView from '@/views/FileView.vue'
import MediaView from '@/views/MediaView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import MediaSearchView from '@/views/MediaSearchView.vue'
import AnimeSeasonView from '@/views/AnimeSeasonView.vue'

export const MediaViewUrl = '/media'
export const FileViewUrl = '/'
export const MediaSearchViewUrl = '/media-search'
export const AnimeSeasonViewUrl = '/anime-season'

const routes: RouteRecordRaw[] = [
	{
		path: MediaViewUrl,
		name: 'Media',
		component: MediaView
	},
	{
		path: MediaSearchViewUrl,
		name: "Media search",
		component: MediaSearchView
	},
	{
		path: AnimeSeasonViewUrl,
		name: 'Anime seasons',
		component: AnimeSeasonView
	},
	{
		path: FileViewUrl,
		name: 'Video files',
		component: FileView
	},
	{
		path: "/:catchAll(.*)",
		name: 'Not found',
		component: NotFoundView
	}
]

const router = createRouter(
	{
		history: createWebHistory(),
		routes: routes
	}
)

export default router
