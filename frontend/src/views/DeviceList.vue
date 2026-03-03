<template>
  <div class="device-list-container">
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索设备..."
        class="input-with-select"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="filter-section">
      <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button 
          v-for="category in categories" 
          :key="category.id" 
          :label="category.id"
        >
          {{ category.name }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <div class="device-grid" v-loading="loading">
      <el-empty v-if="devices.length === 0" description="暂无设备" />
      <el-card
        v-for="device in devices"
        :key="device.id"
        class="device-card"
        @click="goToDetail(device.id)"
        shadow="hover"
      >
        <img :src="device.mainImage" class="device-image" />
        <div class="device-info">
          <h3 class="device-name">{{ device.name }}</h3>
          <p class="device-desc">{{ device.description }}</p>
          <div class="price-row">
            <span class="price">¥{{ device.rentalPrice }}/天</span>
            <span class="stock">库存: {{ device.stockQuantity }}</span>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const devices = ref([])
const categories = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchCategories = async () => {
  try {
    const res = await request.get('/category/list')
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('Failed to fetch categories', error)
  }
}

const fetchDevices = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: selectedCategory.value || undefined,
      status: 1 // Only show on-shelf devices for client
    }
    const res = await request.get('/device/list', { params })
    if (res.code === 200) {
      devices.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchDevices()
}

const handleCategoryChange = () => {
  currentPage.value = 1
  fetchDevices()
}

const handlePageChange = (val) => {
  currentPage.value = val
  fetchDevices()
}

const goToDetail = (id) => {
  router.push(`/device/${id}`)
}

onMounted(() => {
  fetchCategories()
  fetchDevices()
})
</script>

<style scoped>
.device-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.search-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}
.input-with-select {
  width: 500px;
}
.filter-section {
  margin-bottom: 20px;
  text-align: center;
}
.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  min-height: 300px;
}
.device-card {
  cursor: pointer;
  transition: transform 0.3s;
}
.device-card:hover {
  transform: translateY(-5px);
}
.device-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}
.device-info {
  padding: 10px 0;
}
.device-name {
  font-size: 16px;
  margin: 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.device-desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
  height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}
.stock {
  font-size: 12px;
  color: #999;
}
.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>
