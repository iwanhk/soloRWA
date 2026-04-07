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
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="项目ID" prop="projectId">
        <el-input
          v-model="queryParams.projectId"
          placeholder="请输入项目ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="本金金额(U)" prop="principalAmount">
        <el-input
          v-model="queryParams.principalAmount"
          placeholder="请输入本金金额(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="持有金额" prop="holdAmount">
        <el-input
          v-model="queryParams.holdAmount"
          placeholder="请输入持有金额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="购买份额" prop="buyQuantity">
        <el-input
          v-model="queryParams.buyQuantity"
          placeholder="请输入购买份额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="当前持有份额(份)（赎回后扣减）" prop="holdQuantity">
        <el-input
          v-model="queryParams.holdQuantity"
          placeholder="请输入当前持有份额(份)（赎回后扣减）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="累计总收益(U)（含未提取）" prop="totalIncome">
        <el-input
          v-model="queryParams.totalIncome"
          placeholder="请输入累计总收益(U)（含未提取）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="已提取分红(U)" prop="withdrawnDividend">
        <el-input
          v-model="queryParams.withdrawnDividend"
          placeholder="请输入已提取分红(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="冻结的分红(U)" prop="freezeDividend">
        <el-input
          v-model="queryParams.freezeDividend"
          placeholder="请输入冻结的分红(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="未提取分红(U)（=总收益-已提取）" prop="unwithdrawnDividend">
        <el-input
          v-model="queryParams.unwithdrawnDividend"
          placeholder="请输入未提取分红(U)（=总收益-已提取）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="累计赎回本金(U)（赎回时累加）" prop="totalRedemptionAmount">
        <el-input
          v-model="queryParams.totalRedemptionAmount"
          placeholder="请输入累计赎回本金(U)（赎回时累加）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="最后一次收益计算时间" prop="lastIncomeCalcTime">
        <el-date-picker
          v-model="queryParams.lastIncomeCalcTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="最后一次分红提取时间" prop="lastWithdrawTime">
        <el-date-picker
          v-model="queryParams.lastWithdrawTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:order-balance:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:order-balance:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="记录ID" align="center" prop="id" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="项目ID" align="center" prop="projectId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="本金金额(U)" align="center" prop="principalAmount" />
      <el-table-column label="持有金额" align="center" prop="holdAmount" />
      <el-table-column label="购买份额" align="center" prop="buyQuantity" />
      <el-table-column label="当前持有份额(份)（赎回后扣减）" align="center" prop="holdQuantity" />
      <el-table-column label="累计总收益(U)（含未提取）" align="center" prop="totalIncome" />
      <el-table-column label="已提取分红(U)" align="center" prop="withdrawnDividend" />
      <el-table-column label="冻结的分红(U)" align="center" prop="freezeDividend" />
      <el-table-column label="未提取分红(U)（=总收益-已提取）" align="center" prop="unwithdrawnDividend" />
      <el-table-column label="累计赎回本金(U)（赎回时累加）" align="center" prop="totalRedemptionAmount" />
      <el-table-column
        label="最后一次收益计算时间"
        align="center"
        prop="lastIncomeCalcTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="最后一次分红提取时间"
        align="center"
        prop="lastWithdrawTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:order-balance:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:order-balance:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
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
  <OrderBalanceForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderBalanceApi, OrderBalanceVO } from '@/api/project/projectorderbalance'
import OrderBalanceForm from './OrderBalanceForm.vue'

/** 用户项目余额表 列表 */
defineOptions({ name: 'ProjectOrderBalance' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OrderBalanceVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  projectId: undefined,
  orderId: undefined,
  principalAmount: undefined,
  holdAmount: undefined,
  buyQuantity: undefined,
  holdQuantity: undefined,
  totalIncome: undefined,
  withdrawnDividend: undefined,
  freezeDividend: undefined,
  unwithdrawnDividend: undefined,
  totalRedemptionAmount: undefined,
  lastIncomeCalcTime: [],
  lastWithdrawTime: [],
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OrderBalanceApi.getOrderBalancePage(queryParams)
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
    await OrderBalanceApi.deleteOrderBalance(id)
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
    const data = await OrderBalanceApi.exportOrderBalance(queryParams)
    download.excel(data, '用户项目余额表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
