<template>
  <Dialog title="审核银行卡" v-model="dialogVisible" width="500px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="开户名">
        <span>{{ bankInfo.bankAccountName }}</span>
      </el-form-item>
      <el-form-item label="银行卡号">
        <span>{{ bankInfo.bankAccount }}</span>
      </el-form-item>
      <el-form-item label="开户行">
        <span>{{ bankInfo.bankName }}</span>
      </el-form-item>
      <el-form-item label="审核结果" prop="auditStatus">
        <el-radio-group v-model="formData.auditStatus">
          <el-radio :value="2">通过</el-radio>
          <el-radio :value="3">不通过</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="formData.auditRemark"
          type="textarea"
          :rows="3"
          placeholder="请输入审核备注"
        />
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

/** 银行卡审核 表单 */
defineOptions({ name: 'BankAuditForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const bankInfo = ref<Partial<BankVO>>({}) // 银行卡信息
const formData = ref({
  id: undefined as number | undefined,
  auditStatus: undefined as number | undefined,
  auditRemark: ''
})
const formRules = reactive({
  auditStatus: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  resetForm()
  formData.value.id = id
  // 加载银行卡详情
  formLoading.value = true
  try {
    bankInfo.value = await BankApi.getBank(id)
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    await BankApi.auditBank({
      id: formData.value.id!,
      auditStatus: formData.value.auditStatus!,
      auditRemark: formData.value.auditRemark
    })
    message.success('审核成功')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  bankInfo.value = {}
  formData.value = {
    id: undefined,
    auditStatus: undefined,
    auditRemark: ''
  }
  formRef.value?.resetFields()
}
</script>
