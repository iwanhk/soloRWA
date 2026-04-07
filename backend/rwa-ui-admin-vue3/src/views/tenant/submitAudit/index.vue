<template>
  <div class="submit-audit-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">提交审核</span>
          <el-tag v-if="auditStatus !== null" :type="getStatusType(auditStatus)" size="large">
            {{ getStatusText(auditStatus) }}
          </el-tag>
        </div>
      </template>

      <!-- 拒绝原因提示 -->
      <el-alert
        v-if="auditStatus === 3 && auditRemark"
        type="error"
        :title="'拒绝原因：' + auditRemark"
        :closable="false"
        show-icon
        class="mb-4"
      />

      <!-- 待审核提示 -->
      <el-alert
        v-if="auditStatus === 1"
        type="warning"
        title="您的资料正在审核中，请耐心等待..."
        :closable="false"
        show-icon
        class="mb-4"
      />

      <!-- 审核通过提示 -->
      <el-alert
        v-if="auditStatus === 2"
        type="success"
        title="恭喜！您的资质审核已通过"
        :closable="false"
        show-icon
        class="mb-4"
      />

      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="140px"
        :disabled="auditStatus === 1 || auditStatus === 2"
      >
        <el-divider content-position="left">公司信息</el-divider>
        
        <el-form-item label="企业类型" prop="companyType">
          <el-select v-model="formData.companyType" placeholder="请选择企业类型">
            <el-option label="中国大陆企业" :value="1" />
            <el-option label="中国香港企业" :value="2" />
            <el-option label="海外企业" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item :label="formData.companyType === 1 ? '统一社会信用代码' : '企业注册代码'" prop="companyCreditCode">
          <el-input v-model="formData.companyCreditCode" :placeholder="formData.companyType === 1 ? '18位统一社会信用代码' : '请输入企业注册代码'" />
        </el-form-item>
        
        <el-form-item label="营业执照" prop="businessLicenseUrl">
          <UploadImg v-model="formData.businessLicenseUrl" />
        </el-form-item>
        
        <el-form-item label="资质文件" prop="qualificationFileUrls">
          <UploadFile v-model="formData.qualificationFileUrls" />
        </el-form-item>
        
        <el-divider content-position="left">经办人信息</el-divider>
        
        <el-form-item label="认证身份" prop="authIdentity">
          <el-select v-model="formData.authIdentity" placeholder="请选择认证身份">
            <template v-if="formData.companyType === 1">
              <el-option label="企业法人" value="企业法人" />
              <el-option label="经办人" value="经办人" />
            </template>
            <template v-else-if="formData.companyType === 2">
              <el-option label="授权董事" value="授权董事" />
              <el-option label="授权签字人" value="授权签字人" />
            </template>
            <template v-else>
              <el-option label="董事" value="董事" />
              <el-option label="授权签字人" value="授权签字人" />
            </template>
          </el-select>
        </el-form-item>
        
        <el-form-item label="证件姓名" prop="idCardName">
          <el-input v-model="formData.idCardName" placeholder="请输入证件姓名" />
        </el-form-item>
        
        <el-form-item label="证件号" prop="idCardNo">
          <el-input v-model="formData.idCardNo" placeholder="请输入证件号" />
        </el-form-item>
        
        <el-form-item label="证件有效期" prop="idCardExpireTime">
          <el-date-picker
            v-model="formData.idCardExpireTime"
            type="date"
            placeholder="选择有效期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="证件正面" prop="idCardFrontUrl">
          <UploadImg v-model="formData.idCardFrontUrl" />
        </el-form-item>
        
        <el-form-item label="证件反面" prop="idCardBackUrl">
          <UploadImg v-model="formData.idCardBackUrl" />
        </el-form-item>
        
        <el-form-item label="授权文件" prop="authorizationFileUrls">
          <UploadFile v-model="formData.authorizationFileUrls" />
        </el-form-item>
        
        <el-divider content-position="left">联系信息</el-divider>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱（选填）" />
        </el-form-item>
        
        <el-divider content-position="left">银行信息</el-divider>
        
        <el-form-item label="开户名" prop="bankAccountName">
          <el-input v-model="formData.bankAccountName" placeholder="与公司名称/法人姓名一致" />
        </el-form-item>
        
        <el-form-item label="银行账户" prop="bankAccount">
          <el-input v-model="formData.bankAccount" placeholder="请输入银行卡号" />
        </el-form-item>
        
        <el-form-item label="开户行" prop="bankName">
          <el-input v-model="formData.bankName" placeholder="请输入开户行" />
        </el-form-item>
        
        <!-- 提交按钮 -->
        <el-form-item v-if="auditStatus === 0 || auditStatus === 3">
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交审核
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import * as AuditApi from '@/api/system/tenant/audit'
import { UploadImg, UploadFile } from '@/components/UploadFile'

