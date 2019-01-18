import Vue from 'vue'
import Router from 'vue-router'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
**/
export const constantRouterMap = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/404', component: () => import('@/views/404'), hidden: true },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: 'Dashboard',
    hidden: true,
    children: [{
      path: 'dashboard',
      component: () => import('@/views/dashboard/index')
    }]
  },
  {
    path: '/job',
    component: Layout,
    name:'作业管理',
    redirect: '/job/view',
    children: [
      {
        path: 'view',
        name: '作业',
        component: () => import('@/views/job/view'),
        meta: { title: '作业管理', icon: 'example' }
      },
      {
        hidden:true,
        path: 'add',
        name: '添加作业',
        component: () => import('@/views/job/add'),
        meta: { title: '添加作业', icon: 'example' }
      },
      {
        hidden:true,
        path: 'detail',
        name: '作业详情',
        component: () => import('@/views/job/detail'),
        meta: { title: '作业详情', icon: 'example' }
      }
    ]
  },
  {
    path: '/agent',
    component: Layout,
    children: [
      {
        path: 'view',
        name: '执行器',
        component: () => import('@/views/agent/view'),
        meta: { title: '执行器管理', icon: 'example' }
      }
    ]
  },
  {
    path: '/example',
    component: Layout,
    name: 'Example',
    meta: { title: 'Example', icon: 'example' },
    children: [
      {
        path: 'tree',
        name: 'Tree',
        component: () => import('@/views/tree/index'),
        meta: { title: 'Tree', icon: 'tree' }
      }
    ]
  },

  {
    path: '/form',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'Form',
        component: () => import('@/views/form/index'),
        meta: { title: 'Form', icon: 'form' }
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
