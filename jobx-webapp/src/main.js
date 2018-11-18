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

import 'select2/dist/css/select2.css'
import 'select2/dist/js/select2.full.js'
import '../static/css/select2.css'

import 'bootstrap/dist/js/bootstrap.min.js'
import 'material-design-iconic-font/dist/css/material-design-iconic-font.css'
import 'jquery.scrollbar/jquery.scrollbar.css'
import 'jquery.scrollbar/jquery.scrollbar.js'
import 'jquery-scroll-lock'
import 'fullcalendar'
import 'popper.js'
import 'autosize'


import MuseUI from 'muse-ui'
Vue.use(MuseUI)
import 'muse-ui/dist/muse-ui.css'
import '../static/css/muse.scss'


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

