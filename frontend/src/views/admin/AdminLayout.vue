<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="admin-aside">
      <div class="logo">管理后台</div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-sub-menu index="device">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>设备管理</span>
          </template>
          <el-menu-item index="/admin/device">设备列表</el-menu-item>
          <el-menu-item index="/admin/category">分类管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>租客管理</span>
          </template>
          <el-menu-item index="/admin/user">租客列表</el-menu-item>
          <el-menu-item index="/admin/account">账号管控</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="order">
          <template #title>
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/order">订单列表</el-menu-item>
          <el-menu-item index="/admin/analysis">运营分析</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
           <!-- Breadcrumb could go here -->
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="el-dropdown-link">
              管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/')">返回前台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
    localStorage.removeItem('user')
    router.push('/')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-aside {
  background-color: #304156;
  color: #fff;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #2b2f3a;
}
.el-menu-vertical {
  border-right: none;
}
.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}
</style>
