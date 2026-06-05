<template>
  <div class="comment-manage">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名或评论内容"
        style="width: 260px"
        @keyup.enter="fetchComments"
      >
        <template #append>
          <el-button @click="fetchComments">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>

      <el-input
        v-model="orderNo"
        placeholder="搜索订单号"
        style="width: 220px"
        clearable
        @keyup.enter="fetchComments"
      />
    </div>

    <el-table :data="comments" v-loading="loading" style="width: 100%; margin-top: 20px">
      <el-table-column prop="orderNo" label="订单号" min-width="180" show-overflow-tooltip />
      <el-table-column prop="deviceName" label="设备名称" min-width="220" show-overflow-tooltip />
      <el-table-column prop="username" label="用户" width="140" />
      <el-table-column label="评分" width="160">
        <template #default="scope">
          <el-rate :model-value="scope.row.rating" disabled show-score text-color="#ff9900" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="280" show-overflow-tooltip />
      <el-table-column label="时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button type="danger" plain size="small" @click="handleDelete(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const loading = ref(false)
const keyword = ref('')
const orderNo = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchComments = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || null,
      orderNo: orderNo.value.trim() || null
    }
    const res = await request.get('/comment/admin/list', { params })
    if (res.code === 200) {
      comments.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchComments()
}

const handleDelete = (commentId) => {
  ElMessageBox.confirm('确定删除这条评论吗？删除后不可恢复。', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/comment/admin/${commentId}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchComments()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
