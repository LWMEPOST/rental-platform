<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>注册新账号</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="设置密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password />
        </el-form-item>
        <el-form-item prop="agreement">
          <el-checkbox v-model="form.agreement">我已阅读并同意用户协议</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" style="width: 100%">注册</el-button>
        </el-form-item>
        <div class="login-link">
          <el-link type="primary" @click="$router.push('/login')">已有账号？去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const formRef = ref(null)

const form = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  agreement: [{ 
      validator: (rule, value, callback) => {
          if (!value) {
              callback(new Error('请确认用户协议'))
          } else {
              callback()
          }
      }, 
      trigger: 'change' 
  }]
}

const onSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
        try {
            // Note: API needs to be updated to accept phone and create user
            const res = await request.post('/auth/register', {
                username: form.username,
                password: form.password,
                phone: form.phone
            })
            if (res.code === 200) {
                ElMessage.success('注册成功，请登录')
                router.push('/login')
            } else {
                ElMessage.error(res.message || '注册失败')
            }
        } catch (error) {
            console.error(error)
        }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
.register-card {
  width: 450px;
}
.login-link {
  text-align: right;
  margin-top: 10px;
}
</style>
