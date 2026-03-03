<template>
  <div class="auth-audit-container">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="审核状态" clearable @change="fetchList">
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已驳回" :value="2" />
      </el-select>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="idCard" label="身份证号" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="180">
          <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <div v-if="scope.row.status === 0">
            <el-button size="small" type="success" @click="handleAudit(scope.row, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="openRejectDialog(scope.row)">驳回</el-button>
          </div>
          <span v-else class="audit-info">{{ scope.row.auditRemark || '无备注' }}</span>
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

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectDialogVisible" title="驳回原因">
      <el-input v-model="rejectReason" type="textarea" placeholder="请输入驳回原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const filterStatus = ref(0) // Default to pending
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentAuthId = ref(null)

const fetchList = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            status: filterStatus.value === '' ? null : filterStatus.value
        }
        const res = await request.get('/auth/identity/admin/list', { params })
        if (res.code === 200) {
            list.value = res.data.records
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
    fetchList()
}

const handleAudit = (row, status) => {
    ElMessageBox.confirm('确定通过该用户的实名认证申请吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await request.post('/auth/identity/audit', {
                authId: row.id,
                status: 1,
                remark: '审核通过'
            })
            ElMessage.success('操作成功')
            fetchList()
        } catch (e) {
            ElMessage.error('操作失败')
        }
    })
}

const openRejectDialog = (row) => {
    currentAuthId.value = row.id
    rejectReason.value = ''
    rejectDialogVisible.value = true
}

const submitReject = async () => {
    if (!rejectReason.value) {
        ElMessage.warning('请输入驳回原因')
        return
    }
    try {
        await request.post('/auth/identity/audit', {
            authId: currentAuthId.value,
            status: 2,
            remark: rejectReason.value
        })
        ElMessage.success('操作成功')
        rejectDialogVisible.value = false
        fetchList()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

const formatTime = (time) => time ? time.replace('T', ' ') : ''
const getStatusText = (status) => ['待审核', '已通过', '已驳回'][status]
const getStatusType = (status) => ['warning', 'success', 'danger'][status]

onMounted(() => {
    fetchList()
})
</script>

<style scoped>
.audit-info {
    font-size: 12px;
    color: #999;
}
</style>
