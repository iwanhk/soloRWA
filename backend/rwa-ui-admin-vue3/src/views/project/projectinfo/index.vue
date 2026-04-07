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
      <el-form-item label="名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="项目类" prop="projectType">
        <el-select
          v-model="queryParams.projectType"
          placeholder="请选择项目类"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="运行状态" prop="projectStatus">
        <el-select
          v-model="queryParams.projectStatus"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="上线状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS).filter(
              (item) => item.value !== 4
            )"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" plain @click="handleCreate" v-hasPermi="['project:info:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>

        <el-button
          type="success"
          plain
          @click="handleExportByProject"
          :loading="exportSummaryLoading"
          v-hasPermi="['project:info:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出(项目汇总)
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="项目ID" align="center" prop="projectId" />
      <el-table-column label="项目名称" align="center" prop="projectName" min-width="150px" />
      <el-table-column label="项目类" align="center" prop="projectType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_PROJECT_TYPE" :value="scope.row.projectType" />
        </template>
      </el-table-column>
      <el-table-column label="资产类型" align="center" prop="assetType" min-width="95px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_ASSET_TYPE" :value="scope.row.assetType" />
        </template>
      </el-table-column>

      <el-table-column
        label="发行商"
        align="center"
        prop="publisherCompanyName"
        min-width="150px"
      />

      <el-table-column label="发行数量" align="center" prop="issueQuantity" />
      <el-table-column label="单价" align="center" prop="issueUnitPrice">
        <template #default="scope">
          {{ scope.row.issueUnitPrice }} {{ scope.row.investmentCurrency || '' }}
        </template>
      </el-table-column>

      <el-table-column label="预期年化" align="center" prop="expectedAnnualReturn" />
      <!-- <el-table-column label="发行链ID（关联chain_manage）" align="center" prop="issueChainId" /> -->
      <el-table-column label="审核状态" align="center" prop="auditStatus" min-width="140px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
          <el-tooltip
            v-if="scope.row.auditStatus === 3"
            :content="scope.row.auditRemark || '无审核备注'"
            placement="top"
          >
            <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="编辑状态" align="center" prop="editStatus" min-width="140px">
        <template #default="scope">
          <!-- 只有已发布项目（auditStatus=2）才显示编辑状态 -->
          <template v-if="scope.row.auditStatus === 2">
            <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.editStatus" />
            <el-tooltip
              v-if="scope.row.editStatus === 3"
              :content="scope.row.auditRemark || '无审核备注'"
              placement="top"
            >
              <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
            </el-tooltip>
          </template>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="运行状态" align="center" prop="projectStatus" min-width="141px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_PROJECT_STATUS" :value="scope.row.projectStatus" />
          <el-tooltip
            v-if="scope.row.projectStatus === 3"
            :content="scope.row.auditRemark || '无审核备注'"
            placement="top"
          >
            <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" min-width="270px" fixed="right">
        <template #default="scope">
          <el-button type="primary" @click="enterProjectSpace(scope.row.projectId)">
            进入
          </el-button>
          <el-button
            type="success"
            @click="openAuditDialog(scope.row.projectId)"
            v-if="
              (scope.row.auditStatus === 2 && scope.row.editStatus === 1) ||
              scope.row.auditStatus === 1
            "
            v-hasPermi="['project:info:audit']"
          >
            审核
          </el-button>
          <el-button
            type="primary"
            @click="handleRunSubmit(scope.row.projectId)"
            v-if="
              scope.row.auditStatus === 2 &&
              (scope.row.projectStatus === 0 || scope.row.projectStatus === 3)
            "
            v-hasPermi="['project:info:update']"
          >
            申请运行
          </el-button>
          <el-button
            type="warning"
            @click="openAuditDialog(scope.row.projectId, 'run_audit')"
            v-if="scope.row.projectStatus === 1"
            v-hasPermi="['project:info:audit']"
          >
            审核运行
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
  <InfoForm ref="formRef" @success="getList" />
  <!-- 项目审核弹窗 -->
  <ProjectAuditDialog ref="auditDialogRef" @success="getList" />
  <!-- 运营统计弹窗 -->
  <OperationStat ref="opStatRef" />
  <!-- 项目配置弹窗 -->
  <ProjectConfigDialog ref="configDialogRef" @success="getList" />
  <!-- 分红配置弹窗 -->
  <DividendPeriodDialog ref="dividendDialogRef" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import download from '@/utils/download'
import { InfoApi, InfoVO } from '@/api/project/projectinfo'
import InfoForm from './InfoForm.vue'
import ProjectAuditDialog from './ProjectAuditDialog.vue'
import OperationStat from './OperationStat.vue'
import ProjectConfigDialog from './ProjectConfigDialog.vue'
import DividendPeriodDialog from './DividendPeriodDialog.vue'
import { useRouter } from 'vue-router'

