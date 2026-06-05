import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/DeviceList.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { guestOnly: true }
  },
  {
    path: '/device/:id',
    name: 'DeviceDetail',
    component: () => import('../views/DeviceDetail.vue')
  },
  {
    path: '/order/confirm/:deviceId',
    name: 'OrderConfirm',
    component: () => import('../views/OrderConfirm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/favorite/list',
    name: 'FavoriteList',
    component: () => import('../views/FavoriteList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    redirect: '/admin/dashboard',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, adminOnly: true },
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'device',
        component: () => import('../views/admin/DeviceManage.vue')
      },
      {
        path: 'category',
        component: () => import('../views/admin/CategoryManage.vue')
      },
      {
        path: 'user',
        component: () => import('../views/admin/UserManage.vue')
      },
      {
        path: 'order',
        component: () => import('../views/admin/OrderManage.vue')
      },
      {
        path: 'comment',
        component: () => import('../views/admin/CommentManage.vue')
      },
      {
        path: 'analysis',
        component: () => import('../views/admin/Analysis.vue')
      },
      {
        path: 'account', // Auth Audit is part of User/Account management
        component: () => import('../views/admin/AuthAudit.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem('user')
    return raw ? JSON.parse(raw) : null
  } catch (error) {
    localStorage.removeItem('user')
    return null
  }
}

router.beforeEach((to) => {
  const user = readStoredUser()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const guestOnly = to.matched.some(record => record.meta.guestOnly)
  const adminOnly = to.matched.some(record => record.meta.adminOnly)

  if (requiresAuth && !user) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  if (adminOnly && user?.role !== 'ADMIN') {
    return { path: '/' }
  }

  if (guestOnly && user) {
    return { path: user.role === 'ADMIN' ? '/admin/dashboard' : '/' }
  }

  return true
})

export default router
