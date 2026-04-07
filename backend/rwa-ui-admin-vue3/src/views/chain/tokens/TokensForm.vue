<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="代币名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入代币名称" />
      </el-form-item>
      <el-form-item label="代币符号" prop="symbol">
        <el-input v-model="formData.symbol" placeholder="请输入代币符号" />
      </el-form-item>
      <el-form-item label="小数位数" prop="decimals">
        <el-input v-model="formData.decimals" placeholder="请输入小数位数" />
      </el-form-item>
      <el-form-item label="代币合约地址" prop="address">
        <el-input v-model="formData.address" placeholder="请输入代币合约地址" />
      </el-form-item>
      <el-form-item label="所有者地址" prop="ownerAddress">
        <el-input v-model="formData.ownerAddress" placeholder="请输入所有者地址" />
      </el-form-item>
      <el-form-item label="部署者地址" prop="deployerAddress">
        <el-input v-model="formData.deployerAddress" placeholder="请输入部署者地址" />
      </el-form-item>
      <el-form-item label="身份注册表存储ID" prop="identityRegistryStorageId">
        <el-input v-model="formData.identityRegistryStorageId" placeholder="请输入身份注册表存储ID" />
      </el-form-item>
      <el-form-item label="身份注册表地址" prop="identityRegistryAddress">
        <el-input v-model="formData.identityRegistryAddress" placeholder="请输入身份注册表地址" />
      </el-form-item>
      <el-form-item label="声明主题注册表地址" prop="claimTopicsRegistryAddress">
        <el-input v-model="formData.claimTopicsRegistryAddress" placeholder="请输入声明主题注册表地址" />
      </el-form-item>
      <el-form-item label="可信发行者注册表地址" prop="trustedIssuersRegistryAddress">
        <el-input v-model="formData.trustedIssuersRegistryAddress" placeholder="请输入可信发行者注册表地址" />
      </el-form-item>
      <el-form-item label="模块化合规合约地址" prop="modularComplianceAddress">
        <el-input v-model="formData.modularComplianceAddress" placeholder="请输入模块化合规合约地址" />
      </el-form-item>
      <el-form-item label="代币链上ID地址" prop="tokenOnchainIdAddress">
        <el-input v-model="formData.tokenOnchainIdAddress" placeholder="请输入代币链上ID地址" />
      </el-form-item>
      <el-form-item label="交易哈希" prop="transactionHash">
        <el-input v-model="formData.transactionHash" placeholder="请输入交易哈希" />
      </el-form-item>
      <el-form-item label="区块号" prop="blockNumber">
        <el-input v-model="formData.blockNumber" placeholder="请输入区块号" />
      </el-form-item>
      <el-form-item label="状态，如pending-待处理，deployed-已部署" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="盐值，用于加密或哈希计算" prop="salt">
        <el-input v-model="formData.salt" placeholder="请输入盐值，用于加密或哈希计算" />
      </el-form-item>
      <el-form-item label="代币代理，JSON格式" prop="tokenAgents">
        <el-input v-model="formData.tokenAgents" placeholder="请输入代币代理，JSON格式" />
      </el-form-item>
      <el-form-item label="声明主题，JSON格式" prop="claimTopics">
        <el-input v-model="formData.claimTopics" placeholder="请输入声明主题，JSON格式" />
      </el-form-item>
      <el-form-item label="发行者，JSON格式" prop="issuers">
        <el-input v-model="formData.issuers" placeholder="请输入发行者，JSON格式" />
      </el-form-item>
      <el-form-item label="发行者声明，JSON格式" prop="issuerClaims">
        <el-input v-model="formData.issuerClaims" placeholder="请输入发行者声明，JSON格式" />
      </el-form-item>
      <el-form-item label="部署信息，JSON格式" prop="deploymentInfo">
        <el-input v-model="formData.deploymentInfo" placeholder="请输入部署信息，JSON格式" />
      </el-form-item>
      <el-form-item label="错误信息，若有" prop="errorMessage">
        <el-input v-model="formData.errorMessage" placeholder="请输入错误信息，若有" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { TokensApi, TokensVO } from '@/api/chain/tokens'

/** 代币 表单 */
defineOptions({ name: 'TokensForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
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
  errorMessage: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '代币名称不能为空', trigger: 'blur' }],
  symbol: [{ required: true, message: '代币符号不能为空', trigger: 'blur' }],
  decimals: [{ required: true, message: '小数位数不能为空', trigger: 'blur' }],
  ownerAddress: [{ required: true, message: '所有者地址不能为空', trigger: 'blur' }],
  deployerAddress: [{ required: true, message: '部署者地址不能为空', trigger: 'blur' }],
  identityRegistryStorageId: [{ required: true, message: '身份注册表存储ID不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态，如pending-待处理，deployed-已部署不能为空', trigger: 'blur' }],
  salt: [{ required: true, message: '盐值，用于加密或哈希计算不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await TokensApi.getTokens(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as TokensVO
    if (formType.value === 'create') {
      await TokensApi.createTokens(data)
      message.success(t('common.createSuccess'))
    } else {
      await TokensApi.updateTokens(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
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
    errorMessage: undefined
  }
  formRef.value?.resetFields()
}
</script>