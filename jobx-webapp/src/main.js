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

import 'bootstrap/dist/js/bootstrap.min.js'
import 'material-design-iconic-font/dist/css/material-design-iconic-font.css'
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
Vue.use(plugin)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})

