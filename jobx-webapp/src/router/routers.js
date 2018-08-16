export default [
  {
    path: '/',
    redirect: 'login'
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
    name: 'profile',
    path: '/profile',
    component: () => import('@/components/profile/Profile')
  },
  {
    name: 'config',
    path: '/config',
    component: () => import('@/components/config/Config')
  }
]
