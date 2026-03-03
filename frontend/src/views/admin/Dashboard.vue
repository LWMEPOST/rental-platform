<template>
  <div class="dashboard-container" v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>总订单量</template>
          <div class="card-value">{{ stats.totalOrders }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>总收入</template>
          <div class="card-value">¥ {{ stats.totalRevenue }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>在租设备</template>
          <div class="card-value">{{ stats.rentingDevices }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>注册用户</template>
          <div class="card-value">{{ stats.userCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="mt-4">
      <template #header>待处理事项</template>
      <el-empty v-if="stats.todos.length === 0" description="暂无待办事项" />
      <el-table v-else :data="stats.todos" style="width: 100%">
        <el-table-column prop="title" label="事项" />
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
             <template #default="scope">
                <el-tag type="warning">{{ scope.row.status }}</el-tag>
             </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
            <template #default="scope">
                <el-button link type="primary" @click="handleTodo(scope.row)">处理</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const stats = reactive({
    totalOrders: 0,
    totalRevenue: 0,
    rentingDevices: 0,
    userCount: 0,
    todos: []
})

const fetchStats = async () => {
    loading.value = true
    try {
        const res = await request.get('/dashboard/stats')
        if (res.code === 200) {
            Object.assign(stats, res.data)
        }
    } catch (e) {
        ElMessage.error('加载数据失败')
    } finally {
        loading.value = false
    }
}

const handleTodo = (item) => {
    // Basic navigation based on todo content
    if (item.status === '待发货' || item.status === '待处理') {
        router.push('/admin/order')
    } else if (item.status === '待补货') {
        router.push('/admin/device')
    }
}

onMounted(() => {
    fetchStats()
})
</script>

<style scoped>
.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.mt-4 {
  margin-top: 20px;
}
</style>
