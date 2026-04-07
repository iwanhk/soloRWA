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
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select
          v-model="queryParams.orderStatus"
          placeholder="请选择订单状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_ORDER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

<!--      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>-->
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
<!--        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:order:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:order:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>-->
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
<!--      <el-table-column label="订单ID" align="center" prop="id" />-->
      <el-table-column label="订单号" align="center" prop="orderNo" width="240px"/>
      <el-table-column
        label="申请日期"
        align="center"
        prop="applyDate"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="用户" align="center" prop="userName" />
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_ORDER_STATUS" :value="scope.row.orderStatus" />
        </template>
      </el-table-column>
<!--      <el-table-column label="项目ID" align="center" prop="projectId" />-->
      <el-table-column label="项目名称" align="center" prop="projectName" />
      <el-table-column label="份额(份)" align="center" prop="subscribeQuantity" />
<!--      <el-table-column label="单价" align="center" prop="price" />-->
      <el-table-column label="总价" align="center" prop="totalAmount">
        <template #default="scope">
          {{ scope.row.totalAmount }} {{ scope.row.investmentCurrency }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button

            type="primary"
            @click="openForm('audit', scope.row.id)"
            v-if="scope.row.orderStatus === 1"
            v-hasPermi="['project:order:audit']"
          >
            审核
          </el-button>
          <el-button

            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:order:update']"
          >
            查看
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
  <OrderForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { OrderApi, OrderVO } from '@/api/project/projectorder'
import OrderForm from './OrderForm.vue'

/** 项目认购订单 列表 */
defineOptions({ name: 'ProjectOrder' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OrderVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: undefined,
  applyDate: [],
  userId: undefined,
  orderStatus: undefined,
  projectId: undefined,
  projectName: undefined,
  subscribeQuantity: undefined,
  price: undefined,
  totalAmount: undefined,
  confirmPurchaseTime: [],
  payType: undefined,
  chainAddress: undefined,
  contractNo: undefined,
  payVoucherUrl: undefined,
  auditTime: [],
  auditUserId: undefined,
  auditUserName: undefined,
  chainStatus: undefined,
  auditRemark: undefined,
  cancelTime: [],
  cancelReason: undefined,
  expireTime: [],
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OrderApi.getOrderPage(queryParams)
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
    await OrderApi.deleteOrder(id)
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
    const data = await OrderApi.exportOrder(queryParams)
    download.excel(data, '项目认购订单.xls')
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
