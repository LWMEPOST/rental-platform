<template>
  <div class="order-manage">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="fetchOrders">
        <el-option label="待支付" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="租赁中" :value="2" />
        <el-option label="待归还" :value="3" />
        <el-option label="已完成" :value="4" />
        <el-option label="已取消" :value="5" />
      </el-select>
      
      <el-input
        v-model="searchOrderNo"
        placeholder="搜索订单号"
        style="width: 200px; margin-left: 10px;"
        @keyup.enter="fetchOrders"
      >
        <template #append>
          <el-button @click="fetchOrders"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="orders" v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="deviceId" label="设备ID" width="80" />
      <el-table-column label="租期" width="200">
          <template #default="scope">
              {{ formatTime(scope.row.startTime) }} <br/> 至 {{ formatTime(scope.row.endTime) }}
          </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 1"
            size="small"
            type="primary"
            @click="updateStatus(scope.row, 2)"
          >
            发货
          </el-button>
          <el-button
            v-if="scope.row.status === 3 || scope.row.status === 2"
            size="small"
            type="success"
            @click="updateStatus(scope.row, 4)"
          >
            确认归还
          </el-button>
          <!-- More actions like Cancel could be added -->
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: right;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const searchOrderNo = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchOrders = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            orderNo: searchOrderNo.value,
            status: filterStatus.value === '' ? null : filterStatus.value
        }
        const res = await request.get('/order/admin/list', { params })
        if (res.code === 200) {
            orders.value = res.data.records
            total.value = res.data.total
        }
    } catch (e) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handlePageChange = (page) => {
    currentPage.value = page
    fetchOrders()
}

const updateStatus = (row, newStatus) => {
    let action = ''
    if (newStatus === 2) action = '发货'
    if (newStatus === 4) action = '确认归还'
    
    ElMessageBox.confirm(`确定执行 ${action} 操作吗？`, '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await request.put('/order/admin/updateStatus', { orderId: row.id, status: newStatus })
            ElMessage.success('操作成功')
            fetchOrders()
        } catch (e) {
            ElMessage.error('操作失败')
        }
    })
}

const formatTime = (time) => {
    if(!time) return ''
    return time.replace('T', ' ').substring(0, 16)
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

const getStatusType = (status) => {
    if (status === 0) return 'danger'
    if (status === 2) return 'primary'
    if (status === 4) return 'success'
    return 'info'
}

onMounted(() => {
    fetchOrders()
})
</script>

<style scoped>
.toolbar {
    display: flex;
    align-items: center;
}
</style>
