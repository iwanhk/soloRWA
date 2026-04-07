<template>
  <div class="dashboard-container">
    <!-- 审核统计卡片 -->
    <div class="audit-container mb-16px">
      <!-- 发行商审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('system:tenant:audit')"
        title="发行商审核" 
        :count="tenantAuditStats.tenantAuditCount"
        icon-bg="linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)"
        @click="navigateTo('/biz/publisher/list')"
      />

      <!-- 用户审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('user:audit:review')"
        title="用户审核" 
        :count="userAuditStats.userAuditCount"
        icon-bg="linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
        @click="navigateTo('/biz/userall/info')"
      />
      
      <!-- 银行卡审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('user:audit:review')"
        title="银行卡审核" 
        :count="userAuditStats.bankCardAuditCount"
        icon-bg="linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
        @click="navigateTo('/biz/userall/info')"
      />
      
      <!-- 项目上线审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('project:info:audit')"
        title="项目上线审核" 
        :count="projectAuditStats.projectOnlineAuditCount"
        icon-bg="linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
        @click="navigateTo('/biz/projectall/projectinfo')"
      />
      
      <!-- 项目运行审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('project:info:audit')"
        title="项目运行审核" 
        :count="projectAuditStats.projectRunningAuditCount"
        icon-bg="linear-gradient(135deg, #fa709a 0%, #fee140 100%)"
        @click="navigateTo('/biz/projectall/projectinfo')"
      />
      
      <!-- 订单审核 (仅管理员) -->
      <AuditCard 
        v-if="hasPermission('project:order:audit')"
        title="订单审核" 
        :count="orderAuditStats.orderAuditCount"
        icon-bg="linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)"
        @click="navigateTo('/biz/orderAll/order')"
      />
      
      <!-- 账单审核 (管理员 + 发行商) -->
      <AuditCard 
        v-if="hasPermission('project:bill:audit')"
        title="账单审核" 
        :count="billAuditStats.billAuditCount"
        icon-bg="linear-gradient(135deg, #30cfd0 0%, #330867 100%)"
        @click="navigateTo('/biz/projectall/bill')"
      />
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-primary">
              <Icon icon="ep:goods" :size="28" color="#fff" />
            </div>
            <div class="stat-info">
              <div class="stat-label">项目总数</div>
              <CountTo
                class="stat-value"
                :start-val="0"
                :end-val="stats.projectCount"
                :duration="2000"
              />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-success">
              <Icon icon="ep:document" :size="28" color="#fff" />
            </div>
            <div class="stat-info">
              <div class="stat-label">订单总量</div>
              <CountTo
                class="stat-value"
                :start-val="0"
                :end-val="stats.orderCount"
                :duration="2000"
              />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon bg-warning">
              <Icon icon="ep:money" :size="28" color="#fff" />
            </div>
            <div class="stat-info">
              <div class="stat-label">交易总额(U)</div>
              <CountTo
                class="stat-value"
                :start-val="0"
                :end-val="stats.totalAmount"
                :duration="2000"
                :decimals="2"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="mb-16px">
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24" class="mb-16px">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>订单趋势（近30天）</span>
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button value="order">订单量</el-radio-button>
                <el-radio-button value="amount">交易额</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <Echart :options="lineOptions" :height="320" />
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24" class="mb-16px">
        <el-card shadow="never">
          <template #header>
            <span>订单状态分布</span>
          </template>
          <Echart :options="pieOptions" :height="320" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 项目收益统计表格 -->
    <el-row :gutter="16">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>项目每日收益统计</span>
            </div>
          </template>
          <el-table :data="dailyRevenueData" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="revenueDate" label="日期" width="180">
              <template #default="{ row }">
                {{ formatRevenueDate(row.revenueDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="coinRevenue" label="产出" min-width="120" align="right">
              <template #default="{ row }">
                 {{ formatMoney(row.coinRevenue) }} {{ row.coinCode }}
              </template>
            </el-table-column>
            <el-table-column prop="projectRevenue" label="项目方收益" min-width="120" align="right">
              <template #default="{ row }">
                 {{ formatMoney(row.projectRevenue) }} {{ row.coinCode }}
              </template>
            </el-table-column>
            <el-table-column prop="availableRevenue" label="发放收益" min-width="120" align="right">
              <template #default="{ row }">
                 {{ formatMoney(row.availableRevenue) }} {{ row.coinCode }}
              </template>
            </el-table-column>
          </el-table>
          <div style="display: flex; justify-content: flex-end; margin-top: 15px;">
            <el-pagination
              v-model:current-page="queryParams.pageNo"
              v-model:page-size="queryParams.pageSize"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="loadRevenuePage"
              @current-change="loadRevenuePage"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import * as StatisticsApi from '@/api/project/statistics'
import * as UserAuditApi from '@/api/user/auditStatistics'
import * as ProjectAuditApi from '@/api/project/auditStatistics'
import * as TenantApi from '@/api/system/tenant'
import { RevenueApi, RevenueVO } from '@/api/project/projectrevenue'
import AuditCard from './components/AuditCard.vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'Index' })

const router = useRouter()
const userStore = useUserStore()

// 审核统计数据
const tenantAuditStats = ref({ tenantAuditCount: 0 })
const userAuditStats = ref({ userAuditCount: 0, bankCardAuditCount: 0 })
const projectAuditStats = ref({ projectOnlineAuditCount: 0, projectRunningAuditCount: 0 })
const orderAuditStats = ref({ orderAuditCount: 0 })
const billAuditStats = ref({ billAuditCount: 0 })

// 统计数据
const stats = reactive({
  projectCount: 0,
  orderCount: 0,
  totalAmount: 0
})

// 趋势类型
const trendType = ref('order')

// 趋势数据
const orderTrendData = ref<StatisticsApi.OrderTrendVO[]>([])

// 日收益数据
const loading = ref(false)
const dailyRevenueData = ref<RevenueVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})

// 折线图配置
const lineOptions = reactive<EChartsOption>({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '订单量',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ]
        }
      },
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' },
      data: []
    }
  ]
})

