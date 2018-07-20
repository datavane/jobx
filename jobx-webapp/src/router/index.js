import Vue from 'vue'
import Router from 'vue-router'
import routes from './routers'
import store from '@/store'

Vue.use(Router)
const router = new Router({
    routes,
    mode: 'history'
})

router.beforeEach((to, from, next) => {
  store.dispatch('toggleLoading', true)
  if (store.state.user || storage.get(constant.keys.user)) {
    next()
    setTimeout(() => {
      store.dispatch('toggleLoading', false)
    }, 600)
  } else {
    if (to.name == 'login') {
      next()
    } else {
      next('/login')
    }
  }
})