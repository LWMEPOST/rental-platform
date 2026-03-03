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
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/device/:id',
    name: 'DeviceDetail',
    component: () => import('../views/DeviceDetail.vue')
  },
  {
    path: '/order/confirm/:deviceId',
    name: 'OrderConfirm',
    component: () => import('../views/OrderConfirm.vue')
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue')
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue')
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
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

export default router
