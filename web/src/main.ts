import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import { RouterHandler } from '@/router'
import MenuHeader from '@/components/MenuHeader.vue'
import Buefy from 'buefy'
import 'buefy/dist/buefy.css'

Vue.component("MenuHeader", MenuHeader)
Vue.use(Buefy)

Vue.config.productionTip = false

const vueApp = new Vue({
	router,
	render: h => h(App)
}).$mount('#app')
