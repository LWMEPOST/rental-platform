<template>
  <div class="order-confirm-container" v-loading="loading">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>确认订单</span>
        </div>
      </template>

      <div class="device-info" v-if="device">
        <img :src="device.mainImage" class="device-thumb" />
        <div class="info-text">
          <h3>{{ device.name }}</h3>
          <p>租金: ¥{{ device.rentalPrice }}/天</p>
          <p>押金: ¥{{ device.depositAmount }}</p>
        </div>
      </div>

      <el-form label-width="120px" class="mt-4">
        <!-- Address Selection -->
        <el-form-item label="收货地址">
            <el-select v-model="selectedAddressId" placeholder="请选择收货地址" style="width: 100%">
                <el-option
                    v-for="addr in addresses"
                    :key="addr.id"
                    :label="`${addr.receiverName} ${addr.phone} (${addr.province}${addr.city}${addr.detailAddress})`"
                    :value="addr.id"
                />
            </el-select>
            <div class="address-tip" v-if="addresses.length === 0">
                暂无地址，<el-button link type="primary" @click="router.push('/user/profile')">去添加</el-button>
            </div>
        </el-form-item>

        <el-form-item label="租赁时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            @change="calculatePrice"
          />
        </el-form-item>

        <div class="price-breakdown" v-if="dateRange">
          <p>租期: {{ rentalDays }} 天</p>
          <p>租金总额: ¥{{ rentalTotal }}</p>
          <p>押金: ¥{{ device?.depositAmount }}</p>
          <p class="total">应付总额: ¥{{ grandTotal }}</p>
        </div>

        <el-form-item>
          <el-button type="primary" size="large" @click="submitOrder" :disabled="!dateRange || !selectedAddressId">提交订单</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const deviceId = route.params.deviceId
const device = ref(null)
const loading = ref(false)
const dateRange = ref([])
const rentalDays = ref(0)
const addresses = ref([])
const selectedAddressId = ref(null)

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const rentalTotal = computed(() => {
  if (!device.value || rentalDays.value <= 0) return 0
  return (device.value.rentalPrice * rentalDays.value).toFixed(2)
})

const grandTotal = computed(() => {
  if (!device.value) return 0
  return (parseFloat(rentalTotal.value) + parseFloat(device.value.depositAmount)).toFixed(2)
})

const fetchDevice = async () => {
  loading.value = true
  try {
    const res = await request.get(`/device/${deviceId}`)
    if (res.code === 200) {
      device.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载设备信息失败')
  } finally {
    loading.value = false
  }
}

const fetchAddresses = async () => {
    const user = JSON.parse(localStorage.getItem('user'))
    if (!user) return
    try {
        const res = await request.get(`/address/list?userId=${user.id}`)
        if (res.code === 200) {
            addresses.value = res.data
            // Auto select default
            const defaultAddr = addresses.value.find(a => a.isDefault === 1)
            if (defaultAddr) {
                selectedAddressId.value = defaultAddr.id
            } else if (addresses.value.length > 0) {
                selectedAddressId.value = addresses.value[0].id
            }
        }
    } catch (e) {
        console.error(e)
    }
}

const calculatePrice = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    const start = dateRange.value[0]
    const end = dateRange.value[1]
    const diffTime = Math.abs(end - start)
    let days = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    if (days === 0) days = 1
    rentalDays.value = days
  }
}

const submitOrder = async () => {
  const user = JSON.parse(localStorage.getItem('user'))
  if (!user) {
      router.push('/login')
      return
  }
  
  try {
      const params = {
          userId: user.id,
          deviceId: parseInt(deviceId),
          startTime: dateRange.value[0],
          endTime: dateRange.value[1],
          addressId: selectedAddressId.value // Pass address ID to backend (backend entity might need update, or just ignore for now as POC)
      }
      const res = await request.post('/order/create', params)
      if (res.code === 200) {
          ElMessage.success('订单提交成功')
          await mockPay(res.data.id)
      } else {
          ElMessage.error(res.message || '下单失败')
      }
  } catch (error) {
      console.error(error)
      ElMessage.error('下单异常')
  }
}

const mockPay = async (orderId) => {
    try {
        await request.post(`/order/pay/${orderId}`)
        ElMessage.success('支付成功')
        router.push('/')
    } catch (e) {
        ElMessage.error('支付失败')
    }
}

onMounted(() => {
  fetchDevice()
  fetchAddresses()
})
</script>

<style scoped>
.order-confirm-container {
  padding: 40px;
  max-width: 800px;
  margin: 0 auto;
}
.device-info {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}
.device-thumb {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
.mt-4 {
  margin-top: 20px;
}
.price-breakdown {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.total {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin-top: 10px;
  border-top: 1px dashed #ddd;
  padding-top: 10px;
}
.address-tip {
    font-size: 12px;
    color: #999;
    margin-top: 5px;
}
</style>
