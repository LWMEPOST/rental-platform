<template>
  <div class="user-manage">
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名/手机号"
        style="width: 250px;"
        @keyup.enter="fetchUsers"
      >
        <template #append>
          <el-button @click="fetchUsers"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="users" v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
              <el-tag>{{ scope.row.role }}</el-tag>
          </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '冻结' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="实名认证" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.authStatus === 1" type="success">已认证</el-tag>
          <el-tag v-else-if="scope.row.authStatus === 0" type="warning">审核中</el-tag>
          <el-tag v-else-if="scope.row.authStatus === 2" type="danger">已拒绝</el-tag>
          <el-tag v-else type="info">未认证</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="在租设备" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.pendingReturnCount > 0" type="warning">{{ scope.row.pendingReturnCount }} 台</el-tag>
          <el-tag v-else type="success">无</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="scope">
              {{ formatTime(scope.row.createTime) }}
          </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            size="small"
            :type="scope.row.status === 1 ? 'danger' : 'success'"
            @click="toggleStatus(scope.row)"
            :disabled="scope.row.role === 'ADMIN'"
          >
            {{ scope.row.status === 1 ? '冻结' : '解冻' }}
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
      style="margin-top: 20px; text-align: right;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchUsers = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            keyword: searchKeyword.value
        }
        const res = await request.get('/auth/admin/user/list', { params })
        if (res.code === 200) {
            users.value = res.data.records
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
    fetchUsers()
}

const toggleStatus = (row) => {
    const action = row.status === 1 ? '冻结' : '解冻'
    ElMessageBox.confirm(`确定${action}该用户吗？`, '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            const newStatus = row.status === 1 ? 0 : 1
            await request.put('/auth/admin/user/status', { id: row.id, status: newStatus })
            ElMessage.success('操作成功')
            fetchUsers()
        } catch (e) {
            ElMessage.error('操作失败')
        }
    })
}

const formatTime = (time) => {
    if(!time) return ''
    return time.replace('T', ' ')
}

onMounted(() => {
    fetchUsers()
})
</script>

<style scoped>
.toolbar {
    display: flex;
    justify-content: flex-end;
}
</style>
