<template>
  <div class="device-manage">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增设备</el-button>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索设备名称"
        style="width: 200px; margin-left: 10px;"
        @keyup.enter="fetchDevices"
      >
        <template #append>
          <el-button @click="fetchDevices"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="devices" v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="100">
        <template #default="scope">
          <img :src="scope.row.mainImage" style="width: 50px; height: 50px; object-fit: cover;" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="设备名称" show-overflow-tooltip />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column prop="rentalPrice" label="日租金" width="100">
        <template #default="scope">¥{{ scope.row.rentalPrice }}</template>
      </el-table-column>
      <el-table-column prop="stockQuantity" label="库存" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button
            size="small"
            :type="scope.row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑设备' : '新增设备'"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="form.brand" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryIds" multiple placeholder="请选择分类(可多选)">
            <el-option 
                v-for="item in categories" 
                :key="item.id" 
                :label="item.name" 
                :value="item.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="主图URL">
          <el-input v-model="form.mainImage" />
        </el-form-item>
        <el-form-item label="租金">
          <el-input-number v-model="form.rentalPrice" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="押金">
          <el-input-number v-model="form.depositAmount" :precision="2" :step="100" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stockQuantity" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import request from '../../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const devices = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
    id: null,
    name: '',
    brand: '',
    categoryIds: [],
    mainImage: '',
    rentalPrice: 0,
    depositAmount: 0,
    stockQuantity: 0,
    description: '',
    status: 1
})

const categories = ref([])

const fetchCategories = async () => {
    try {
        const res = await request.get('/category/list')
        if (res.code === 200) {
            categories.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

const fetchDevices = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            keyword: searchKeyword.value
        }
        const res = await request.get('/device/admin/list', { params })
        if (res.code === 200) {
            devices.value = res.data.records
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
    fetchDevices()
}

const handleAdd = () => {
    isEdit.value = false
    Object.assign(form, {
        id: null,
        name: '',
        brand: '',
        categoryIds: [],
        mainImage: '',
        rentalPrice: 0,
        depositAmount: 0,
        stockQuantity: 0,
        description: '',
        status: 1
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该设备吗？', '提示', {
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/device/delete/${row.id}`)
            ElMessage.success('删除成功')
            fetchDevices()
        } catch (e) {
            ElMessage.error('删除失败')
        }
    })
}

const toggleStatus = async (row) => {
    try {
        const newStatus = row.status === 1 ? 0 : 1
        const payload = { ...row, status: newStatus }
        await request.put('/device/update', payload)
        ElMessage.success('操作成功')
        fetchDevices()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

const submitForm = async () => {
    try {
        if (isEdit.value) {
            await request.put('/device/update', form)
        } else {
            await request.post('/device/add', form)
        }
        ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
        dialogVisible.value = false
        fetchDevices()
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

onMounted(() => {
    fetchCategories()
    fetchDevices()
})
</script>

<style scoped>
.toolbar {
    display: flex;
    align-items: center;
}
</style>
