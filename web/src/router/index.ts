import Vue from 'vue'
import VueRouter, { RouteConfig } from 'vue-router'
import PageA from '@/views/PageA.vue'
import PageB from '@/views/PageB.vue'
import NotFound from '@/views/NotFound.vue'

Vue.use(VueRouter)

export const URL_LOGIN = '/'
export const URL_HOME = '/home'

const routes: Array<RouteConfig> = [
	{
		path: URL_LOGIN,
		name: 'Login',
		component: PageB
	},
	{
		path: URL_HOME,
		name: 'Home',
		component: PageA
	},
	{
		path: '*',
		name: 'Not found',
		component: NotFound
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