defineOptions({ name: 'TenantSubmitAudit' })

const message = useMessage()

const loading = ref(false)
const submitting = ref(false)
const auditStatus = ref<number | null>(null) // 0-待提交 1-待审核 2-审核通过 3-审核拒绝
const auditRemark = ref('')

const formRef = ref()
const formData = ref({
  companyType: 1, // 1-中国大陆 2-香港 3-海外
  companyCreditCode: '',
  businessLicenseUrl: '',
  qualificationFileUrls: undefined,
  authorizationFileUrls: undefined,
  authIdentity: '',
  idCardName: '',
  idCardNo: '',
  idCardExpireTime: '',
  idCardFrontUrl: '',
  idCardBackUrl: '',
  email: '',
  bankAccountName: '',
  bankAccount: '',
  bankName: ''
})

const formRules = reactive({
  companyCreditCode: [{ required: true, message: '统一社会信用代码不能为空', trigger: 'blur' }],
  businessLicenseUrl: [{ required: true, message: '营业执照不能为空', trigger: 'blur' }],
  authIdentity: [{ required: true, message: '认证身份不能为空', trigger: 'change' }],
  idCardName: [{ required: true, message: '身份证姓名不能为空', trigger: 'blur' }],
  idCardNo: [{ required: true, message: '身份证号不能为空', trigger: 'blur' }],
  idCardExpireTime: [{ required: true, message: '身份证有效期不能为空', trigger: 'change' }],
  idCardFrontUrl: [{ required: true, message: '身份证正面不能为空', trigger: 'blur' }],
  idCardBackUrl: [{ required: true, message: '身份证背面不能为空', trigger: 'blur' }],
  bankAccountName: [{ required: true, message: '开户名不能为空', trigger: 'blur' }],
  bankAccount: [{ required: true, message: '银行账户不能为空', trigger: 'blur' }],
  bankName: [{ required: true, message: '开户行不能为空', trigger: 'blur' }]
})

// 获取状态类型
const getStatusType = (status: number) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const texts = {
    0: '待提交审核',
    1: '审核中',
    2: '审核通过',
    3: '审核拒绝'
  }
  return texts[status] || '未知'
}

// 加载审核状态
const loadAuditStatus = async () => {
  loading.value = true
  try {
    const res = await AuditApi.getAuditStatus()
    auditStatus.value = res.auditStatus
    auditRemark.value = res.auditRemark || ''
    
    // 加载已有的发行商信息进行回显
    await loadPublisherInfo()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// 加载发行商信息
const loadPublisherInfo = async () => {
  try {
    const res = await AuditApi.getPublisherInfo()
    if (res) {
      formData.value.companyType = (res as any).companyType || 1
      formData.value.companyCreditCode = res.companyCreditCode || ''
      formData.value.businessLicenseUrl = res.businessLicenseUrl || ''
      formData.value.qualificationFileUrls = res.qualificationFileUrls as any
      formData.value.authorizationFileUrls = res.authorizationFileUrls as any
      formData.value.authIdentity = res.authIdentity || ''
      formData.value.idCardName = res.idCardName || ''
      formData.value.idCardNo = res.idCardNo || ''
      formData.value.idCardExpireTime = res.idCardExpireTime as any || ''
      formData.value.idCardFrontUrl = res.idCardFrontUrl || ''
      formData.value.idCardBackUrl = res.idCardBackUrl || ''
      formData.value.email = res.email || ''
      formData.value.bankAccountName = res.bankAccountName || ''
      formData.value.bankAccount = res.bankAccount || ''
      formData.value.bankName = res.bankName || ''
    }
  } catch (e) {
    console.error('加载发行商信息失败', e)
  }
}

// 提交审核
const handleSubmit = async () => {
  await formRef.value?.validate()
  
  submitting.value = true
  try {
    const data = JSON.parse(JSON.stringify(formData.value))
    
    // 处理多文件上传字段
    if (data.qualificationFileUrls && Array.isArray(data.qualificationFileUrls)) {
      data.qualificationFileUrls = data.qualificationFileUrls.map((file: any) => file.url || file).join(',')
    }
    if (data.authorizationFileUrls && Array.isArray(data.authorizationFileUrls)) {
      data.authorizationFileUrls = data.authorizationFileUrls.map((file: any) => file.url || file).join(',')
    }
    
    await AuditApi.submitAudit(data)
    message.success('提交成功，请等待审核')
    await loadAuditStatus()
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  loadAuditStatus()
})
</script>


<style scoped>
.submit-audit-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 18px;
  font-weight: bold;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
