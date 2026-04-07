<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >

      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="收益日期" prop="incomeDate">
        <el-date-picker
          v-model="queryParams.incomeDate"
          value-format="YYYY-MM-DD"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="收益状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择收益状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_INCOME_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
<!--        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:order-daily-income:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>-->
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:order-daily-income:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
        <el-button
          type="warning"
          plain
          @click="openDistributeDialog"
          v-hasPermi="['project:order-daily-income:distribute-income']"
        >
          <Icon icon="ep:position" class="mr-5px" /> 发放收益
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">

      <el-table-column label="订单号" align="center" prop="orderNo" />

      <el-table-column
        label="收益日期"
        align="center"
        prop="incomeDate"
        :formatter="dateFormatter2"
        width="180px"
      />
      <el-table-column label="当日持有份额" align="center" prop="holdQuantity" />

      <el-table-column label="当日收益" align="center" prop="dailyIncome" />
      <el-table-column label="收益状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_INCOME_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="收益发放时间"
        align="center"
        prop="issueTime"
        :formatter="dateFormatter"
        width="180px"
      />
<!--      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:order-daily-income:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:order-daily-income:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>-->
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <OrderDailyIncomeForm ref="formRef" @success="getList" />

  <!-- 发放收益弹窗 -->
  <Dialog title="发放收益" v-model="distributeDialogVisible" width="400px">
    <el-form :model="distributeForm" label-width="100px">
      <el-form-item label="收益日期" required>
        <el-date-picker
          v-model="distributeForm.incomeDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择要发放收益的日期"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item>
        <el-alert type="warning" :closable="false">
          请确认此操作将发放所选日期的所有待发放收益
        </el-alert>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="distributeDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleDistribute" :loading="distributeLoading">确 定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter,dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderDailyIncomeApi, OrderDailyIncomeVO } from '@/api/project/orderdailyincome'
import OrderDailyIncomeForm from './OrderDailyIncomeForm.vue'

/** 订单每日收益统计 列表 */
defineOptions({ name: 'OrderDailyIncome' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OrderDailyIncomeVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderId: undefined,
  orderNo: undefined,
  userId: undefined,
  projectId: undefined,
  incomeDate: [],
  projectRevenueId: undefined,
  holdQuantity: undefined,
  issueTime: [],
  dailyIncome: undefined,
  incomeRate: undefined,
  cumulativeIncome: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OrderDailyIncomeApi.getOrderDailyIncomePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await OrderDailyIncomeApi.deleteOrderDailyIncome(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await OrderDailyIncomeApi.exportOrderDailyIncome(queryParams)
    download.excel(data, '订单每日收益统计.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})

/** 发放收益弹窗 */
const distributeDialogVisible = ref(false)
const distributeLoading = ref(false)
const distributeForm = reactive({
  incomeDate: ''
})

const openDistributeDialog = () => {
  distributeForm.incomeDate = ''
  distributeDialogVisible.value = true
}

const handleDistribute = async () => {
  if (!distributeForm.incomeDate) {
    message.warning('请选择收益日期')
    return
  }
  try {
    // 二次确认
    await message.confirm(`确定要发放 ${distributeForm.incomeDate} 的所有收益吗？此操作不可撤销。`)
    
    distributeLoading.value = true
    await OrderDailyIncomeApi.distributeIncome(distributeForm.incomeDate)
    message.success('收益发放成功')
    distributeDialogVisible.value = false
    await getList()
  } catch {}
  finally {
    distributeLoading.value = false
  }
}
</script>
