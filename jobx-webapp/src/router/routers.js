export default [
  {
    path: '/',
    redirect: '/login'
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('@/views/login/Login')
  },
  {
    name: 'dashboard',
    path: '/dashboard',
    component: () => import('@/views/dashboard/Dashboard')
  },
  {
    name: 'agent',
    path: '/agent',
    component: () => import('@/views/agent/View'),
    children: [
      {
        path: '/agent/add',
        component: () => import('@/views/agent/Add')
      }]
  },
  {
    name: 'group',
    path: '/group',
    component: () => import('@/views/group/View'),
    children: [
      {
        path: '/group/add',
        component: () => import('@/views/group/Add')
      }]
  },
  {
    path:'/job',
    redirect:'/job/view'
  },
  {
    name:'job',
    path: '/job/view',
    component: () => import('@/views/job/View')
  },
  {
    name:'job',
    path: '/job/edit',
    component: () => import('@/views/job/Edit')
  },
  {
    name:'job',
    path: '/job/add',
    component: () => import('@/views/job/Add')
  },
  {
    path:'/profile',
    redirect:'/profile/view'
  },
  {
    name:'profile',
    path: '/profile/view',
    component: () => import('@/views/profile/View')
  },
  {
    name:'profile',
    path: '/profile/edit',
    component: () => import('@/views/profile/Edit')
  }
]
