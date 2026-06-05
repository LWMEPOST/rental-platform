<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>登录</span>
        </div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const route = useRoute()
const form = reactive({
  phone: '',
  password: ''
})

const onSubmit = async () => {
  if (!form.phone || !form.password) {
      ElMessage.warning('请输入手机号和密码')
      return
  }
  try {
      const res = await request.post('/auth/login', form)
      if (res.code === 200) {
          ElMessage.success('登录成功')
          localStorage.setItem('user', JSON.stringify(res.data))
          const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
          if (res.data.role === 'ADMIN') {
              await router.replace(redirect.startsWith('/admin') ? redirect : '/admin/dashboard')
          } else {
              await router.replace(redirect && !redirect.startsWith('/admin') ? redirect : '/')
          }
      } else {
          ElMessage.error(res.message || '登录失败')
      }
  } catch (error) {
      console.error(error)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
.login-card {
  width: 400px;
}
</style>
