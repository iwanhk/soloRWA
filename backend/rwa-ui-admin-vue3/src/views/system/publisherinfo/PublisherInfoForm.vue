<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="用户手机号（冗余sys_user.phonenumber）" prop="userPhone">
        <el-input 
          v-model="formData.userPhone" 
          placeholder="请输入用户手机号（冗余sys_user.phonenumber）" 
          :disabled="formType === 'update'"
        />
      </el-form-item>
      <el-form-item label="注册时间（冗余sys_user.create_time）" prop="registerTime">
        <el-date-picker
          v-model="formData.registerTime"
          type="date"
          value-format="x"
          placeholder="选择注册时间（冗余sys_user.create_time）"
        />
      </el-form-item>
      <el-form-item label="身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败" prop="identityAuthStatus">
        <el-radio-group v-model="formData.identityAuthStatus">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="认证身份（如“企业法人”“经办人”）" prop="authIdentity">
        <el-input v-model="formData.authIdentity" placeholder="请输入认证身份（如“企业法人”“经办人”）" />
      </el-form-item>
      <el-form-item label="公司名称（企业全称，与营业执照一致）" prop="companyName">
        <el-input v-model="formData.companyName" placeholder="请输入公司名称（企业全称，与营业执照一致）" />
      </el-form-item>
      <el-form-item label="公司统一社会信用代码（唯一，18位）" prop="companyCreditCode">
        <el-input v-model="formData.companyCreditCode" placeholder="请输入公司统一社会信用代码（唯一，18位）" />
      </el-form-item>
      <el-form-item label="营业执照URL（图片/文件）" prop="businessLicenseUrl">
        <el-input v-model="formData.businessLicenseUrl" placeholder="请输入营业执照URL（图片/文件）" />
      </el-form-item>
      <el-form-item label="资质文件URL（多个用,分隔）" prop="qualificationFileUrls">
        <el-input v-model="formData.qualificationFileUrls" placeholder="请输入资质文件URL（多个用,分隔）" />
      </el-form-item>
      <el-form-item label="授权文件URL（多个用,分隔）" prop="authorizationFileUrls">
        <el-input v-model="formData.authorizationFileUrls" placeholder="请输入授权文件URL（多个用,分隔）" />
      </el-form-item>
      <el-form-item label="身份证姓名" prop="idCardName">
        <el-input v-model="formData.idCardName" placeholder="请输入身份证姓名" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="formData.idCardNo" placeholder="请输入身份证号" />
      </el-form-item>
      <el-form-item label="身份证有效期" prop="idCardExpireTime">
        <el-date-picker
          v-model="formData.idCardExpireTime"
          type="date"
          value-format="x"
          placeholder="选择身份证有效期"
        />
      </el-form-item>
      <el-form-item label="身份证正面URL" prop="idCardFrontUrl">
        <el-input v-model="formData.idCardFrontUrl" placeholder="请输入身份证正面URL" />
      </el-form-item>
      <el-form-item label="身份证背面URL" prop="idCardBackUrl">
        <el-input v-model="formData.idCardBackUrl" placeholder="请输入身份证背面URL" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="开户名（与公司名称/法人姓名一致）" prop="bankAccountName">
        <el-input v-model="formData.bankAccountName" placeholder="请输入开户名（与公司名称/法人姓名一致）" />
      </el-form-item>
      <el-form-item label="银行账户（卡号）" prop="bankAccount">
        <el-input v-model="formData.bankAccount" placeholder="请输入银行账户（卡号）" />
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input v-model="formData.bankName" placeholder="请输入开户行" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { PublisherInfoApi, PublisherInfoVO } from '@/api/system/publisherinfo'

/** 发行商 表单 */
defineOptions({ name: 'PublisherInfoForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userPhone: undefined,
  registerTime: undefined,
  identityAuthStatus: undefined,
  authIdentity: undefined,
  companyName: undefined,
  companyCreditCode: undefined,
  businessLicenseUrl: undefined,
  qualificationFileUrls: undefined,
  authorizationFileUrls: undefined,
  idCardName: undefined,
  idCardNo: undefined,
  idCardExpireTime: undefined,
  idCardFrontUrl: undefined,
  idCardBackUrl: undefined,
  email: undefined,
  bankAccountName: undefined,
  bankAccount: undefined,
  bankName: undefined
})
const formRules = reactive({
  userPhone: [{ required: true, message: '用户手机号（冗余sys_user.phonenumber）不能为空', trigger: 'blur' }],
  registerTime: [{ required: true, message: '注册时间（冗余sys_user.create_time）不能为空', trigger: 'blur' }],
  identityAuthStatus: [{ required: true, message: '身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败不能为空', trigger: 'blur' }],
  companyName: [{ required: true, message: '公司名称（企业全称，与营业执照一致）不能为空', trigger: 'blur' }],
  companyCreditCode: [{ required: true, message: '公司统一社会信用代码（唯一，18位）不能为空', trigger: 'blur' }]
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
      formData.value = await PublisherInfoApi.getPublisherInfo(id)
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
    const data = formData.value as unknown as PublisherInfoVO
    if (formType.value === 'create') {
      await PublisherInfoApi.createPublisherInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await PublisherInfoApi.updatePublisherInfo(data)
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
    userPhone: undefined,
    registerTime: undefined,
    identityAuthStatus: undefined,
    authIdentity: undefined,
    companyName: undefined,
    companyCreditCode: undefined,
    businessLicenseUrl: undefined,
    qualificationFileUrls: undefined,
    authorizationFileUrls: undefined,
    idCardName: undefined,
    idCardNo: undefined,
    idCardExpireTime: undefined,
    idCardFrontUrl: undefined,
    idCardBackUrl: undefined,
    email: undefined,
    bankAccountName: undefined,
    bankAccount: undefined,
    bankName: undefined
  }
  formRef.value?.resetFields()
}
</script>