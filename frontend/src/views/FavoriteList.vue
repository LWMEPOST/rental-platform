<template>
  <div class="favorite-list-container" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
        </div>
      </template>
      
      <el-empty v-if="favorites.length === 0" description="暂无收藏设备" />
      
      <div v-else class="favorite-grid">
        <el-row :gutter="20">
          <el-col :span="6" v-for="device in favorites" :key="device.id" style="margin-bottom: 20px;">
            <el-card :body-style="{ padding: '0px' }" class="device-card" shadow="hover" @click="goToDetail(device.id)">
              <img :src="device.mainImage" class="image" />
              <div style="padding: 14px;">
                <div class="device-name">{{ device.name }}</div>
                <div class="device-price">
                  <span class="price-val">¥{{ device.rentalPrice }}</span><span class="unit">/天</span>
                </div>
                <div class="device-action">
                   <el-button type="danger" link @click.stop="handleRemove(device.id)">取消收藏</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const favorites = ref([])
const loading = ref(false)

const fetchFavorites = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    const user = JSON.parse(userStr)
    const res = await request.get(`/favorite/list/${user.id}`)
    if (res.code === 200) {
      favorites.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/device/${id}`)
}

const handleRemove = async (deviceId) => {
    try {
        await ElMessageBox.confirm('确定要取消收藏该设备吗？', '提示', { type: 'warning' })
        const user = JSON.parse(localStorage.getItem('user'))
        const res = await request.post('/favorite/toggle', {
            userId: user.id,
            deviceId: deviceId
        })
        if (res.code === 200) {
            ElMessage.success('已取消收藏')
            fetchFavorites()
        }
    } catch (e) {
        if(e !== 'cancel') {
            console.error(e)
        }
    }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorite-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.device-card {
  cursor: pointer;
  transition: all 0.3s;
}

.device-card:hover {
  transform: translateY(-5px);
}

.image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.device-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.device-price {
  color: #f56c6c;
  margin-bottom: 10px;
}

.price-val {
  font-size: 18px;
  font-weight: bold;
}

.unit {
  font-size: 12px;
  color: #999;
}

.device-action {
    text-align: right;
}
</style>