// 饼图配置
const pieOptions = reactive<EChartsOption>({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    top: 'center'
  },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: []
    }
  ]
})

// 修改趋势图数据
const updateLineChart = () => {
  if (trendType.value === 'order') {
    if (lineOptions.series && lineOptions.series[0]) {
      (lineOptions.series[0] as any).name = '订单量';
      (lineOptions.series[0] as any).data = orderTrendData.value.map(d => d.orderCount)
    }
  } else {
    if (lineOptions.series && lineOptions.series[0]) {
      (lineOptions.series[0] as any).name = '交易额';
      (lineOptions.series[0] as any).data = orderTrendData.value.map(d => d.amount)
    }
  }
}

// 监听趋势类型切换
watch(trendType, updateLineChart)

// 加载概览数据
const loadOverview = async () => {
  try {
    const res = await StatisticsApi.getOverview()
    stats.projectCount = res.projectCount || 0
    stats.orderCount = res.orderCount || 0
    stats.totalAmount = res.totalAmount || 0
  } catch (e) {
    console.error('加载概览数据失败', e)
  }
}

// 加载订单趋势
const loadOrderTrend = async () => {
  try {
    const res = await StatisticsApi.getOrderTrend()
    orderTrendData.value = res || []
    lineOptions.xAxis = { ...lineOptions.xAxis, data: res.map(d => d.date) }
    updateLineChart()
  } catch (e) {
    console.error('加载订单趋势失败', e)
  }
}

// 加载订单状态分布
const loadOrderStatusDist = async () => {
  try {
    const res = await StatisticsApi.getOrderStatusDist()
    const colors: Record<number, string> = {
      0: '#909399',
      1: '#E6A23C',
      2: '#67C23A',
      3: '#F56C6C',
      4: '#f3ecec'
    }
    pieOptions.series = [{
      ...pieOptions.series![0],
      data: res.map(d => ({
        value: d.count,
        name: d.name,
        itemStyle: { color: colors[d.status] || '#909399' }
      }))
    }]
  } catch (e) {
    console.error('加载订单状态分布失败', e)
  }
}

// 加载日收益数据
const loadRevenuePage = async () => {
  loading.value = true
  try {
    const res = await RevenueApi.getRevenuePage(queryParams)
    dailyRevenueData.value = res.list
    total.value = res.total
  } catch (e) {
    console.error('加载收益数据失败', e)
  } finally {
    loading.value = false
  }
}

// 权限检查
const hasPermission = (permission: string) => {
  return userStore.permissions.has(permission)
}

// 导航到指定页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 加载审核统计
const loadAuditStats = async () => {
  try {
    // 仅管理员加载发行商审核统计
    if (hasPermission('system:tenant:audit')) {
      const count = await TenantApi.getAuditCount()
      tenantAuditStats.value = { tenantAuditCount: count || 0 }
    }

    // 仅管理员加载用户审核统计
    if (hasPermission('user:audit:review')) {
      const userStats = await UserAuditApi.getUserAuditStatistics()
      userAuditStats.value = userStats
    }
    
    // 仅管理员加载项目审核统计
    if (hasPermission('project:info:audit')) {
      const projectStats = await ProjectAuditApi.getProjectAuditStatistics()
      projectAuditStats.value = projectStats
    }
    
    // 仅管理员加载订单审核统计
    if (hasPermission('project:order:audit')) {
      const orderStats = await ProjectAuditApi.getOrderAuditStatistics()
      orderAuditStats.value = orderStats
    }
    
    // 管理员和发行商都加载账单审核统计
    if (hasPermission('project:bill:audit')) {
      const billStats = await ProjectAuditApi.getBillAuditStatistics()
      billAuditStats.value = billStats
    }
  } catch (e) {
    console.error('加载审核统计失败', e)
  }
}

// 格式化日期数组
const formatRevenueDate = (dateArray: any) => {
  if (Array.isArray(dateArray) && dateArray.length >= 3) {
    const year = dateArray[0]
    const month = String(dateArray[1]).padStart(2, '0')
    const day = String(dateArray[2]).padStart(2, '0')
    return `${year}-${month}-${day}`
  }
  return dateArray || '-'
}

// 格式化金额，最多保留8位小数
const formatMoney = (val: any) => {
  if (val === undefined || val === null || val === '') return '0'
  return parseFloat(Number(val).toFixed(8)).toString()
}

onMounted(() => {
  loadAuditStats()
  loadOverview()
  loadOrderTrend()
  loadOrderStatusDist()
  loadRevenuePage()
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
}

.mb-16px {
  margin-bottom: 16px;
}

.audit-container {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 12px;
  padding-bottom: 8px; /* For scrollbar */
  scrollbar-width: thin;
}

.audit-container::-webkit-scrollbar {
  height: 6px;
}

.audit-container::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.stat-card {
  height: 100%;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.bg-primary {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}

.bg-success {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.bg-warning {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
