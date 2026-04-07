<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="存储合约地址" prop="address">
        <el-input v-model="formData.address" placeholder="请输入存储合约地址" />
      </el-form-item>
      <el-form-item label="部署者地址" prop="deployerAddress">
        <el-input v-model="formData.deployerAddress" placeholder="请输入部署者地址" />
      </el-form-item>
      <el-form-item label="交易哈希" prop="transactionHash">
        <el-input v-model="formData.transactionHash" placeholder="请输入交易哈希" />
      </el-form-item>
      <el-form-item label="区块号" prop="blockNumber">
        <el-input v-model="formData.blockNumber" placeholder="请输入区块号" />
      </el-form-item>
      <el-form-item label="绑定的代币数量" prop="boundTokenCount">
        <el-input v-model="formData.boundTokenCount" placeholder="请输入绑定的代币数量" />
      </el-form-item>
      <el-form-item label="状态，如deployed-已部署，active-活跃" prop="status">
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
import { IdentityRegistryStoragesApi, IdentityRegistryStoragesVO } from '@/api/chain/identityregistrystorages'

/** 身份注册表存储 表单 */
defineOptions({ name: 'IdentityRegistryStoragesForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  address: undefined,
  deployerAddress: undefined,
  transactionHash: undefined,
  blockNumber: undefined,
  boundTokenCount: undefined,
  status: undefined
})
const formRules = reactive({
  address: [{ required: true, message: '存储合约地址不能为空', trigger: 'blur' }],
  deployerAddress: [{ required: true, message: '部署者地址不能为空', trigger: 'blur' }],
  boundTokenCount: [{ required: true, message: '绑定的代币数量不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态，如deployed-已部署，active-活跃不能为空', trigger: 'blur' }]
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
      formData.value = await IdentityRegistryStoragesApi.getIdentityRegistryStorages(id)
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
    const data = formData.value as unknown as IdentityRegistryStoragesVO
    if (formType.value === 'create') {
      await IdentityRegistryStoragesApi.createIdentityRegistryStorages(data)
      message.success(t('common.createSuccess'))
    } else {
      await IdentityRegistryStoragesApi.updateIdentityRegistryStorages(data)
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
    address: undefined,
    deployerAddress: undefined,
    transactionHash: undefined,
    blockNumber: undefined,
    boundTokenCount: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>