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
      <el-form-item label="发行者地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入发行者地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="发行者合约地址" prop="contractAddress">
        <el-input
          v-model="queryParams.contractAddress"
          placeholder="请输入发行者合约地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="管理密钥地址" prop="managementKey">
        <el-input
          v-model="queryParams.managementKey"
          placeholder="请输入管理密钥地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="关联的区块链地址ID" prop="blockchainAddressId">
        <el-input
          v-model="queryParams.blockchainAddressId"
          placeholder="请输入关联的区块链地址ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="盐值，用于加密或哈希计算" prop="salt">
        <el-input
          v-model="queryParams.salt"
          placeholder="请输入盐值，用于加密或哈希计算"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="交易哈希" prop="transactionHash">
        <el-input
          v-model="queryParams.transactionHash"
          placeholder="请输入交易哈希"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="区块号" prop="blockNumber">
        <el-input
          v-model="queryParams.blockNumber"
          placeholder="请输入区块号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="合约是否已部署，0-未部署，1-已部署" prop="contractDeployed">
        <el-select
          v-model="queryParams.contractDeployed"
          placeholder="请选择合约是否已部署，0-未部署，1-已部署"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="声明密钥是否已设置，0-未设置，1-已设置" prop="claimKeySetup">
        <el-select
          v-model="queryParams.claimKeySetup"
          placeholder="请选择声明密钥是否已设置，0-未设置，1-已设置"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态，如active-活跃" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态，如active-活跃"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
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
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['chain:claim-issuer-identities:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['chain:claim-issuer-identities:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="发行者地址" align="center" prop="address" />
      <el-table-column label="发行者合约地址" align="center" prop="contractAddress" />
      <el-table-column label="管理密钥地址" align="center" prop="managementKey" />
      <el-table-column label="关联的区块链地址ID" align="center" prop="blockchainAddressId" />
      <el-table-column label="盐值，用于加密或哈希计算" align="center" prop="salt" />
      <el-table-column label="交易哈希" align="center" prop="transactionHash" />
      <el-table-column label="区块号" align="center" prop="blockNumber" />
      <el-table-column label="合约是否已部署，0-未部署，1-已部署" align="center" prop="contractDeployed" />
      <el-table-column label="声明密钥是否已设置，0-未设置，1-已设置" align="center" prop="claimKeySetup" />
      <el-table-column label="状态，如active-活跃" align="center" prop="status" />
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
            v-hasPermi="['chain:claim-issuer-identities:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['chain:claim-issuer-identities:delete']"
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
  <ClaimIssuerIdentitiesForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { ClaimIssuerIdentitiesApi, ClaimIssuerIdentitiesVO } from '@/api/chain/claimissueridentities'
import ClaimIssuerIdentitiesForm from './ClaimIssuerIdentitiesForm.vue'

/** 声明发行者身份 列表 */
defineOptions({ name: 'ClaimIssuerIdentities' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ClaimIssuerIdentitiesVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  address: undefined,
  contractAddress: undefined,
  managementKey: undefined,
  blockchainAddressId: undefined,
  salt: undefined,
  transactionHash: undefined,
  blockNumber: undefined,
  contractDeployed: undefined,
  claimKeySetup: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ClaimIssuerIdentitiesApi.getClaimIssuerIdentitiesPage(queryParams)
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
    await ClaimIssuerIdentitiesApi.deleteClaimIssuerIdentities(id)
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
    const data = await ClaimIssuerIdentitiesApi.exportClaimIssuerIdentities(queryParams)
    download.excel(data, '声明发行者身份.xls')
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