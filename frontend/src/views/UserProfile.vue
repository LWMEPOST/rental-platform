<template>
  <div class="user-profile-container">
    <el-tabs tab-position="left" style="height: 100%;">
      <el-tab-pane label="基本信息">
        <el-card>
          <template #header>基本信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ user?.phone }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ user?.role }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatTime(user?.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="实名认证">
        <el-card>
          <template #header>实名认证状态</template>
          <div v-if="authInfo">
            <el-alert
              v-if="authInfo.status === 1"
              title="已通过认证"
              type="success"
              show-icon
              :description="`真实姓名：${authInfo.realName} | 身份证号：${maskIdCard(authInfo.idCard)}`"
            />
            <el-alert
              v-else-if="authInfo.status === 0"
              title="审核中"
              type="warning"
              show-icon
              description="您的实名认证申请正在审核中，请耐心等待。"
            />
            <el-alert
              v-else-if="authInfo.status === 2"
              title="认证驳回"
              type="error"
              show-icon
              :description="`原因：${authInfo.auditRemark}。请修改后重新提交。`"
            />
          </div>
          
          <div v-if="!authInfo || authInfo.status === 2" class="mt-4">
            <h3>申请认证</h3>
            <el-form :model="authForm" label-width="100px" style="max-width: 500px;">
              <el-form-item label="真实姓名">
                <el-input v-model="authForm.realName" />
              </el-form-item>
              <el-form-item label="身份证号">
                <el-input v-model="authForm.idCard" />
              </el-form-item>
              <el-form-item label="证件照片">
                 <el-upload action="#" list-type="picture-card" :auto-upload="false" disabled>
                    <el-icon><Plus /></el-icon>
                 </el-upload>
                 <div class="tip">演示模式暂不支持文件上传</div>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitAuth">提交认证</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="收货地址">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>地址管理</span>
              <el-button type="primary" size="small" @click="openAddressDialog()">新增地址</el-button>
            </div>
          </template>
          
          <el-table :data="addresses" style="width: 100%">
            <el-table-column prop="receiverName" label="收货人" width="120" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column label="地址">
              <template #default="scope">
                {{ scope.row.province }} {{ scope.row.city }} {{ scope.row.district }} {{ scope.row.detailAddress }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-tag v-if="scope.row.isDefault === 1" size="small" type="success">默认</el-tag>
                <el-button v-else size="small" link @click="setDefaultAddress(scope.row)">设为默认</el-button>
                <el-button size="small" link type="primary" @click="openAddressDialog(scope.row)">编辑</el-button>
                <el-button size="small" link type="danger" @click="deleteAddress(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Address Dialog -->
    <el-dialog v-model="addressDialogVisible" :title="isEditAddress ? '编辑地址' : '新增地址'">
      <el-form :model="addressForm" label-width="100px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiverName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="省市区">
           <el-input v-model="addressForm.province" placeholder="省" style="width: 30%" />
           <el-input v-model="addressForm.city" placeholder="市" style="width: 30%; margin: 0 5%" />
           <el-input v-model="addressForm.district" placeholder="区" style="width: 30%" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.detailAddress" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddress">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import request from '../api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const user = ref(JSON.parse(localStorage.getItem('user')))
const authInfo = ref(null)
const authForm = reactive({
    realName: '',
    idCard: ''
})

const addresses = ref([])
const addressDialogVisible = ref(false)
const isEditAddress = ref(false)
const addressForm = reactive({
    id: null,
    receiverName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: ''
})

const fetchAuthInfo = async () => {
    try {
        const res = await request.get(`/auth/identity/status?userId=${user.value.id}`)
        if (res.code === 200) {
            authInfo.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

const submitAuth = async () => {
    try {
        const payload = { ...authForm, userId: user.value.id }
        const res = await request.post('/auth/identity/apply', payload)
        if (res.code === 200) {
            ElMessage.success('提交成功')
            fetchAuthInfo()
        } else {
            ElMessage.error(res.message)
        }
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

const fetchAddresses = async () => {
    try {
        const res = await request.get(`/address/list?userId=${user.value.id}`)
        if (res.code === 200) {
            addresses.value = res.data
        }
    } catch (e) {
        console.error(e)
    }
}

const openAddressDialog = (row) => {
    if (row) {
        isEditAddress.value = true
        Object.assign(addressForm, row)
    } else {
        isEditAddress.value = false
        Object.assign(addressForm, {
            id: null,
            receiverName: '',
            phone: '',
            province: '',
            city: '',
            district: '',
            detailAddress: ''
        })
    }
    addressDialogVisible.value = true
}

const submitAddress = async () => {
    try {
        const payload = { ...addressForm, userId: user.value.id }
        if (isEditAddress.value) {
            await request.put('/address/update', payload)
        } else {
            await request.post('/address/add', payload)
        }
        ElMessage.success('保存成功')
        addressDialogVisible.value = false
        fetchAddresses()
    } catch (e) {
        ElMessage.error('保存失败')
    }
}

const deleteAddress = (row) => {
    ElMessageBox.confirm('确定删除该地址吗？').then(async () => {
        try {
            await request.delete(`/address/delete/${row.id}`)
            ElMessage.success('删除成功')
            fetchAddresses()
        } catch (e) {
            ElMessage.error('删除失败')
        }
    })
}

const setDefaultAddress = async (row) => {
    try {
        await request.post('/address/setDefault', { userId: user.value.id, id: row.id })
        ElMessage.success('设置成功')
        fetchAddresses()
    } catch (e) {
        ElMessage.error('设置失败')
    }
}

const formatTime = (time) => time ? time.replace('T', ' ') : ''
const maskIdCard = (id) => id ? id.replace(/^(.{4})(?:\d+)(.{4})$/, "$1******$2") : ''

onMounted(() => {
    if (user.value) {
        fetchAuthInfo()
        fetchAddresses()
    }
})
</script>

<style scoped>
.user-profile-container {
  max-width: 1000px;
  margin: 20px auto;
  min-height: 500px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mt-4 {
  margin-top: 20px;
}
.tip {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
}
</style>
