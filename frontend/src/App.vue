<template>
  <el-container v-if="!isAdminRoute">
    <el-header class="app-header">
      <div class="header-content">
        <h2 class="logo" @click="router.push('/')">租赁平台</h2>
        <div class="nav-right">
          <template v-if="user">
            <span class="username">欢迎, {{ user.nickname || user.username }}</span>
            <el-button link @click="router.push('/user/profile')">个人中心</el-button>
            <el-button link @click="router.push('/order/list')">我的订单</el-button>
            <el-button link @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button link @click="router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
  
  <!-- For admin routes, we render router-view directly without client header -->
  <router-view v-else></router-view>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'

const router = useRouter()
const route = useRoute()

const user = computed(() => {
    const u = localStorage.getItem('user')
    return u ? JSON.parse(u) : null
})

const isAdminRoute = computed(() => {
    return route.path.startsWith('/admin')
})

const handleLogout = () => {
    localStorage.removeItem('user')
    router.push('/login')
    window.location.reload()
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

.app-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  cursor: pointer;
  color: #409eff;
  margin: 0;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #666;
}
</style>
