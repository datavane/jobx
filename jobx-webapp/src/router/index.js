import Vue from 'vue'
import Router from 'vue-router'
import routes from './routers'

import storage from '@/utils/storage.js'
import constant from '@/utils/constant.js'
import store from '@/store'

Vue.use(Router)
const router = new Router({
    routes,
    mode: 'history'
})

router.beforeEach((to, from, next) => {
  if (to.name == 'login') {
    next()
  }else {
    store.dispatch('toggleLoading', true)
    if (store.state.user || storage.get(constant.keys.user)) {
      next()
      setTimeout(() => {
        store.dispatch('toggleLoading', false)
      }, 600)
    } else {
      //未登录，没有权限访问。。。
      next('/login')
    }
  }
})

export default router
