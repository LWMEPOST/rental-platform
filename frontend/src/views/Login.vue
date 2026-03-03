<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>登录</span>
        </div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="third-party">
        <el-divider>第三方登录</el-divider>
        <el-space>
            <el-button circle>
                <el-icon><ChatDotRound /></el-icon>
            </el-button>
            <el-button circle>
                <el-icon><Wallet /></el-icon>
            </el-button>
        </el-space>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const form = reactive({
  username: '',
  password: ''
})

const onSubmit = async () => {
  if (!form.username || !form.password) {
      ElMessage.warning('请输入用户名和密码')
      return
  }
  try {
      const res = await request.post('/auth/login', form)
      if (res.code === 200) {
          ElMessage.success('登录成功')
          localStorage.setItem('user', JSON.stringify(res.data))
          if (res.data.role === 'ADMIN') {
              router.push('/admin/dashboard')
          } else {
              router.push('/')
          }
          // Force reload to update header state
          setTimeout(() => {
              window.location.reload()
          }, 100)
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
.third-party {
  text-align: center;
  margin-top: 20px;
}
</style>
