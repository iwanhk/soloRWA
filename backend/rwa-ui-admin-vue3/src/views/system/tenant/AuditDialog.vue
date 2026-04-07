<template>
  <Dialog :title="'审核租户：' + tenantName" v-model="dialogVisible" width="900px">
    <div v-loading="loading">
      <el-alert
        type="info"
        title="请仔细核对发行商提交的资质信息后进行审核"
        :closable="false"
        show-icon
        class="mb-4"
      />

      <!-- 发行商信息展示 -->
      <el-descriptions :column="2" border class="mb-4" v-if="publisherInfo">
        <el-descriptions-item label="企业类型" :span="2">
          <el-tag v-if="(publisherInfo as any).companyType === 1">中国大陆企业</el-tag>
          <el-tag v-else-if="(publisherInfo as any).companyType === 2" type="warning">中国香港企业</el-tag>
          <el-tag v-else-if="(publisherInfo as any).companyType === 3" type="danger">海外企业</el-tag>
          <span v-else>中国大陆企业</span>
        </el-descriptions-item>
        <el-descriptions-item 
          :label="(publisherInfo as any).companyType === 1 ? '统一社会信用代码' : '企业注册代码'" 
          :span="2"
          label-style="min-width: 140px"
        >
          {{ publisherInfo.companyCreditCode || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="认证身份">
          {{ publisherInfo.authIdentity || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证姓名">
          {{ publisherInfo.idCardName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">
          {{ publisherInfo.idCardNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证有效期">
          {{ publisherInfo.idCardExpireTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ publisherInfo.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="开户名">
          {{ publisherInfo.bankAccountName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="银行账户">
          {{ publisherInfo.bankAccount || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="开户行">
          {{ publisherInfo.bankName || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 图片展示 -->
      <el-divider content-position="left" v-if="publisherInfo">证件附件</el-divider>
      <el-row :gutter="20" v-if="publisherInfo" class="mb-4">
        <el-col :span="8" v-if="publisherInfo.businessLicenseUrl">
          <div class="image-item">
            <div class="image-label">营业执照</div>
            <el-image
              :src="publisherInfo.businessLicenseUrl"
              :preview-src-list="[publisherInfo.businessLicenseUrl]"
              fit="contain"
              class="preview-image"
            />
          </div>
        </el-col>
        <el-col :span="8" v-if="publisherInfo.idCardFrontUrl">
          <div class="image-item">
            <div class="image-label">证件正面</div>
            <el-image
              :src="publisherInfo.idCardFrontUrl"
              :preview-src-list="[publisherInfo.idCardFrontUrl]"
              fit="contain"
              class="preview-image"
            />
          </div>
        </el-col>
        <el-col :span="8" v-if="publisherInfo.idCardBackUrl">
          <div class="image-item">
            <div class="image-label">证件背面</div>
            <el-image
              :src="publisherInfo.idCardBackUrl"
              :preview-src-list="[publisherInfo.idCardBackUrl]"
              fit="contain"
              class="preview-image"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 资质文件展示 -->
      <el-divider content-position="left" v-if="publisherInfo && publisherInfo.qualificationFileUrls">资质文件</el-divider>
      <div v-if="publisherInfo && publisherInfo.qualificationFileUrls" class="file-list mb-4">
        <el-link
          v-for="(url, index) in parseFileUrls(publisherInfo.qualificationFileUrls)"
          :key="'q' + index"
          :href="url"
          target="_blank"
          type="primary"
          class="file-link"
        >
          <el-icon><Document /></el-icon>
          资质文件 {{ index + 1 }}
        </el-link>
      </div>

      <!-- 授权文件展示 -->
      <el-divider content-position="left" v-if="publisherInfo && publisherInfo.authorizationFileUrls">授权文件</el-divider>
      <div v-if="publisherInfo && publisherInfo.authorizationFileUrls" class="file-list mb-4">
        <el-link
          v-for="(url, index) in parseFileUrls(publisherInfo.authorizationFileUrls)"
          :key="'a' + index"
          :href="url"
          target="_blank"
          type="primary"
          class="file-link"
        >
          <el-icon><Document /></el-icon>
          授权文件 {{ index + 1 }}
        </el-link>
      </div>

      <!-- 无数据提示 -->
      <el-empty v-if="!publisherInfo && !loading" description="暂无发行商信息" />

      <!-- 审核表单 -->
      <el-divider content-position="left">审核决定</el-divider>
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="审核结果" prop="auditResult">
          <el-radio-group v-model="formData.auditResult">
            <el-radio :value="2">通过</el-radio>
            <el-radio :value="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="formData.auditRemark"
            type="textarea"
            :rows="3"
            :placeholder="formData.auditResult === 3 ? '请说明拒绝原因（必填）' : '审核备注（选填）'"
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        确认审核
      </el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import * as AuditApi from '@/api/system/tenant/audit'
import { Document } from '@element-plus/icons-vue'

defineOptions({ name: 'TenantAuditDialog' })

const message = useMessage()

// 解析逗号分隔的文件URL
const parseFileUrls = (urls: string | undefined): string[] => {
  if (!urls) return []
  return urls.split(',').filter(url => url.trim())
}


const dialogVisible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const tenantId = ref<number>()
const tenantName = ref('')
const publisherInfo = ref<AuditApi.SubmitAuditVO | null>(null)

const formRef = ref()
const formData = ref({
  auditResult: 2 as number,
  auditRemark: ''
})

const formRules = reactive({
  auditResult: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
})

// 打开弹窗
const open = async (id: number, name: string) => {
  dialogVisible.value = true
  tenantId.value = id
  tenantName.value = name
  formData.value = {
    auditResult: 2,
    auditRemark: ''
  }
  publisherInfo.value = null
  
  // 加载发行商信息
  await loadPublisherInfo(id)
}

// 加载发行商信息
const loadPublisherInfo = async (id: number) => {
  loading.value = true
  try {
    const res = await AuditApi.getPublisherInfoByTenantId(id)
    publisherInfo.value = res
  } catch (e) {
    console.error('加载发行商信息失败', e)
    publisherInfo.value = null
  } finally {
    loading.value = false
  }
}

// 提交审核
const handleSubmit = async () => {
  await formRef.value?.validate()
  
  // 拒绝时必须填写备注
  if (formData.value.auditResult === 3 && !formData.value.auditRemark) {
    message.error('拒绝时必须填写审核备注')
    return
  }
  
  submitting.value = true
  try {
    await AuditApi.auditTenant({
      tenantId: tenantId.value!,
      auditResult: formData.value.auditResult,
      auditRemark: formData.value.auditRemark
    })
    message.success('审核成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

const emit = defineEmits(['success'])
defineExpose({ open })
</script>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}

.image-item {
  text-align: center;
}

.image-label {
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.preview-image {
  width: 100%;
  height: 150px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.file-link {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>

