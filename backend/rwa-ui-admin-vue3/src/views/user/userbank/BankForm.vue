<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="关联用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入关联用户ID" />
      </el-form-item>
      <el-form-item label="开户名" prop="bankAccountName">
        <el-input v-model="formData.bankAccountName" placeholder="请输入银行卡开户名（需与实名一致）" />
      </el-form-item>
      <el-form-item label="银行卡号" prop="bankAccount">
        <el-input v-model="formData.bankAccount" placeholder="请输入银行卡号" />
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input v-model="formData.bankName" placeholder="请输入开户行" />
      </el-form-item>
<!--      <el-form-item label="开户行支行" prop="bankBranch">
        <el-input v-model="formData.bankBranch" placeholder="请输入开户行支行" />
      </el-form-item>
      <el-form-item label="是否默认银行卡" prop="isDefault">
        <el-radio-group v-model="formData.isDefault">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>-->
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="formData.auditStatus" placeholder="请选择审核状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark" placeholder="请输入审核备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { BankApi, BankVO } from '@/api/user/userbank'
import {DICT_TYPE, getIntDictOptions} from "@/utils/dict";

/** 用户银行卡信息 表单 */
defineOptions({ name: 'BankForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userId: undefined,
  bankAccountName: undefined,
  bankAccount: undefined,
  bankName: undefined,
  bankBranch: undefined,
  isDefault: undefined,
  auditStatus: undefined,
  auditRemark: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '关联用户ID不能为空', trigger: 'blur' }],
  bankAccountName: [{ required: true, message: '银行卡开户名（需与实名一致）不能为空', trigger: 'blur' }],
  bankAccount: [{ required: true, message: '银行卡号不能为空', trigger: 'blur' }],
  bankName: [{ required: true, message: '开户行不能为空', trigger: 'blur' }],
  isDefault: [{ required: true, message: '是否默认银行卡不能为空', trigger: 'blur' }],
  auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'blur' }]
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
      formData.value = await BankApi.getBank(id)
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
    const data = formData.value as unknown as BankVO
    if (formType.value === 'create') {
      await BankApi.createBank(data)
      message.success(t('common.createSuccess'))
    } else {
      await BankApi.updateBank(data)
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
    bankAccountName: undefined,
    bankAccount: undefined,
    bankName: undefined,
    bankBranch: undefined,
    isDefault: undefined,
    auditStatus: undefined,
    auditRemark: undefined
  }
  formRef.value?.resetFields()
}
</script>
