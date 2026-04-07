<template>
  <Dialog title="查看发行商信息" v-model="dialogVisible" width="800px">
    <el-descriptions :column="2" border v-loading="formLoading">
      <el-descriptions-item label="发行商编号">{{ formData.id }}</el-descriptions-item>
      <el-descriptions-item label="发行商名">{{ formData.name }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ formData.contactName }}</el-descriptions-item>
      <el-descriptions-item label="联系手机">{{ formData.contactMobile }}</el-descriptions-item>
      <el-descriptions-item label="发行商套餐">
        <el-tag v-if="formData.packageId === 0" type="danger">系统发行商</el-tag>
        <el-tag v-else type="success">{{ getPackageName(formData.packageId) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="发行商状态">
        <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="formData.status" />
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <el-tag v-if="formData.auditStatus === 0" type="info">待提交</el-tag>
        <el-tag v-else-if="formData.auditStatus === 1" type="warning">待审核</el-tag>
        <el-tag v-else-if="formData.auditStatus === 2" type="success">已通过</el-tag>
        <el-tag v-else-if="formData.auditStatus === 3" type="danger">已拒绝</el-tag>
        <el-tag v-else type="info">-</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="绑定域名">
        <el-tag v-for="website in formData.websites || []" :key="website" class="mr-1">
          {{ website }}
        </el-tag>
        <span v-if="!formData.websites || formData.websites.length === 0">-</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDate(formData.createTime) }}</el-descriptions-item>

    </el-descriptions>

    <!-- 审核信息（仅审核通过或拒绝时显示） -->
    <template v-if="formData.auditStatus === 2 || formData.auditStatus === 3">
      <el-divider content-position="left">审核信息</el-divider>
      <el-descriptions border>
      <el-descriptions-item label-width="24%" label="审核备注">{{ formData.auditRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </template>

    <!-- 发行商详细信息（审核通过时显示） -->
    <template v-if="formData.auditStatus === 2 && publisherInfo">
      <el-divider content-position="left">发行商详细信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公司名称" :span="2">{{ formData.name }}</el-descriptions-item>
        <el-descriptions-item label="企业类型">
          <el-tag v-if="publisherInfo.companyType === 1">中国大陆企业</el-tag>
          <el-tag v-else-if="publisherInfo.companyType === 2" type="warning">中国香港企业</el-tag>
          <el-tag v-else-if="publisherInfo.companyType === 3" type="danger">海外企业</el-tag>
          <span v-else>中国大陆企业</span>
        </el-descriptions-item>
        <el-descriptions-item :label="publisherInfo.companyType === 1 || !publisherInfo.companyType ? '统一社会信用代码' : '企业注册代码'">{{ publisherInfo.companyCreditCode }}</el-descriptions-item>
        <el-descriptions-item label="认证身份">{{ publisherInfo.authIdentity }}</el-descriptions-item>
        <el-descriptions-item label="身份证姓名">{{ publisherInfo.idCardName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ publisherInfo.idCardNo }}</el-descriptions-item>
        <el-descriptions-item label="身份证有效期">{{ publisherInfo.idCardExpireTime }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ publisherInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="开户名">{{ publisherInfo.bankAccountName }}</el-descriptions-item>
        <el-descriptions-item label="银行账户">{{ publisherInfo.bankAccount }}</el-descriptions-item>
        <el-descriptions-item label="开户行">{{ publisherInfo.bankName }}</el-descriptions-item>
      </el-descriptions>

      <!-- 证件照片 -->
      <el-divider content-position="left">证件照片</el-divider>
      <el-row :gutter="20">
        <el-col :span="8" v-if="publisherInfo.businessLicenseUrl">
          <div class="image-item">
            <div class="image-label">营业执照</div>
            <el-image
              :src="publisherInfo.businessLicenseUrl"
              :preview-src-list="[publisherInfo.businessLicenseUrl]"
              fit="cover"
              class="preview-image"
            />
          </div>
        </el-col>
        <el-col :span="8" v-if="publisherInfo.idCardFrontUrl">
          <div class="image-item">
            <div class="image-label">身份证正面</div>
            <el-image
              :src="publisherInfo.idCardFrontUrl"
              :preview-src-list="[publisherInfo.idCardFrontUrl]"
              fit="cover"
              class="preview-image"
            />
          </div>
        </el-col>
        <el-col :span="8" v-if="publisherInfo.idCardBackUrl">
          <div class="image-item">
            <div class="image-label">身份证背面</div>
            <el-image
              :src="publisherInfo.idCardBackUrl"
              :preview-src-list="[publisherInfo.idCardBackUrl]"
              fit="cover"
              class="preview-image"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 资质文件 -->
      <template v-if="publisherInfo.qualificationFileUrls">
        <el-divider content-position="left">资质文件</el-divider>
        <div class="file-list">
          <div v-for="(url, index) in parseFileUrls(publisherInfo.qualificationFileUrls)" :key="index" class="file-item">
            <el-link :href="url" target="_blank" type="primary" :underline="false">
              <el-icon><Download /></el-icon>
              资质文件 {{ index + 1 }}
            </el-link>
          </div>
        </div>
      </template>

      <!-- 授权文件 -->
      <template v-if="publisherInfo.authorizationFileUrls">
        <el-divider content-position="left">授权文件</el-divider>
        <div class="file-list">
          <div v-for="(url, index) in parseFileUrls(publisherInfo.authorizationFileUrls)" :key="index" class="file-item">
            <el-link :href="url" target="_blank" type="primary" :underline="false">
              <el-icon><Download /></el-icon>
              授权文件 {{ index + 1 }}
            </el-link>
          </div>
        </div>
      </template>
    </template>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as TenantApi from '@/api/system/tenant'
import * as TenantPackageApi from '@/api/system/tenantPackage'
import { getPublisherInfoByTenantId } from '@/api/system/tenant/audit'
import { Download } from '@element-plus/icons-vue'

/** 发行商信息 查看 */
defineOptions({ name: 'TenantViewForm' })

const dialogVisible = ref(false)
const formLoading = ref(false)
const formData = ref<Partial<TenantApi.TenantVO> & { auditTime?: Date; auditRemark?: string }>({})
const publisherInfo = ref<any>(null)
const packageList = ref<TenantPackageApi.TenantPackageVO[]>([])

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  formLoading.value = true
  formData.value = {}
  publisherInfo.value = null
  try {
    // 加载套餐列表
    packageList.value = await TenantPackageApi.getTenantPackageList()
    // 加载租户详情
    formData.value = await TenantApi.getTenant(id)

    // 如果审核通过，获取发行商详细信息
    if (formData.value.auditStatus === 2) {
      try {
        publisherInfo.value = await getPublisherInfoByTenantId(id)
      } catch (e) {
        console.log('获取发行商信息失败', e)
      }
    }
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open })

/** 获取套餐名称 */
const getPackageName = (packageId: number | undefined) => {
  if (!packageId) return '-'
  const pkg = packageList.value.find((item) => item.id === packageId)
  return pkg ? pkg.name : '-'
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
