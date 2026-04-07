<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="合约类型" prop="contractType">
        <el-select v-model="formData.contractType" placeholder="请选择合约类型">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="部署地址" prop="deploymentAddress">
        <el-input v-model="formData.deploymentAddress" placeholder="请输入部署地址" />
      </el-form-item>
      <el-form-item label="部署者地址" prop="deployerAddress">
        <el-input v-model="formData.deployerAddress" placeholder="请输入部署者地址" />
      </el-form-item>
      <el-form-item label="部署信息，JSON格式" prop="deploymentInfo">
        <el-input v-model="formData.deploymentInfo" placeholder="请输入部署信息，JSON格式" />
      </el-form-item>
      <el-form-item label="交易哈希" prop="transactionHash">
        <el-input v-model="formData.transactionHash" placeholder="请输入交易哈希" />
      </el-form-item>
      <el-form-item label="区块号" prop="blockNumber">
        <el-input v-model="formData.blockNumber" placeholder="请输入区块号" />
      </el-form-item>
      <el-form-item label="状态，如deployed-已部署" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { ContractDeploymentsApi, ContractDeploymentsVO } from '@/api/chain/contractdeployments'

/** 合约部署 表单 */
defineOptions({ name: 'ContractDeploymentsForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  contractType: undefined,
  deploymentAddress: undefined,
  deployerAddress: undefined,
  deploymentInfo: undefined,
  transactionHash: undefined,
  blockNumber: undefined,
  status: undefined
})
const formRules = reactive({
  contractType: [{ required: true, message: '合约类型不能为空', trigger: 'change' }],
  deploymentAddress: [{ required: true, message: '部署地址不能为空', trigger: 'blur' }],
  deployerAddress: [{ required: true, message: '部署者地址不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态，如deployed-已部署不能为空', trigger: 'blur' }]
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
      formData.value = await ContractDeploymentsApi.getContractDeployments(id)
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
    const data = formData.value as unknown as ContractDeploymentsVO
    if (formType.value === 'create') {
      await ContractDeploymentsApi.createContractDeployments(data)
      message.success(t('common.createSuccess'))
    } else {
      await ContractDeploymentsApi.updateContractDeployments(data)
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
    contractType: undefined,
    deploymentAddress: undefined,
    deployerAddress: undefined,
    deploymentInfo: undefined,
    transactionHash: undefined,
    blockNumber: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>