import { createApp } from "vue"
import router from '@/router'
import Oruga from '@oruga-ui/oruga-next'
import App from "@/App.vue"
import "@oruga-ui/oruga-next/dist/oruga.css"

createApp(App)
	.use(router)
	.use(Oruga)
	.mount('#app')
