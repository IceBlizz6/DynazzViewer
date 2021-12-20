import { Vue } from "vue-class-component"
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

export class RouterHandler {
	private static currentPath(app: Vue): string {
		return app.$route.path
	}

	public static navigate(app: Vue, path: string): void {
		const current = RouterHandler.currentPath(app)
		if (current != path) {
			app.$router.push(path)
		}
	}
}

export default router
