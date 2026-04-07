<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="关联链ID（chain_manage.id）" prop="chainId">
        <el-input v-model="formData.chainId" placeholder="请输入关联链ID（chain_manage.id）" />
      </el-form-item>
      <el-form-item label="用户在该链上的地址（如ETH地址：0x...）" prop="chainAddress">
        <el-input v-model="formData.chainAddress" placeholder="请输入用户在该链上的地址（如ETH地址：0x...）" />
      </el-form-item>
      <el-form-item label="identity_id" prop="identityId">
        <el-input v-model="formData.identityId" placeholder="请输入identity_id" />
      </el-form-item>
      <el-form-item label="链上状态" prop="chainStatus">
        <el-select v-model="formData.chainStatus" placeholder="请选择链上状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_USER_CHAIN_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否默认地址：1-是 0-否（同一链下仅1个默认）" prop="isDefault">
        <el-radio-group v-model="formData.isDefault">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="地址备注（如“常用钱包”）" prop="addressRemark">
        <el-input v-model="formData.addressRemark" placeholder="请输入地址备注（如“常用钱包”）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { ChainApi, ChainVO } from '@/api/user/userchain'

/** 用户链地址表= 表单 */
defineOptions({ name: 'ChainForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userId: undefined,
  chainId: undefined,
  chainAddress: undefined,
  identityId: undefined,
  chainStatus: undefined,
  isDefault: undefined,
  addressRemark: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  chainId: [{ required: true, message: '关联链ID（chain_manage.id）不能为空', trigger: 'blur' }],
  chainAddress: [{ required: true, message: '用户在该链上的地址（如ETH地址：0x...）不能为空', trigger: 'blur' }],
  isDefault: [{ required: true, message: '是否默认地址：1-是 0-否（同一链下仅1个默认）不能为空', trigger: 'blur' }]
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
      formData.value = await ChainApi.getChain(id)
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
    const data = formData.value as unknown as ChainVO
    if (formType.value === 'create') {
      await ChainApi.createChain(data)
      message.success(t('common.createSuccess'))
    } else {
      await ChainApi.updateChain(data)
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
    userId: undefined,
    chainId: undefined,
    chainAddress: undefined,
    identityId: undefined,
    chainStatus: undefined,
    isDefault: undefined,
    addressRemark: undefined
  }
  formRef.value?.resetFields()
}
</script>