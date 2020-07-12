import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import { RouterHandler } from '@/router'
import { URL_HOME, URL_LOGIN } from '@/router'

Vue.config.productionTip = false

const vueApp = new Vue({
	router,
	render: h => h(App)
}).$mount('#app')
