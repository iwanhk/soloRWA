<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="初始化步骤名称" prop="step">
        <el-input v-model="formData.step" placeholder="请输入初始化步骤名称" />
      </el-form-item>
      <el-form-item label="状态，如pending-待处理，completed-已完成" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="相关合约地址" prop="contractAddress">
        <el-input v-model="formData.contractAddress" placeholder="请输入相关合约地址" />
      </el-form-item>
      <el-form-item label="交易哈希" prop="transactionHash">
        <el-input v-model="formData.transactionHash" placeholder="请输入交易哈希" />
      </el-form-item>
      <el-form-item label="区块号" prop="blockNumber">
        <el-input v-model="formData.blockNumber" placeholder="请输入区块号" />
      </el-form-item>
      <el-form-item label="错误信息，若有" prop="errorMessage">
        <el-input v-model="formData.errorMessage" placeholder="请输入错误信息，若有" />
      </el-form-item>
      <el-form-item label="元数据，JSON格式" prop="metadata">
        <el-input v-model="formData.metadata" placeholder="请输入元数据，JSON格式" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { SystemInitializationApi, SystemInitializationVO } from '@/api/chain/systeminitialization'

/** 系统初始化 表单 */
defineOptions({ name: 'SystemInitializationForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  step: undefined,
  status: undefined,
  contractAddress: undefined,
  transactionHash: undefined,
  blockNumber: undefined,
  errorMessage: undefined,
  metadata: undefined
})
const formRules = reactive({
  step: [{ required: true, message: '初始化步骤名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态，如pending-待处理，completed-已完成不能为空', trigger: 'blur' }]
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
      formData.value = await SystemInitializationApi.getSystemInitialization(id)
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
    const data = formData.value as unknown as SystemInitializationVO
    if (formType.value === 'create') {
      await SystemInitializationApi.createSystemInitialization(data)
      message.success(t('common.createSuccess'))
    } else {
      await SystemInitializationApi.updateSystemInitialization(data)
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
    step: undefined,
    status: undefined,
    contractAddress: undefined,
    transactionHash: undefined,
    blockNumber: undefined,
    errorMessage: undefined,
    metadata: undefined
  }
  formRef.value?.resetFields()
}
</script>