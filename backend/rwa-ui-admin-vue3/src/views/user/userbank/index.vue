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

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>

      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">

      <el-table-column label="开户名" align="center" prop="bankAccountName" />
      <el-table-column label="银行卡号" align="center" prop="bankAccount" />
      <el-table-column label="开户行" align="center" prop="bankName" />

      <el-table-column label="审核状态" align="center" prop="auditStatus" >
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
            link
            type="warning"
            @click="openAuditForm(scope.row.id)"
            v-hasPermi="['user:bank:audit']"
            v-if="scope.row.auditStatus === 1"
          >
            审核
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('view', scope.row.id)"
            v-hasPermi="['user:bank:query']"
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
  <BankForm ref="formRef" @success="getList" />

  <!-- 表单弹窗：审核 -->
  <BankAuditForm ref="auditFormRef" @success="getList" />
  
  <!-- 详情弹窗 -->
  <BankDetail ref="detailRef" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { BankApi, BankVO } from '@/api/user/userbank'
import BankForm from './BankForm.vue'
import BankAuditForm from './BankAuditForm.vue'
import BankDetail from './BankDetail.vue'
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'

/** 用户银行卡信息 列表 */
defineOptions({ name: 'UserBank' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<BankVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  bankAccountName: undefined,
  bankAccount: undefined,
  bankName: undefined,
  bankBranch: undefined,
  isDefault: undefined,
  auditStatus: undefined,
  auditRemark: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BankApi.getBankPage(queryParams)
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

/** 审核操作 */
const auditFormRef = ref()
const openAuditForm = (id: number) => {
  auditFormRef.value.open(id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await BankApi.deleteBank(id)
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
    const data = await BankApi.exportBank(queryParams)
    download.excel(data, '用户银行卡信息.xls')
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

