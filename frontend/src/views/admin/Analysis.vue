<template>
  <div class="analysis-container" v-loading="loading">
    <el-card>
      <template #header>运营数据分析</template>
      
      <div class="stats-content" style="margin-top: 20px;">
          <el-row :gutter="20">
              <el-col :span="12">
                  <el-card shadow="never">
                      <h4>近7日订单趋势</h4>
                      <div style="height: 300px;">
                          <v-chart class="chart" :option="lineOption" autoresize />
                      </div>
                  </el-card>
              </el-col>
              <el-col :span="12">
                  <el-card shadow="never">
                      <h4>设备库存分布</h4>
                      <div style="height: 300px;">
                          <v-chart class="chart" :option="pieOption" autoresize />
                      </div>
                  </el-card>
              </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
                <el-card shadow="never">
                    <h4>热门机型排行 (Top 10)</h4>
                    <div style="height: 400px;">
                        <v-chart class="chart" :option="barOption" autoresize />
                    </div>
                </el-card>
            </el-col>
          </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../api/request'
import { ElMessage } from 'element-plus'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import VChart from 'vue-echarts'

use([CanvasRenderer, LineChart, PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const loading = ref(false)
const trendData = ref({ dates: [], values: [] })
const categoryData = ref([])
const topDeviceData = ref({ names: [], counts: [] })

const fetchAnalysis = async () => {
    loading.value = true
    try {
        const res = await request.get('/dashboard/analysis')
        if (res.code === 200) {
            trendData.value = {
                dates: res.data.trendDates,
                values: res.data.trendValues
            }
            categoryData.value = res.data.categoryData
            topDeviceData.value = {
                names: res.data.topDeviceNames || [],
                counts: res.data.topDeviceCounts || []
            }
        }
    } catch (e) {
        ElMessage.error('加载数据失败')
    } finally {
        loading.value = false
    }
}

const lineOption = computed(() => ({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trendData.value.dates },
    yAxis: { type: 'value' },
    series: [{
        data: trendData.value.values,
        type: 'line',
        smooth: true,
        areaStyle: {}
    }]
}))

const pieOption = computed(() => ({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [{
        name: '库存分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        data: categoryData.value
    }]
}))

const barOption = computed(() => ({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { 
        type: 'category', 
        data: topDeviceData.value.names,
        axisLabel: { interval: 0, rotate: 30 } 
    },
    yAxis: { type: 'value' },
    series: [{
        name: '租赁次数',
        type: 'bar',
        barWidth: '60%',
        data: topDeviceData.value.counts,
        itemStyle: { color: '#409EFF' }
    }]
}))

onMounted(() => {
    fetchAnalysis()
})
</script>

<style scoped>
.chart {
    height: 100%;
    width: 100%;
}
</style>
