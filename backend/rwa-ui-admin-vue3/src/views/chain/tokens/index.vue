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
      <el-form-item label="代币名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入代币名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="代币符号" prop="symbol">
        <el-input
          v-model="queryParams.symbol"
          placeholder="请输入代币符号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="小数位数" prop="decimals">
        <el-input
          v-model="queryParams.decimals"
          placeholder="请输入小数位数"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="代币合约地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入代币合约地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="所有者地址" prop="ownerAddress">
        <el-input
          v-model="queryParams.ownerAddress"
          placeholder="请输入所有者地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="部署者地址" prop="deployerAddress">
        <el-input
          v-model="queryParams.deployerAddress"
          placeholder="请输入部署者地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份注册表存储ID" prop="identityRegistryStorageId">
        <el-input
          v-model="queryParams.identityRegistryStorageId"
          placeholder="请输入身份注册表存储ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份注册表地址" prop="identityRegistryAddress">
        <el-input
          v-model="queryParams.identityRegistryAddress"
          placeholder="请输入身份注册表地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="声明主题注册表地址" prop="claimTopicsRegistryAddress">
        <el-input
          v-model="queryParams.claimTopicsRegistryAddress"
          placeholder="请输入声明主题注册表地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="可信发行者注册表地址" prop="trustedIssuersRegistryAddress">
        <el-input
          v-model="queryParams.trustedIssuersRegistryAddress"
          placeholder="请输入可信发行者注册表地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="模块化合规合约地址" prop="modularComplianceAddress">
        <el-input
          v-model="queryParams.modularComplianceAddress"
          placeholder="请输入模块化合规合约地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="代币链上ID地址" prop="tokenOnchainIdAddress">
        <el-input
          v-model="queryParams.tokenOnchainIdAddress"
          placeholder="请输入代币链上ID地址"
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
      <el-form-item label="状态，如pending-待处理，deployed-已部署" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态，如pending-待处理，deployed-已部署"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
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
      <el-form-item label="代币代理，JSON格式" prop="tokenAgents">
        <el-input
          v-model="queryParams.tokenAgents"
          placeholder="请输入代币代理，JSON格式"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="声明主题，JSON格式" prop="claimTopics">
        <el-input
          v-model="queryParams.claimTopics"
          placeholder="请输入声明主题，JSON格式"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="发行者，JSON格式" prop="issuers">
        <el-input
          v-model="queryParams.issuers"
          placeholder="请输入发行者，JSON格式"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="发行者声明，JSON格式" prop="issuerClaims">
        <el-input
          v-model="queryParams.issuerClaims"
          placeholder="请输入发行者声明，JSON格式"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="部署信息，JSON格式" prop="deploymentInfo">
        <el-input
          v-model="queryParams.deploymentInfo"
          placeholder="请输入部署信息，JSON格式"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="错误信息，若有" prop="errorMessage">
        <el-input
          v-model="queryParams.errorMessage"
          placeholder="请输入错误信息，若有"
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
          v-hasPermi="['chain:tokens:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['chain:tokens:export']"
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
      <el-table-column label="代币名称" align="center" prop="name" />
      <el-table-column label="代币符号" align="center" prop="symbol" />
      <el-table-column label="小数位数" align="center" prop="decimals" />
      <el-table-column label="代币合约地址" align="center" prop="address" />
      <el-table-column label="所有者地址" align="center" prop="ownerAddress" />
      <el-table-column label="部署者地址" align="center" prop="deployerAddress" />
      <el-table-column label="身份注册表存储ID" align="center" prop="identityRegistryStorageId" />
      <el-table-column label="身份注册表地址" align="center" prop="identityRegistryAddress" />
      <el-table-column label="声明主题注册表地址" align="center" prop="claimTopicsRegistryAddress" />
      <el-table-column label="可信发行者注册表地址" align="center" prop="trustedIssuersRegistryAddress" />
      <el-table-column label="模块化合规合约地址" align="center" prop="modularComplianceAddress" />
      <el-table-column label="代币链上ID地址" align="center" prop="tokenOnchainIdAddress" />
      <el-table-column label="交易哈希" align="center" prop="transactionHash" />
      <el-table-column label="区块号" align="center" prop="blockNumber" />
      <el-table-column label="状态，如pending-待处理，deployed-已部署" align="center" prop="status" />
      <el-table-column label="盐值，用于加密或哈希计算" align="center" prop="salt" />
      <el-table-column label="代币代理，JSON格式" align="center" prop="tokenAgents" />
      <el-table-column label="声明主题，JSON格式" align="center" prop="claimTopics" />
      <el-table-column label="发行者，JSON格式" align="center" prop="issuers" />
      <el-table-column label="发行者声明，JSON格式" align="center" prop="issuerClaims" />
      <el-table-column label="部署信息，JSON格式" align="center" prop="deploymentInfo" />
      <el-table-column label="错误信息，若有" align="center" prop="errorMessage" />
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
            v-hasPermi="['chain:tokens:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['chain:tokens:delete']"
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
  <TokensForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { TokensApi, TokensVO } from '@/api/chain/tokens'
import TokensForm from './TokensForm.vue'

/** 代币 列表 */
defineOptions({ name: 'Tokens' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<TokensVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  symbol: undefined,
  decimals: undefined,
  address: undefined,
  ownerAddress: undefined,
  deployerAddress: undefined,
  identityRegistryStorageId: undefined,
  identityRegistryAddress: undefined,
  claimTopicsRegistryAddress: undefined,
  trustedIssuersRegistryAddress: undefined,
  modularComplianceAddress: undefined,
  tokenOnchainIdAddress: undefined,
  transactionHash: undefined,
  blockNumber: undefined,
  status: undefined,
  salt: undefined,
  tokenAgents: undefined,
  claimTopics: undefined,
  issuers: undefined,
  issuerClaims: undefined,
  deploymentInfo: undefined,
  errorMessage: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TokensApi.getTokensPage(queryParams)
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
    await TokensApi.deleteTokens(id)
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
    const data = await TokensApi.exportTokens(queryParams)
    download.excel(data, '代币.xls')
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