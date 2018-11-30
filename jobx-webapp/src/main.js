// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from '@/App'
import router from '@/router'

import storage from '@/utils/storage.js'
import constant from '@/utils/constant.js'
import http from '@/utils/http.js'
import plugin from '@/plugins/plugins.js'
import store from '@/store'
import VueSweetalert2 from 'vue-sweetalert2'

import flatPickr from 'vue-flatpickr-component'
import 'flatpickr/dist/flatpickr.css'

//Ant
//import Ant from 'ant-design-vue'
//import 'ant-design-vue/dist/antd.css'
//import '../static/css/ant.scss'
//Vue.use(Ant)

//Vuetify
import Vuetify from 'vuetify'
import './stylus/vuetify.styl'
Vue.use(Vuetify, {
  iconfont: 'material-icons',
  icons: {
    'cancel': 'mdi-cancel',
    'detail':'mdi-eye',
    'edit':'mdi-playlist-edit',
    'add':'mdi-plus-box',
    'delete':'mdi-delete',
    'play':'mdi-play'
  }
})

//MuseUI
//import MuseUI from 'muse-ui'
//Vue.use(MuseUI)
//import 'muse-ui/dist/muse-ui.css'
//import '../static/css/muse.scss'

//select2

import 'select2/dist/css/select2.css'
import 'select2/dist/js/select2.full.js'
import '../static/css/select2.css'


//其他js依赖包
import 'bootstrap/dist/js/bootstrap.min.js'
import 'material-design-iconic-font/dist/css/material-design-iconic-font.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import 'jquery.scrollbar/jquery.scrollbar.css'
import 'jquery.scrollbar/jquery.scrollbar.js'
import 'jquery-scroll-lock'
import 'fullcalendar'
import 'popper.js'
import 'autosize'


Vue.config.productionTip = false
Vue.prototype.$storage = storage
Vue.prototype.$const = constant

Vue.prototype.$http = http
Vue.use(VueSweetalert2)
Vue.use(flatPickr)
Vue.use(plugin)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})

