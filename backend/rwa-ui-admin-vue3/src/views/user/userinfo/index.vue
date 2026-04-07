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
<!--      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>-->
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS).filter(item => item.value !== 4)"
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
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['user:info:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>-->
        <el-button
          type="success"
          plain
          @click="handleExportPurchase"
          :loading="exportPurchaseLoading"
          v-hasPermi="['user:info:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
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
      <el-table-column label="用户ID" align="center" prop="id" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="用户姓名" align="center" prop="realName" />
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
        </template>
      </el-table-column>
      <el-table-column label="2FA状态" align="center" prop="twoFactorAuthStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.F2A_STATUS" :value="scope.row.twoFactorAuthStatus" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="180px">
        <template #default="scope">
          <el-button

            type="primary"
            @click="openForm('view', scope.row.id)"
            v-hasPermi="['user:info:query']"
          >
            查看
          </el-button>

          <el-button

            type="warning"
            @click="handleApprove2FAUnbind(scope.row.id)"
            v-if="scope.row.twoFactorAuthStatus === 3"
            v-hasPermi="['user:info:update']"
          >
            2FA解绑审核
          </el-button>

          <el-button

            type="primary"
            @click="openAuditReview(scope.row.id)"
            v-if="scope.row.auditStatus === 1"
            v-hasPermi="['user:audit:review']"
          >
            用户审核
          </el-button>

          <el-button

            type="warning"
            @click="openBankAudit(scope.row.pendingBankApplyId)"
            v-if="scope.row.auditStatus === 2 && scope.row.pendingBankApplyId"
            v-hasPermi="['user:bank:audit']"
          >
            银行卡审核
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
  <!-- 详情弹窗 -->
  <InfoDetail ref="detailRef" />
  
  <!-- 用户审核弹窗 -->
  <UserAuditReviewDialog ref="auditReviewRef" @success="getList" />

  <!-- 银行卡审核弹窗 -->
  <BankAuditForm ref="bankAuditRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { InfoApi, InfoVO } from '@/api/user/userinfo'
import InfoForm from './InfoForm.vue'
import InfoDetail from './InfoDetail.vue'
import UserAuditReviewDialog from './UserAuditReviewDialog.vue'
import BankAuditForm from '../userbank/BankAuditForm.vue'

/** 用户基础信息 列表 */
defineOptions({ name: 'UserInfo' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<InfoVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  mobile: undefined,
  auditStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const exportPurchaseLoading = ref(false)
const selectedUserIds = ref<number[]>([])

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
    download.excel(data, '用户基础信息.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const handleSelectionChange = (rows: InfoVO[]) => {
  selectedUserIds.value = rows.map((row) => row.id)
}

const handleExportPurchase = async () => {
  try {
    await message.exportConfirm()
    exportPurchaseLoading.value = true
    const params: any = { ...queryParams }
    if (selectedUserIds.value.length > 0) {
      params.userIds = selectedUserIds.value.join(',')
    }
    const data = await InfoApi.exportUserPurchase(params)
    download.excel(data, '用户购买信息.xls')
  } catch {
  } finally {
    exportPurchaseLoading.value = false
  }
}

/** 2FA解绑审核按钮操作 */
const handleApprove2FAUnbind = async (userId: number) => {
  try {
    await ElMessageBox.confirm('请选择审核结果', '2FA解绑审核', {
      distinguishCancelAndClose: true,
      confirmButtonText: '通过',
      cancelButtonText: '拒绝',
      type: 'warning'
    })
    // 通过
    await InfoApi.approve2FAUnbind({ id: userId, approved: true })
    message.success('审核通过，2FA已解绑')
    await getList()
  } catch (action) {
    if (action === 'cancel') {
      // 拒绝 - 需要输入拒绝原因
      const { value: remark } = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '拒绝原因不能为空',
        inputType: 'textarea'
      })
      await InfoApi.approve2FAUnbind({ id: userId, approved: false, remark })
      message.success('审核拒绝，2FA状态恢复')
      await getList()
    }
  }
}

/** 用户审核操作 */
const auditReviewRef = ref()
const openAuditReview = (userId: number) => {
  auditReviewRef.value.open(userId)
}

/** 银行卡审核操作 */
const bankAuditRef = ref()
const openBankAudit = (bankId: number) => {
  bankAuditRef.value.open(bankId)
}


/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
