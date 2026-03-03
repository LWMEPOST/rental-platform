import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api', // Proxy target
  timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    // Add token if available
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // You can adjust this based on your backend response structure
    // if (res.code !== 200) {
    //   ElMessage.error(res.message || 'Error')
    //   return Promise.reject(new Error(res.message || 'Error'))
    // }
    return res
  },
  (error) => {
    console.log('err' + error)
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)

export default service
