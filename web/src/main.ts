import { createApp } from "vue"
import router from '@/router'
import Oruga from '@oruga-ui/oruga-next'
import "@oruga-ui/oruga-next/dist/oruga-full-vars.css"
import App from "@/App.vue"

createApp(App)
	.use(router)
	.use(Oruga)
	.mount('#app')
