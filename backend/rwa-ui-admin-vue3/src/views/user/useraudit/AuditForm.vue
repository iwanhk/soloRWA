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
      <el-form-item label="用户姓名" prop="realName">
        <el-input v-model="formData.realName" placeholder="请输入用户姓名" />
      </el-form-item>
      <el-form-item label="证件号" prop="idCard">
        <el-input v-model="formData.idCard" placeholder="请输入证件号" />
      </el-form-item>
      <el-form-item label="证件号有效期" prop="idCardExpire">
        <el-input v-model="formData.idCardExpire" placeholder="请输入证件号有效期" />
      </el-form-item>
      <el-form-item label="证件正面" prop="idCardFrontUrl">
        <UploadImg v-model="formData.idCardFrontUrl" />
      </el-form-item>
      <el-form-item label="证件反面" prop="idCardBackUrl">
        <UploadImg v-model="formData.idCardBackUrl" />
      </el-form-item>
      <el-form-item label="投资资质图片地址" prop="investmentQualificationUrl">
        <UploadImg v-model="formData.investmentQualificationUrl" />
      </el-form-item>
      <el-form-item label="银行流水单图片地址" prop="bankFlowUrl">
        <UploadImg v-model="formData.bankFlowUrl" />
      </el-form-item>
      <el-form-item label="住址证明图片地址" prop="residenceProofUrl">
        <UploadImg v-model="formData.residenceProofUrl" />
      </el-form-item>
<!--      <el-form-item label="关联用户银行卡ID（user_bank_card.id）" prop="bankCardId">
        <el-input v-model="formData.bankCardId" placeholder="请输入关联用户银行卡ID（user_bank_card.id）" />
      </el-form-item>-->
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
      </el-form-item>
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
<!--      <el-form-item label="提交版本" prop="submitVersion">
        <el-input v-model="formData.submitVersion" placeholder="请输入提交版本" />
      </el-form-item>-->
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark" placeholder="请输入审核备注" />
      </el-form-item>
<!--      <el-form-item label="是否为最新提交记录（1-是 0-否）" prop="isLatest">
        <el-radio-group v-model="formData.isLatest">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { AuditApi, AuditVO } from '@/api/user/useraudit'

/** 用户投资者认证审核 表单 */
defineOptions({ name: 'AuditForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userId: undefined,
  realName: undefined,
  idCard: undefined,
  idCardExpire: undefined,
  idCardFrontUrl: undefined,
  idCardBackUrl: undefined,
  investmentQualificationUrl: undefined,
  bankFlowUrl: undefined,
  residenceProofUrl: undefined,
  bankCardId: undefined,
  email: undefined,
  contactPhone: undefined,
  auditStatus: undefined,
  submitVersion: undefined,
  auditRemark: undefined,
  isLatest: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '关联用户ID不能为空', trigger: 'blur' }],
  auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }],
  submitVersion: [{ required: true, message: '提交版本不能为空', trigger: 'blur' }],
  isLatest: [{ required: true, message: '是否为最新提交记录（1-是 0-否）不能为空', trigger: 'blur' }]
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
      formData.value = await AuditApi.getAudit(id)
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
    const data = formData.value as unknown as AuditVO
    if (formType.value === 'create') {
      await AuditApi.createAudit(data)
      message.success(t('common.createSuccess'))
    } else {
      await AuditApi.updateAudit(data)
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
    realName: undefined,
    idCard: undefined,
    idCardExpire: undefined,
    idCardFrontUrl: undefined,
    idCardBackUrl: undefined,
    investmentQualificationUrl: undefined,
    bankFlowUrl: undefined,
    residenceProofUrl: undefined,
    bankCardId: undefined,
    email: undefined,
    contactPhone: undefined,
    auditStatus: undefined,
    submitVersion: undefined,
    auditRemark: undefined,
    isLatest: undefined
  }
  formRef.value?.resetFields()
}
</script>
