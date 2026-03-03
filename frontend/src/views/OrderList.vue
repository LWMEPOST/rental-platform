<template>
  <div class="order-list-container" v-loading="loading">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      
      <el-empty v-if="orders.length === 0" description="暂无订单" />
      
      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          
          <div class="order-content">
            <div class="info-row">
              <span class="label">租赁设备ID:</span>
              <span class="value">{{ order.deviceId }}</span>
              <!-- Ideally we should fetch device name, or backend includes it -->
            </div>
            <div class="info-row">
              <span class="label">租赁时间:</span>
              <span class="value">{{ formatTime(order.startTime) }} 至 {{ formatTime(order.endTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">总金额:</span>
              <span class="price">¥{{ order.totalAmount }}</span>
              <span class="deposit">(含押金 ¥{{ order.depositAmount }})</span>
            </div>
          </div>
          
          <div class="order-footer">
            <span class="time">下单时间: {{ formatTime(order.createTime) }}</span>
            <div class="actions">
               <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order.id)">去支付</el-button>
               <el-button v-if="order.status === 2" type="success" size="small" @click="handleReturn(order.id)">归还设备</el-button>
               <el-button v-if="order.status === 4" size="small" @click="openReviewDialog(order)">评价</el-button>
               <el-button size="small" @click="router.push(`/order/detail/${order.id}`)">查看详情</el-button>
            </div>
          </div>
        </div>
        
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="handlePageChange"
          class="pagination"
        />
      </div>
    </el-card>

    <!-- Review Dialog -->
    <el-dialog v-model="reviewDialogVisible" title="评价设备">
      <el-form :model="reviewForm">
        <el-form-item label="评分">
           <el-rate v-model="reviewForm.rating" show-score text-color="#ff9900" />
        </el-form-item>
        <el-form-item label="评价内容">
           <el-input v-model="reviewForm.content" type="textarea" placeholder="分享您的使用体验..." />
        </el-form-item>
      </el-form>
      <template #footer>
         <el-button @click="reviewDialogVisible = false">取消</el-button>
         <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const reviewDialogVisible = ref(false)
const reviewForm = reactive({
    orderId: null,
    deviceId: null,
    rating: 5,
    content: ''
})

const fetchOrders = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      userId: user.id
    }
    const res = await request.get('/order/list', { params })
    if (res.code === 200) {
      orders.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

const handlePay = async (orderId) => {
    try {
        await request.post(`/order/pay/${orderId}`)
        ElMessage.success('支付成功')
        fetchOrders()
    } catch (e) {
        ElMessage.error('支付失败')
    }
}

const handleReturn = async (orderId) => {
    try {
        await request.post(`/order/return/${orderId}`)
        ElMessage.success('申请成功')
        fetchOrders()
    } catch (e) {
        ElMessage.error('申请失败')
    }
}

const openReviewDialog = (order) => {
    reviewForm.orderId = order.id
    reviewForm.deviceId = order.deviceId
    reviewForm.rating = 5
    reviewForm.content = ''
    reviewDialogVisible.value = true
}

const submitReview = async () => {
    const user = JSON.parse(localStorage.getItem('user'))
    try {
        const payload = { ...reviewForm, userId: user.id }
        const res = await request.post('/comment/add', payload)
        if (res.code === 200) {
            ElMessage.success('评价成功')
            reviewDialogVisible.value = false
        } else {
            ElMessage.error(res.message)
        }
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

const getStatusText = (status) => {
  const map = {
    0: '待支付',
    1: '待发货',
    2: '租赁中',
    3: '待归还',
    4: '已完成',
    5: '已取消'
  }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  if (status === 0) return 'text-danger'
  if (status === 2) return 'text-primary'
  if (status === 4) return 'text-success'
  return ''
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}
.order-item {
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 20px;
  padding: 15px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
  padding-bottom: 10px;
  margin-bottom: 10px;
}
.order-no {
  font-weight: bold;
}
.info-row {
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
  margin-left: 5px;
}
.deposit {
  font-size: 12px;
  color: #999;
  margin-left: 5px;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}
.time {
  font-size: 12px;
  color: #999;
}
.text-danger { color: #f56c6c; }
.text-primary { color: #409eff; }
.text-success { color: #67c23a; }
.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
