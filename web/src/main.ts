import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import { RouterHandler } from '@/router'
import MenuHeader from '@/components/MenuHeader.vue'

Vue.component("MenuHeader", MenuHeader)

Vue.config.productionTip = false

const vueApp = new Vue({
	router,
	render: h => h(App)
}).$mount('#app')
