<template>
  <el-container v-if="!isAdminRoute">
    <el-header class="app-header">
      <div class="header-content">
        <div class="nav-left">
          <el-button
            v-if="showBackButton"
            link
            class="back-button"
            @click="handleBack"
          >
            {{ backButtonText }}
          </el-button>
          <h2 class="logo" @click="router.push('/')">租赁平台</h2>
        </div>
        <div class="nav-right">
          <template v-if="user">
            <span class="username">欢迎，{{ user.nickname || user.username }}</span>
            <el-button link @click="router.push('/user/profile')">个人中心</el-button>
            <el-button link @click="router.push('/favorite/list')">我的收藏</el-button>
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
import { computed, ref, watch } from 'vue'

const router = useRouter()
const route = useRoute()

const readStoredUser = () => {
    try {
        const raw = localStorage.getItem('user')
        return raw ? JSON.parse(raw) : null
    } catch (error) {
        localStorage.removeItem('user')
        return null
    }
}

const user = ref(readStoredUser())

const isAdminRoute = computed(() => {
    return route.path.startsWith('/admin')
})

const showBackButton = computed(() => {
    return route.path !== '/' && route.path !== '/login'
})

const isAdminUser = computed(() => {
    return user.value?.role === 'ADMIN'
})

const backButtonText = computed(() => {
    return isAdminUser.value ? '返回控制台' : '返回'
})

const handleBack = () => {
    if (isAdminUser.value) {
        router.push('/admin/dashboard')
        return
    }
    if (window.history.length > 1) {
        router.back()
    } else {
        router.push('/')
    }
}

watch(
    () => route.fullPath,
    () => {
        user.value = readStoredUser()
    },
    { immediate: true }
)

const handleLogout = async () => {
    localStorage.removeItem('user')
    user.value = null
    if (route.path !== '/') {
        await router.replace('/')
    }
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

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  cursor: pointer;
  color: #409eff;
  margin: 0;
}

.back-button {
  font-size: 14px;
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
