import Vue from 'vue'
import App from './App'

import pageHead from './components/page-head.vue'
import pageFoot from './components/page-foot.vue'

import store from './store'

import ElementUI from './element-ui'
import './element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)

import { DataTables, DataTablesServer } from './vue-data-tables';
Vue.use(DataTables)
Vue.use(DataTablesServer)

import Vant from './vant'
import './vant/lib/index.css'
Vue.use(Vant)

Vue.config.productionTip = false

Vue.prototype.$store = store
Vue.prototype.$backgroundAudioData = {
	playing: false,
	playTime: 0,
	formatedPlayTime: '00:00:00'
}

Vue.component('page-head', pageHead)
Vue.component('page-foot', pageFoot)

App.mpType = 'app'

const app = new Vue({
	store,
    ...App
})
app.$mount()
