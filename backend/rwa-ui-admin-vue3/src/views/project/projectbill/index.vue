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
      <el-form-item label="流水号" prop="billNo">
        <el-input
          v-model="queryParams.billNo"
          placeholder="请输入流水号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态：" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="账单类型" prop="billType">
        <el-select
          v-model="queryParams.billType"
          placeholder="请选择账单类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_BILL_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="项目" prop="projectId">
        <el-select
          v-model="queryParams.projectId"
          placeholder="请选择项目"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="申请时间" prop="applyTime">
        <el-date-picker
          v-model="queryParams.applyTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>

      <el-form-item label="审核状态：" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态：1-待审核 2-审核通过 3-审核不通过"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      </el-form-item>-->
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
<!--        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:bill:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>-->
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:bill:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="账单ID" align="center" prop="id" />
      <el-table-column label="流水号" align="center" prop="billNo" width="180px" />
      <el-table-column label="账单类型" align="center" prop="billType" >
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_BILL_TYPE" :value="scope.row.billType" />
        </template>
      </el-table-column>

      <el-table-column label="申请人" align="center" prop="userName" />
      <el-table-column label="项目名称" align="center" prop="projectName" />
      <el-table-column label="账单金额" align="center" prop="billAmount" width="140px">
        <template #default="scope">
          {{ scope.row.billAmount }} {{ scope.row.billCoin }}
        </template>
      </el-table-column>
      <el-table-column label="实际到账" align="center" prop="actualAmount" width="140px">
        <template #default="scope">
          {{ scope.row.actualAmount }} {{ scope.row.actualCoin }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column
        label="申请时间"
        align="center"
        prop="applyTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="审核时间"
        align="center"
        prop="auditTime"
        :formatter="dateFormatter"
        width="180px"
      />

      <el-table-column label="操作" align="center" min-width="190px">

        <template #default="scope">
          <el-button

            type="primary"
            @click="openViewForm(scope.row.id)"
            v-hasPermi="['project:bill:query']"
          >
            查看
          </el-button>
          <el-button

            type="primary"
            @click="openAuditForm(scope.row.id)"
            v-if="scope.row.auditStatus === 1"
            v-hasPermi="['project:bill:audit']"
          >
            审核
          </el-button>
          <el-button

            type="success"
            @click="openVoucherForm(scope.row.id)"
            v-if="scope.row.auditStatus === 2"
            v-hasPermi="['project:bill:audit']"
          >
            上传凭证
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

  <!-- 审核表单弹窗 -->
  <BillAuditForm ref="auditFormRef" @success="getList" />
  <!-- 查看表单弹窗 -->
  <BillViewForm ref="viewFormRef" />
  <!-- 上传凭证弹窗 -->
  <BillVoucherForm ref="voucherFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { BillApi, BillVO } from '@/api/project/projectbill'
import { InfoApi } from '@/api/project/projectinfo'
import BillAuditForm from './BillAuditForm.vue'
import BillViewForm from './BillViewForm.vue'
import BillVoucherForm from './BillVoucherForm.vue'
import { useRoute } from 'vue-router'

/** 项目账单管理表 列表 */
defineOptions({ name: 'ProjectBill' })

const route = useRoute()
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<BillVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  billNo: undefined,
  billType: undefined,
  applyTime: [],
  userId: undefined,
  projectId: route.query.projectId ? Number(route.query.projectId) : undefined,
  projectName: undefined,
  payee: undefined,
  bankAccount: undefined,
  bankName: undefined,
  billAmount: undefined,
  auditStatus: undefined,
  auditUserId: undefined,
  auditUserName: undefined,
  auditTime: [],
  auditRemark: undefined,
  payVoucherUrl: undefined,
  payTime: [],
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const projectList = ref<any[]>([]) // 项目列表

/** 加载项目列表 */
const loadProjects = async () => {
  try {
    const data = await InfoApi.getInfoSimple()
    projectList.value = data
  } catch (e) {
    console.error('加载项目列表失败', e)
  }
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BillApi.getBillPage(queryParams)
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

/** 审核操作 */
const auditFormRef = ref()
const openAuditForm = (id: number) => {
  auditFormRef.value.open(id)
}

/** 查看操作 */
const viewFormRef = ref()
const openViewForm = (id: number) => {
  viewFormRef.value.open(id)
}

/** 上传凭证操作 */
const voucherFormRef = ref()
const openVoucherForm = (id: number) => {
  voucherFormRef.value.open(id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await BillApi.deleteBill(id)
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
    const data = await BillApi.exportBill(queryParams)
    download.excel(data, '项目账单管理表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  loadProjects()
  getList()
})
</script>