/** 项目核心表（基础+状态） 列表 */
defineOptions({ name: 'ProjectInfo' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const router = useRouter() // 路由

const loading = ref(true) // 列表的加载中
const list = ref<InfoVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  projectName: undefined,
  projectType: undefined,
  assetType: undefined,
  publisherUserId: undefined,
  publisherCompanyName: undefined,
  createTime: [],
  issueQuantity: undefined,
  issueUnitPrice: undefined,
  remainingQuantity: undefined,
  expectedAnnualReturn: undefined,
  minimumPurchase: undefined,
  issueChainId: undefined,
  subscriptionStartTime: [],
  subscriptionEndTime: [],
  lockStartTime: [],
  lockEndTime: [],
  projectStatus: undefined,
  projectIntro: undefined,
  projectFileUrls: undefined,
  subscriptionContractIds: undefined,
  dividendContractIds: undefined,
  maturityRedemptionContractIds: undefined,
  earlyRedemptionContractIds: undefined,
  earlyRedemptionFeeJson: undefined,
  projectImageUrls: undefined,
  projectVideoUrl: undefined,
  auditStatus: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const exportSummaryLoading = ref(false)
const selectedProjectIds = ref<number[]>([])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await InfoApi.getInfoPage(queryParams)
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

/** 新增项目 - 跳转到全页面 */
const handleCreate = () => {
  router.push({ name: 'ProjectCreate' })
}

/** 进入项目空间 */
const enterProjectSpace = (id: number) => {
  router.push({ name: 'ProjectSpace', query: { id } })
}

/** 编辑项目 - 跳转到全页面 */
const handleEdit = (id: number) => {
  router.push({ name: 'ProjectCreate', query: { id } })
}

/** 打开审核弹窗 */
const auditDialogRef = ref()
const openAuditDialog = (id: number, type: string = 'audit') => {
  auditDialogRef.value.open(id, type)
}

/** 添加/修改操作（审核仍用弹窗） */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  if (type === 'create') {
    handleCreate()
  } else if (type === 'update' && id) {
    handleEdit(id)
  } else {
    formRef.value.open(type, id)
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await InfoApi.deleteInfo(id)
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
    const data = await InfoApi.exportInfo(queryParams)
    download.excel(data, '项目核心表（基础+状态）.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const handleSelectionChange = (rows: InfoVO[]) => {
  selectedProjectIds.value = rows.map((row) => row.projectId)
}

const handleExportByProject = async () => {
  try {
    await message.exportConfirm()
    exportSummaryLoading.value = true
    const params: any = {}
    if (selectedProjectIds.value.length > 0) {
      params.projectIds = selectedProjectIds.value.join(',')
    }
    const data = await InfoApi.exportProjectSummary(params)
    download.excel(data, '项目汇总.xls')
  } catch {
  } finally {
    exportSummaryLoading.value = false
  }
}

/** 跳转到公告列表 */
const handleNotice = (projectId: number) => {
  router.push({
    name: 'ProjectNotice',
    query: {
      projectId: projectId
    }
  })
}

/** 提交审核操作 */
const handleAuditSubmit = async (projectId: number) => {
  try {
    await message.confirm('确认提交审核吗？')
    await InfoApi.submitAudit({ projectId })
    message.success('提交审核成功')
    await getList()
  } catch {}
}

/** 提交运行审核操作 */
const handleRunSubmit = async (projectId: number) => {
  try {
    await message.confirm('确认提交运行审核吗？')
    await InfoApi.submitRunAudit({ projectId })
    message.success('提交运行审核成功')
    await getList()
  } catch {}
}

/** 上下架按钮操作 */
const handleSellStatus = async (row: InfoVO) => {
  const newStatus = row.sellStatus === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'

  try {
    await message.confirm(`确认要${statusText}该项目吗？`)
    await InfoApi.updateSellStatus({
      projectId: row.projectId,
      sellStatus: newStatus
    })
    message.success(`${statusText}成功`)
    await getList()
  } catch {}
}

/** 运营统计按钮操作 */
const opStatRef = ref()
const handleOperation = (projectId: number) => {
  opStatRef.value.open(projectId)
}

/** 项目配置按钮操作 */
const configDialogRef = ref()
const openConfigDialog = (projectId: number, projectName: string) => {
  configDialogRef.value.open(projectId, projectName)
}

/** 分红配置按钮操作 */
const dividendDialogRef = ref()
const openDividendDialog = (projectId: number, projectName: string) => {
  dividendDialogRef.value.open(projectId, projectName)
}

/** 初始化 **/
onMounted(() => {
  getList()
})

/** 组件激活时刷新列表（用于从创建页面返回） **/
onActivated(() => {
  getList()
})
</script>
