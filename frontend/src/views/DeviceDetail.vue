<template>
  <div class="device-detail-container" v-loading="loading">
    <div v-if="device" class="device-content">
      <el-row :gutter="40">
        <el-col :span="12">
          <img :src="device.mainImage" class="main-image" />
          <!-- Thumbnails could go here if detailImages were parsed -->
        </el-col>
        <el-col :span="12">
          <h1 class="title">{{ device.name }}</h1>
          <div class="tags">
            <el-tag>{{ device.brand }}</el-tag>
            <el-tag type="info" class="ml-2">{{ device.model }}</el-tag>
          </div>
          <div class="price-box">
            <span class="label">租金：</span>
            <span class="price">¥{{ device.rentalPrice }}/天</span>
            <div class="deposit">押金：¥{{ device.depositAmount }}</div>
          </div>
          
          <div class="meta-info">
            <p><strong>库存：</strong> {{ device.stockQuantity }} 件</p>
            <p><strong>描述：</strong> {{ device.description }}</p>
          </div>

          <div class="actions">
            <el-button type="primary" size="large" @click="handleRent">立即预约</el-button>
            <el-button size="large">加入收藏</el-button>
          </div>
        </el-col>
      </el-row>

      <el-tabs class="mt-4">
        <el-tab-pane label="详细参数">
            <p>规格参数暂未展示 (JSON格式)</p>
        </el-tab-pane>
        <el-tab-pane label="用户评价">
            <div v-if="reviews.length > 0" class="reviews-list">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                    <div class="review-header">
                        <span class="username">{{ review.username }}</span>
                        <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" />
                        <span class="time">{{ formatTime(review.createTime) }}</span>
                    </div>
                    <div class="review-content">{{ review.content }}</div>
                </div>
            </div>
            <el-empty v-else description="暂无评价" />
        </el-tab-pane>
        <el-tab-pane label="租赁须知">
            <p>1. 租期计算：以签收后的次日0点开始计算...</p>
            <p>2. 归还规则：租期结束当日寄出...</p>
        </el-tab-pane>
      </el-tabs>
    </div>
    <el-empty v-else description="未找到设备信息" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const deviceId = route.params.id
const device = ref(null)
const loading = ref(false)
const reviews = ref([])

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/device/${deviceId}`)
    if (res.code === 200) {
      device.value = res.data
    } else {
      ElMessage.error(res.message || '获取详情失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
    try {
        const res = await request.get(`/comment/list/${deviceId}`)
        if (res.code === 200) {
            reviews.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

const handleRent = () => {
    // Check login
    const user = localStorage.getItem('user')
    if (!user) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
    }
    // Proceed to order page
    router.push(`/order/confirm/${deviceId}`)
}

const formatTime = (time) => {
    if(!time) return ''
    return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  fetchDetail()
  fetchReviews()
})
</script>

<style scoped>
.device-detail-container {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}
.main-image {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.title {
  font-size: 28px;
  margin-bottom: 20px;
}
.tags {
  margin-bottom: 20px;
}
.ml-2 {
  margin-left: 10px;
}
.price-box {
  background-color: #fdf5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}
.label {
  color: #666;
}
.price {
  color: #f56c6c;
  font-size: 32px;
  font-weight: bold;
}
.deposit {
  color: #999;
  font-size: 14px;
  margin-top: 5px;
}
.meta-info {
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}
.actions {
  display: flex;
  gap: 20px;
}
.mt-4 {
  margin-top: 40px;
}
.review-item {
    padding: 20px 0;
    border-bottom: 1px solid #eee;
}
.review-header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 10px;
}
.review-header .username {
    font-weight: bold;
}
.review-header .time {
    color: #999;
    font-size: 12px;
    margin-left: auto;
}
.review-content {
    line-height: 1.6;
    color: #333;
}
</style>
