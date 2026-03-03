<template>
  <div class="order-detail-container" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单详情</span>
          <el-button link @click="router.back()">返回</el-button>
        </div>
      </template>

      <div v-if="order">
        <div class="status-steps">
          <el-steps :active="activeStep" finish-status="success" align-center>
            <el-step title="待支付" />
            <el-step title="待发货" />
            <el-step title="租赁中" />
            <el-step title="待归还" />
            <el-step title="已完成" />
          </el-steps>
        </div>

        <el-descriptions title="基本信息" border :column="2" class="mt-4">
          <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
             <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(order.payTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ formatTime(order.deliveryTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="归还时间">{{ formatTime(order.returnTime) || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="租赁信息" border :column="2" class="mt-4">
          <el-descriptions-item label="设备ID">{{ order.deviceId }}</el-descriptions-item>
          <el-descriptions-item label="租期">
             {{ formatTime(order.startTime) }} 至 {{ formatTime(order.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="租金总额">¥{{ order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="押金">¥{{ order.depositAmount }}</el-descriptions-item>
        </el-descriptions>

        <div class="actions mt-4">
           <el-button v-if="order.status === 0" type="primary" @click="handlePay">立即支付</el-button>
           <el-button v-if="order.status === 2" type="success" @click="handleReturn">申请归还</el-button>
           <el-button v-if="order.status === 4" @click="openReviewDialog">评价订单</el-button>
        </div>
      </div>
      <el-empty v-else description="订单不存在" />
    </el-card>

    <!-- Review Dialog (Reuse logic or component, here simplified duplicate) -->
    <el-dialog v-model="reviewDialogVisible" title="评价设备">
      <el-form :model="reviewForm">
        <el-form-item label="评分">
           <el-rate v-model="reviewForm.rating" show-score text-color="#ff9900" />
        </el-form-item>
        <el-form-item label="评价内容">
           <el-input v-model="reviewForm.content" type="textarea" />
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
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const orderId = route.params.id
const order = ref(null)
const loading = ref(false)

const reviewDialogVisible = ref(false)
const reviewForm = reactive({
    rating: 5,
    content: ''
})

const activeStep = computed(() => {
    if (!order.value) return 0
    const status = order.value.status
    if (status === 5) return 0 // Cancelled
    if (status === 0) return 0
    if (status === 1) return 1
    if (status === 2) return 2
    if (status === 3) return 3
    if (status === 4) return 5 // Completed
    return 0
})

const fetchDetail = async () => {
    loading.value = true
    try {
        const res = await request.get(`/order/detail/${orderId}`)
        if (res.code === 200) {
            order.value = res.data
        } else {
            ElMessage.error(res.message)
        }
    } catch (e) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handlePay = async () => {
    try {
        await request.post(`/order/pay/${orderId}`)
        ElMessage.success('支付成功')
        fetchDetail()
    } catch (e) {
        ElMessage.error('支付失败')
    }
}

const handleReturn = async () => {
    try {
        await request.post(`/order/return/${orderId}`)
        ElMessage.success('申请成功')
        fetchDetail()
    } catch (e) {
        ElMessage.error('申请失败')
    }
}

const openReviewDialog = () => {
    reviewDialogVisible.value = true
}

const submitReview = async () => {
    const user = JSON.parse(localStorage.getItem('user'))
    try {
        const payload = {
            userId: user.id,
            orderId: order.value.id,
            deviceId: order.value.deviceId,
            rating: reviewForm.rating,
            content: reviewForm.content
        }
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

const formatTime = (time) => time ? time.replace('T', ' ') : ''
const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '待发货', 2: '租赁中', 3: '待归还', 4: '已完成', 5: '已取消' }
  return map[status] || '未知'
}
const getStatusType = (status) => {
  if (status === 0) return 'danger'
  if (status === 2) return 'primary'
  if (status === 4) return 'success'
  return 'info'
}

onMounted(() => {
    fetchDetail()
})
</script>

<style scoped>
.order-detail-container {
    max-width: 1000px;
    margin: 20px auto;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.mt-4 {
    margin-top: 20px;
}
.status-steps {
    padding: 20px 0;
}
.actions {
    display: flex;
    justify-content: flex-end;
}
</style>
