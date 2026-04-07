<template>
  <Dialog title="查看发行商信息" v-model="dialogVisible" width="800px">
    <el-descriptions :column="2" border v-loading="formLoading">
      <el-descriptions-item label="用户手机号">{{ formData.userPhone }}</el-descriptions-item>
      <el-descriptions-item label="注册时间">{{ formatDate(formData.registerTime) }}</el-descriptions-item>
      <el-descriptions-item label="身份认证状态">
        <el-tag :type="getStatusType(formData.identityAuthStatus)">
          {{ getStatusText(formData.identityAuthStatus) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="认证身份">{{ formData.authIdentity }}</el-descriptions-item>
      <el-descriptions-item label="公司名称" :span="2">{{ formData.companyName }}</el-descriptions-item>
      <el-descriptions-item label="统一社会信用代码">{{ formData.companyCreditCode }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ formData.email }}</el-descriptions-item>
      <el-descriptions-item label="身份证姓名">{{ formData.idCardName }}</el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ formData.idCardNo }}</el-descriptions-item>
      <el-descriptions-item label="身份证有效期">{{ formData.idCardExpireTime }}</el-descriptions-item>
      <el-descriptions-item label="开户名">{{ formData.bankAccountName }}</el-descriptions-item>
      <el-descriptions-item label="银行账户">{{ formData.bankAccount }}</el-descriptions-item>
      <el-descriptions-item label="开户行">{{ formData.bankName }}</el-descriptions-item>
    </el-descriptions>

    <!-- 图片信息 -->
    <el-divider content-position="left">证件照片</el-divider>
    <el-row :gutter="20">
      <el-col :span="8" v-if="formData.businessLicenseUrl">
        <div class="image-item">
          <div class="image-label">营业执照</div>
          <el-image
            :src="formData.businessLicenseUrl"
            :preview-src-list="[formData.businessLicenseUrl]"
            fit="cover"
            class="preview-image"
          />
        </div>
      </el-col>
      <el-col :span="8" v-if="formData.idCardFrontUrl">
        <div class="image-item">
          <div class="image-label">身份证正面</div>
          <el-image
            :src="formData.idCardFrontUrl"
            :preview-src-list="[formData.idCardFrontUrl]"
            fit="cover"
            class="preview-image"
          />
        </div>
      </el-col>
      <el-col :span="8" v-if="formData.idCardBackUrl">
        <div class="image-item">
          <div class="image-label">身份证背面</div>
          <el-image
            :src="formData.idCardBackUrl"
            :preview-src-list="[formData.idCardBackUrl]"
            fit="cover"
            class="preview-image"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 资质文件 -->
    <template v-if="formData.qualificationFileUrls">
      <el-divider content-position="left">资质文件</el-divider>
      <div class="file-list">
        <div v-for="(url, index) in parseFileUrls(formData.qualificationFileUrls)" :key="index" class="file-item">
          <el-link :href="url" target="_blank" type="primary" :underline="false">
            <el-icon><Download /></el-icon>
            资质文件 {{ index + 1 }}
          </el-link>
        </div>
      </div>
    </template>

    <!-- 授权文件 -->
    <template v-if="formData.authorizationFileUrls">
      <el-divider content-position="left">授权文件</el-divider>
      <div class="file-list">
        <div v-for="(url, index) in parseFileUrls(formData.authorizationFileUrls)" :key="index" class="file-item">
          <el-link :href="url" target="_blank" type="primary" :underline="false">
            <el-icon><Download /></el-icon>
            授权文件 {{ index + 1 }}
          </el-link>
        </div>
      </div>
    </template>

    <!-- 审核信息（仅审核通过时显示） -->
    <template v-if="formData.identityAuthStatus === 2 && auditInfo">
      <el-divider content-position="left">审核信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审核状态">
          <el-tag type="success">审核通过</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatDate(auditInfo.auditTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ auditInfo.auditRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </template>

    <!-- 审核失败时显示 -->
    <template v-if="formData.identityAuthStatus === 3 && auditInfo">
      <el-divider content-position="left">审核信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审核状态">
          <el-tag type="danger">审核不通过</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatDate(auditInfo.auditTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ auditInfo.auditRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </template>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { PublisherInfoApi, PublisherInfoVO } from '@/api/system/publisherinfo'
import { formatDate } from '@/utils/formatTime'
import { getPublisherInfoByTenantId } from '@/api/system/tenant/audit'
import { Download } from '@element-plus/icons-vue'

/** 发行商信息 查看 */
defineOptions({ name: 'PublisherInfoViewForm' })

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref<Partial<PublisherInfoVO>>({})
const auditInfo = ref<{ auditTime?: Date; auditRemark?: string } | null>(null)

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  formLoading.value = true
  formData.value = {}
  auditInfo.value = null
  try {
    formData.value = await PublisherInfoApi.getPublisherInfo(id)

    // 如果是已认证或认证失败，尝试获取审核信息
    if (formData.value.identityAuthStatus === 2 || formData.value.identityAuthStatus === 3) {
      try {
        // 这里假设tenantId和发行商id相关联，实际可能需要调整
        const tenantInfo = await getPublisherInfoByTenantId(id)
        if (tenantInfo) {
          auditInfo.value = {
            auditTime: tenantInfo.auditTime,
            auditRemark: tenantInfo.auditRemark
          }
        }
      } catch (e) {
        // 如果获取审核信息失败，忽略
        console.log('获取审核信息失败', e)
      }
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open })

/** 获取状态类型 */
const getStatusType = (status: number | undefined) => {
  switch (status) {
    case 0:
      return 'info'
    case 1:
      return 'warning'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

/** 获取状态文本 */
const getStatusText = (status: number | undefined) => {
  switch (status) {
    case 0:
      return '未认证'
    case 1:
      return '认证中'
    case 2:
      return '已认证'
    case 3:
      return '认证失败'
    default:
      return '未知'
  }
}

/** 解析文件URL列表（用逗号分隔） */
const parseFileUrls = (urls: string | undefined): string[] => {
  if (!urls) return []
  return urls.split(',').filter((url) => url.trim())
}
</script>

<style scoped>
.image-item {
  text-align: center;
  margin-bottom: 16px;
}

.image-label {
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
}

.preview-image {
  width: 200px;
  height: 150px;
  border-radius: 4px;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
