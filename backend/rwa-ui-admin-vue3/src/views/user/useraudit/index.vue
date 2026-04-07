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
      <el-form-item label="审核状态" prop="auditStatus">
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
<!--        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['user:audit:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>-->
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['user:audit:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="审核记录ID" align="center" prop="id" />
      <el-table-column label="用户姓名" align="center" prop="realName" />
      <el-table-column label="证件号" align="center" prop="idCard" />
      <el-table-column label="证件号有效期" align="center" prop="idCardExpire" />
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="160px">
        <template #default="scope">
          <el-button
            v-if="scope.row.auditStatus === 1"
            link
            type="primary"
            @click="openReviewDialog(scope.row)"
            v-hasPermi="['user:audit:update']"
          >
            审核
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('view', scope.row.id)"
            v-hasPermi="['user:audit:query']"
          >
            查看
          </el-button>
<!--          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['user:audit:delete']"
          >
            删除
          </el-button>-->
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
  <AuditForm ref="formRef" @success="getList" />

  <!-- 审核弹窗 -->
  <Dialog v-model="reviewDialogVisible" title="用户认证审核" width="800px">
    <el-form :model="reviewForm" label-width="120px">
      <el-form-item label="审核记录ID">
        <span>{{ reviewDetail?.id }}</span>
      </el-form-item>
      <el-form-item label="关联用户ID">
        <span>{{ reviewDetail?.userId }}</span>
      </el-form-item>
      <el-form-item label="用户姓名">
        <span>{{ reviewDetail?.realName }}</span>
      </el-form-item>
      <el-form-item label="证件号">
        <span>{{ reviewDetail?.idCard }}</span>
      </el-form-item>
      <el-form-item label="证件有效期">
        <span>{{ reviewDetail?.idCardExpire }}</span>
      </el-form-item>
      <el-form-item label="证件正面">
        <el-image
          v-if="reviewDetail?.idCardFrontUrl"
          class="h-80px w-80px"
          :src="reviewDetail.idCardFrontUrl"
          :preview-src-list="[reviewDetail.idCardFrontUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="证件反面">
        <el-image
          v-if="reviewDetail?.idCardBackUrl"
          class="h-80px w-80px"
          :src="reviewDetail.idCardBackUrl"
          :preview-src-list="[reviewDetail.idCardBackUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="投资资质图片">
        <template v-if="investmentQualificationUrls.length">
          <el-image
            v-for="(url, index) in investmentQualificationUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="investmentQualificationUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="银行流水图片">
        <template v-if="bankFlowUrls.length">
          <el-image
            v-for="(url, index) in bankFlowUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="bankFlowUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="住址证明图片">
        <template v-if="residenceProofUrls.length">
          <el-image
            v-for="(url, index) in residenceProofUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="residenceProofUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="银行卡ID">
        <span>{{ reviewDetail?.bankCardId }}</span>
      </el-form-item>
      <el-form-item label="邮箱">
        <span>{{ reviewDetail?.email }}</span>
      </el-form-item>
      <el-form-item label="联系电话">
        <span>{{ reviewDetail?.contactPhone }}</span>
      </el-form-item>
      <el-form-item label="提交版本">
        <span>{{ reviewDetail?.submitVersion }}</span>
      </el-form-item>
      <el-form-item label="是否为最新记录">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="reviewDetail?.isLatest" />
      </el-form-item>
      <el-form-item label="当前状态">
        <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="reviewDetail?.auditStatus" />
      </el-form-item>
      <el-divider />
      <el-form-item label="审核状态">
        <el-select v-model="reviewForm.auditStatus" placeholder="请选择审核状态" class="!w-240px">
          <el-option :value="2" label="审核通过" />
          <el-option :value="3" label="审核驳回" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核备注">
        <el-input
          v-model="reviewForm.auditRemark"
          type="textarea"
          :rows="3"
          placeholder="请输入审核备注（驳回时建议填写原因）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reviewDialogVisible = false">取 消</el-button>
      <el-button type="primary" :loading="reviewLoading" @click="submitReview">确 定</el-button>
    </template>
  </Dialog>

  <!-- 详情弹窗 -->
  <AuditDetail ref="detailRef" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { AuditApi, AuditVO } from '@/api/user/useraudit'
import AuditForm from './AuditForm.vue'
import AuditDetail from './AuditDetail.vue'

/** 用户投资者认证审核 列表 */
defineOptions({ name: 'UserAudit' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<AuditVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  auditStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

// 审核弹窗相关
const reviewDialogVisible = ref(false)
const reviewLoading = ref(false)
const reviewDetail = ref<AuditVO | null>(null)
const parseImageUrls = (value?: string): string[] => {
  if (!value) {
    return []
  }
  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) {
      return parsed.filter((item) => typeof item === 'string' && item.length > 0)
    }
  } catch {}
  return value
    .split(',')
    .map((item) => item.trim())
    .filter((item) => item.length > 0)
}
const investmentQualificationUrls = computed(() =>
  parseImageUrls(reviewDetail.value?.investmentQualificationUrl)
)
const bankFlowUrls = computed(() => parseImageUrls(reviewDetail.value?.bankFlowUrl))
const residenceProofUrls = computed(() => parseImageUrls(reviewDetail.value?.residenceProofUrl))
const reviewForm = reactive<{
  id: number | null
  auditStatus: number | null
  auditRemark: string
}>({
  id: null,
  auditStatus: 2,
  auditRemark: ''
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AuditApi.getAuditPage(queryParams)
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
const detailRef = ref()
const openForm = (type: string, id?: number) => {
  if (type === 'view') {
    detailRef.value.open(id)
    return
  }
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await AuditApi.deleteAudit(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 打开审核弹窗（仅待审核记录可见按钮） */
const openReviewDialog = (row: AuditVO) => {
  reviewDetail.value = row
  reviewForm.id = row.id
  reviewForm.auditStatus = 2
  reviewForm.auditRemark = row.auditRemark || ''
  reviewDialogVisible.value = true
}

/** 提交审核 */
const submitReview = async () => {
  if (!reviewForm.id || !reviewForm.auditStatus) {
    message.error('请选择审核状态')
    return
  }
  try {
    reviewLoading.value = true
    await AuditApi.reviewAudit({
      id: reviewForm.id,
      auditStatus: reviewForm.auditStatus,
      auditRemark: reviewForm.auditRemark
    })
    message.success('审核成功')
    reviewDialogVisible.value = false
    await getList()
  } finally {
    reviewLoading.value = false
  }
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await AuditApi.exportAudit(queryParams)
    download.excel(data, '用户投资者认证审核.xls')
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
