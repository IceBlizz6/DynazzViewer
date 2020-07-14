import Vue from 'vue'
import VueRouter, { RouteConfig } from 'vue-router'
import FileView from '@/views/FileView.vue'
import MediaView from '@/views/MediaView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import MediaSearchView from '@/views/MediaSearchView.vue'

Vue.use(VueRouter)

export const MediaViewUrl = '/media'
export const FileViewUrl = '/'
export const MediaSearchViewUrl = '/media-search'

const routes: Array<RouteConfig> = [
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
		path: FileViewUrl,
		name: 'Video files',
		component: FileView
	},
	{
		path: '*',
		name: 'Not found',
		component: NotFoundView
	}
]

const router = new VueRouter({
	mode: 'history',
	base: process.env.BASE_URL,
	routes
})

export class RouterHandler {
	static currentPath(app: Vue) {
		return app.$route.path
	}

	static navigate(app: Vue, path: string) {
		const current = RouterHandler.currentPath(app)
		if (current != path) {
			app.$router.push(path)
		}
	}
}

export default router
