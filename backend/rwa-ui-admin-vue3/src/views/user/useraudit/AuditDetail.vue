<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="关联用户ID">{{ formData.userId }}</el-descriptions-item>
      <el-descriptions-item label="用户姓名">{{ formData.realName }}</el-descriptions-item>
      <el-descriptions-item label="证件号">{{ formData.idCard }}</el-descriptions-item>
      <el-descriptions-item label="证件号有效期">{{ formData.idCardExpire }}</el-descriptions-item>
      
      <el-descriptions-item label="证件正面">
        <el-image
          v-if="formData.idCardFrontUrl"
          class="h-80px w-80px"
          :src="formData.idCardFrontUrl"
          :preview-src-list="[formData.idCardFrontUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-descriptions-item>

      <el-descriptions-item label="证件反面">
        <el-image
          v-if="formData.idCardBackUrl"
          class="h-80px w-80px"
          :src="formData.idCardBackUrl"
          :preview-src-list="[formData.idCardBackUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-descriptions-item>

      <el-descriptions-item label="投资资质图片">
        <div v-if="investmentQualificationUrls.length" class="flex gap-2 flex-wrap">
          <el-image
            v-for="(url, index) in investmentQualificationUrls"
            :key="index"
            class="h-80px w-80px"
            :src="url"
            :preview-src-list="investmentQualificationUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </div>
        <span v-else>暂无</span>
      </el-descriptions-item>

      <el-descriptions-item label="银行流水图片">
        <div v-if="bankFlowUrls.length" class="flex gap-2 flex-wrap">
           <el-image
            v-for="(url, index) in bankFlowUrls"
            :key="index"
            class="h-80px w-80px"
            :src="url"
            :preview-src-list="bankFlowUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </div>
        <span v-else>暂无</span>
      </el-descriptions-item>

      <el-descriptions-item label="住址证明图片">
        <div v-if="residenceProofUrls.length" class="flex gap-2 flex-wrap">
           <el-image
            v-for="(url, index) in residenceProofUrls"
            :key="index"
            class="h-80px w-80px"
            :src="url"
            :preview-src-list="residenceProofUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </div>
        <span v-else>暂无</span>
      </el-descriptions-item>

      <el-descriptions-item label="邮箱">{{ formData.email }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ formData.contactPhone }}</el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="formData.auditStatus" />
      </el-descriptions-item>
      <el-descriptions-item label="审核备注">{{ formData.auditRemark }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { AuditApi, AuditVO } from '@/api/user/useraudit'

defineOptions({ name: 'AuditDetail' })

const dialogVisible = ref(false)
const dialogTitle = ref('认证详情')
const formData = ref({} as AuditVO)

// 图片解析逻辑
const parseImageUrls = (value?: string): string[] => {
  if (!value) {
    return []
  }
  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) {
      return parsed.filter((item) => typeof item === 'string' && item.length > 0)
    }
  } catch {}
  return value
    .split(',')
    .map((item) => item.trim())
    .filter((item) => item.length > 0)
}

const investmentQualificationUrls = computed(() => parseImageUrls(formData.value.investmentQualificationUrl))
const bankFlowUrls = computed(() => parseImageUrls(formData.value.bankFlowUrl))
const residenceProofUrls = computed(() => parseImageUrls(formData.value.residenceProofUrl))

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  formData.value = {} as AuditVO // 重置
  if (id) {
    formData.value = await AuditApi.getAudit(id)
  }
}

defineExpose({ open })
</script>
