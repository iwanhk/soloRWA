<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="区块链地址" prop="address">
        <el-input v-model="formData.address" placeholder="请输入区块链地址" />
      </el-form-item>
      <el-form-item label="身份ID，关联到对应的身份表" prop="identityId">
        <el-input v-model="formData.identityId" placeholder="请输入身份ID，关联到对应的身份表" />
      </el-form-item>
      <el-form-item label="身份类型，如ClaimIssuer、User等" prop="type">
        <el-select v-model="formData.type" placeholder="请选择身份类型，如ClaimIssuer、User等">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="合约地址" prop="contractAddress">
        <el-input v-model="formData.contractAddress" placeholder="请输入合约地址" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { AddressIdentitiesApi, AddressIdentitiesVO } from '@/api/chain/addressidentities'

/** 地址身份关联 表单 */
defineOptions({ name: 'AddressIdentitiesForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  address: undefined,
  identityId: undefined,
  type: undefined,
  contractAddress: undefined
})
const formRules = reactive({
  address: [{ required: true, message: '区块链地址不能为空', trigger: 'blur' }],
  identityId: [{ required: true, message: '身份ID，关联到对应的身份表不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '身份类型，如ClaimIssuer、User等不能为空', trigger: 'change' }]
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
      formData.value = await AddressIdentitiesApi.getAddressIdentities(id)
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
    const data = formData.value as unknown as AddressIdentitiesVO
    if (formType.value === 'create') {
      await AddressIdentitiesApi.createAddressIdentities(data)
      message.success(t('common.createSuccess'))
    } else {
      await AddressIdentitiesApi.updateAddressIdentities(data)
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
    identityId: undefined,
    type: undefined,
    contractAddress: undefined
  }
  formRef.value?.resetFields()
}
</script>