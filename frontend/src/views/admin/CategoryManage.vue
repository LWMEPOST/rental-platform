<template>
  <div class="category-manage">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增分类</el-button>
    </div>

    <el-table :data="categories" v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">{{ formatTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
    id: null,
    name: '',
    sortOrder: 0
})

const fetchCategories = async () => {
    loading.value = true
    try {
        const res = await request.get('/category/list')
        if (res.code === 200) {
            categories.value = res.data
        }
    } catch (e) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleAdd = () => {
    isEdit.value = false
    form.id = null
    form.name = ''
    form.sortOrder = 0
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该分类吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/category/delete/${row.id}`)
            ElMessage.success('删除成功')
            fetchCategories()
        } catch (e) {
            ElMessage.error('删除失败')
        }
    })
}

const submitForm = async () => {
    if (!form.name) {
        ElMessage.warning('请输入分类名称')
        return
    }
    try {
        if (isEdit.value) {
            await request.put('/category/update', form)
        } else {
            await request.post('/category/add', form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchCategories()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

const formatTime = (time) => time ? time.replace('T', ' ') : ''

onMounted(() => {
    fetchCategories()
})
</script>

<style scoped>
.toolbar {
    display: flex;
    justify-content: flex-start;
}
</style>
