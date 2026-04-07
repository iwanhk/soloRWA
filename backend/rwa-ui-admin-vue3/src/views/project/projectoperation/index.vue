<template>
<!--  <ContentWrap>
    &lt;!&ndash; 搜索工作栏 &ndash;&gt;
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="参与投资人数" prop="investorCount">
        <el-input
          v-model="queryParams.investorCount"
          placeholder="请输入参与投资人数"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="申请分红人数" prop="dividendApplyCount">
        <el-input
          v-model="queryParams.dividendApplyCount"
          placeholder="请输入申请分红人数"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="申请分红金额(U)" prop="dividendApplyAmount">
        <el-input
          v-model="queryParams.dividendApplyAmount"
          placeholder="请输入申请分红金额(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="提前赎回人数" prop="earlyRedemptionPeople">
        <el-input
          v-model="queryParams.earlyRedemptionPeople"
          placeholder="请输入提前赎回人数"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="提前赎回金额(U)" prop="earlyRedemptionAmount">
        <el-input
          v-model="queryParams.earlyRedemptionAmount"
          placeholder="请输入提前赎回金额(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="提前赎回份额" prop="earlyRedemptionCount">
        <el-input
          v-model="queryParams.earlyRedemptionCount"
          placeholder="请输入提前赎回份额"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="到期赎回人数" prop="maturityRedemptionCount">
        <el-input
          v-model="queryParams.maturityRedemptionCount"
          placeholder="请输入到期赎回人数"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="到期赎回金额(U)" prop="maturityRedemptionAmount">
        <el-input
          v-model="queryParams.maturityRedemptionAmount"
          placeholder="请输入到期赎回金额(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="投资人总收益(U)" prop="totalInvestorIncome">
        <el-input
          v-model="queryParams.totalInvestorIncome"
          placeholder="请输入投资人总收益(U)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="投资人总收益率(%)" prop="totalInvestorYield">
        <el-input
          v-model="queryParams.totalInvestorYield"
          placeholder="请输入投资人总收益率(%)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
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
          v-hasPermi="['project:operation:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:operation:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>-->

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="关联project_core.id" align="center" prop="projectId" />
      <el-table-column label="参与投资人数" align="center" prop="investorCount" />
      <el-table-column label="申请分红人数" align="center" prop="dividendApplyCount" />
      <el-table-column label="申请分红金额(U)" align="center" prop="dividendApplyAmount" />
      <el-table-column label="提前赎回人数" align="center" prop="earlyRedemptionPeople" />
      <el-table-column label="提前赎回金额(U)" align="center" prop="earlyRedemptionAmount" />
      <el-table-column label="提前赎回份额" align="center" prop="earlyRedemptionCount" />
      <el-table-column label="到期赎回人数" align="center" prop="maturityRedemptionCount" />
      <el-table-column label="到期赎回金额(U)" align="center" prop="maturityRedemptionAmount" />
      <el-table-column label="投资人总收益(U)" align="center" prop="totalInvestorIncome" />
      <el-table-column label="投资人总收益率(%)" align="center" prop="totalInvestorYield" />
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
            v-hasPermi="['project:operation:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:operation:delete']"
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
  <OperationForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { OperationApi, OperationVO } from '@/api/project/projectoperation'
import OperationForm from './OperationForm.vue'

/** 项目运营统计表 列表 */
defineOptions({ name: 'ProjectOperation' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OperationVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  investorCount: undefined,
  dividendApplyCount: undefined,
  dividendApplyAmount: undefined,
  earlyRedemptionPeople: undefined,
  earlyRedemptionAmount: undefined,
  earlyRedemptionCount: undefined,
  maturityRedemptionCount: undefined,
  maturityRedemptionAmount: undefined,
  totalInvestorIncome: undefined,
  totalInvestorYield: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OperationApi.getOperationPage(queryParams)
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
    await OperationApi.deleteOperation(id)
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
    const data = await OperationApi.exportOperation(queryParams)
    download.excel(data, '项目运营统计表.xls')
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
