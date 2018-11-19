export default [
  {
    path: '/',
    redirect: '/login'
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('@/components/login/Login')
  },
  {
    name: 'dashboard',
    path: '/dashboard',
    component: () => import('@/components/dashboard/Dashboard')
  },
  {
    name: 'agent',
    path: '/agent',
    component: () => import('@/components/agent/View'),
    children: [
      {
        path: '/agent/add',
        component: () => import('@/components/agent/Add')
      }]
  },
  {
    name: 'group',
    path: '/group',
    component: () => import('@/components/group/View'),
    children: [
      {
        path: '/group/add',
        component: () => import('@/components/group/Add')
      }]
  },
  {
    path:'/job',
    redirect:'/job/view'
  },
  {
    name:'job',
    path: '/job/view',
    component: () => import('@/components/job/View')
  },
  {
    name:'job',
    path: '/job/edit',
    component: () => import('@/components/job/Edit')
  },
  {
    name:'job',
    path: '/job/add',
    component: () => import('@/components/job/Add')
  },
  {
    path:'/profile',
    redirect:'/profile/view'
  },
  {
    name:'profile',
    path: '/profile/view',
    component: () => import('@/components/profile/View')
  },
  {
    name:'profile',
    path: '/profile/edit',
    component: () => import('@/components/profile/Edit')
  }
]
