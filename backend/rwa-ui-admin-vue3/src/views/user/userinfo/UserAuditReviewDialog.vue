<template>
  <Dialog v-model="dialogVisible" title="用户认证审核" width="800px">
    <el-form ref="formRef" :model="reviewForm" :rules="formRules" label-width="120px">
<!--      <el-form-item label="审核记录ID">
        <span>{{ reviewDetail?.id }}</span>
      </el-form-item>
      <el-form-item label="关联用户ID">
        <span>{{ reviewDetail?.userId }}</span>
      </el-form-item>-->
      <el-form-item label="用户姓名">
        <span>{{ reviewDetail?.realName }}</span>
      </el-form-item>
      <el-form-item label="证件号">
        <span>{{ reviewDetail?.idCard }}</span>
      </el-form-item>
      <el-form-item label="证件有效期">
        <span>{{ reviewDetail?.idCardExpire }}</span>
      </el-form-item>
      <el-form-item label="证件正面">
        <el-image
          v-if="reviewDetail?.idCardFrontUrl"
          class="h-80px w-80px"
          :src="reviewDetail.idCardFrontUrl"
          :preview-src-list="[reviewDetail.idCardFrontUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="证件反面">
        <el-image
          v-if="reviewDetail?.idCardBackUrl"
          class="h-80px w-80px"
          :src="reviewDetail.idCardBackUrl"
          :preview-src-list="[reviewDetail.idCardBackUrl]"
          preview-teleported
          fit="cover"
        />
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="投资资质图片">
        <template v-if="investmentQualificationUrls.length">
          <el-image
            v-for="(url, index) in investmentQualificationUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="investmentQualificationUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="银行流水图片">
        <template v-if="bankFlowUrls.length">
          <el-image
            v-for="(url, index) in bankFlowUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="bankFlowUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      <el-form-item label="住址证明图片">
        <template v-if="residenceProofUrls.length">
          <el-image
            v-for="(url, index) in residenceProofUrls"
            :key="index"
            class="h-80px w-80px mr-8px"
            :src="url"
            :preview-src-list="residenceProofUrls"
            :initial-index="index"
            preview-teleported
            fit="cover"
          />
        </template>
        <span v-else>暂无</span>
      </el-form-item>
      
      <!-- 银行卡信息 -->
      <el-form-item label="银行卡信息" v-if="bankCardInfo">
        <div class="bank-card-info">
          <div class="info-row">
            <span class="label">开户名:</span>
            <span class="value">{{ bankCardInfo.bankAccountName }}</span>
          </div>
          <div class="info-row">
            <span class="label">银行卡号:</span>
            <span class="value">{{ bankCardInfo.bankAccount }}</span>
          </div>
          <div class="info-row">
            <span class="label">开户行:</span>
            <span class="value">{{ bankCardInfo.bankName }}</span>
          </div>
         
        </div>
      </el-form-item>
  
      
      <el-form-item label="邮箱">
        <span>{{ reviewDetail?.email }}</span>
      </el-form-item>
      <el-form-item label="联系电话">
        <span>{{ reviewDetail?.contactPhone }}</span>
      </el-form-item>
<!--      <el-form-item label="提交版本">
        <span>{{ reviewDetail?.submitVersion }}</span>
      </el-form-item>-->
      <el-form-item label="是否为最新记录">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="reviewDetail?.isLatest" v-if="reviewDetail?.isLatest !== undefined" />
      </el-form-item>
      <el-form-item label="当前状态">
        <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="reviewDetail?.auditStatus" v-if="reviewDetail?.auditStatus !== undefined" />
      </el-form-item>
      <el-form-item label="审核备注" v-if="reviewDetail?.auditRemark">
        <span>{{ reviewDetail?.auditRemark }}</span>
      </el-form-item>
      
      <el-divider v-if="!readonly" />
      <el-form-item label="审核状态" v-if="!readonly">
        <el-select v-model="reviewForm.auditStatus" placeholder="请选择审核状态" class="!w-240px">
          <el-option :value="2" label="审核通过" />
          <el-option :value="3" label="审核驳回" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark" v-if="!readonly">
        <el-input
          v-model="reviewForm.auditRemark"
          type="textarea"
          :rows="3"
          placeholder="请输入审核备注（驳回时必填）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">{{ readonly ? '关 闭' : '取 消' }}</el-button>
      <el-button type="primary" :loading="reviewLoading" @click="submitReview" v-if="!readonly">确 定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { AuditApi, AuditVO } from '@/api/user/useraudit'
import { BankApi, BankVO } from '@/api/user/userbank'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'UserAuditReviewDialog' })
const props = defineProps<{
  readonly?: boolean
}>()
const message = useMessage()

const dialogVisible = ref(false)
const reviewLoading = ref(false)
const reviewDetail = ref<AuditVO | null>(null)
const bankCardInfo = ref<BankVO | null>(null)
const formRef = ref<FormInstance>()

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
const investmentQualificationUrls = computed(() =>
  parseImageUrls(reviewDetail.value?.investmentQualificationUrl)
)
const bankFlowUrls = computed(() => parseImageUrls(reviewDetail.value?.bankFlowUrl))
const residenceProofUrls = computed(() => parseImageUrls(reviewDetail.value?.residenceProofUrl))

const reviewForm = reactive<{
  id: number | null
  auditStatus: number | null
  auditRemark: string
}>({
  id: null,
  auditStatus: 2,
  auditRemark: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  auditRemark: [
    {
      validator: (rule, value, callback) => {
        // 如果审核状态为驳回(3),则备注必填
        if (reviewForm.auditStatus === 3 && (!value || value.trim() === '')) {
          callback(new Error('驳回时必须填写审核备注'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

/** 打开弹窗 */
const open = async (id: number, type: 'user' | 'audit' = 'user') => {
  reviewDetail.value = null
  reviewForm.id = null
  // 重置表单验证
  formRef.value?.resetFields()
  
  try {
    if (type === 'audit') {
      // 详情模式:直接获取ID
      const data = await AuditApi.getAudit(id)
      reviewDetail.value = data
      reviewForm.id = data.id
      reviewForm.auditStatus = data.auditStatus
      reviewForm.auditRemark = data.auditRemark || ''
      
      // 获取银行卡信息
      if (data.bankCardId) {
        try {
          bankCardInfo.value = await BankApi.getBank(data.bankCardId)
        } catch (error) {
          console.error('获取银行卡信息失败:', error)
          bankCardInfo.value = null
        }
      } else {
        bankCardInfo.value = null
      }
      
      dialogVisible.value = true
    } else {
      // 审核模式：根据User ID查找
      const data = await AuditApi.getAuditPage({
          pageNo: 1,
          pageSize: 10,
          userId: id,
          isLatest: true
      })
      if (data.list.length > 0) {
          const pending = data.list.find(item => item.auditStatus === 1)
          const target = pending || data.list[0]
          reviewDetail.value = target
          
          reviewForm.id = target.id
          reviewForm.auditStatus = 2
          reviewForm.auditRemark = target.auditRemark || ''
          
          // 获取银行卡信息
          if (target.bankCardId) {
            try {
              bankCardInfo.value = await BankApi.getBank(target.bankCardId)
            } catch (error) {
              console.error('获取银行卡信息失败:', error)
              bankCardInfo.value = null
            }
          } else {
            bankCardInfo.value = null
          }
          
          dialogVisible.value = true
      } else {
          message.warning('未找到该用户的审核记录')
      }
    }
  } catch (e) {
     console.error(e)
     message.error('获取审核记录失败')
  }
}

const emit = defineEmits(['success'])

/** 提交审核 */
const submitReview = async () => {
  if (!reviewForm.id || !reviewForm.auditStatus) {
    message.error('请选择审核状态')
    return
  }
  
  // 验证表单
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    reviewLoading.value = true
    await AuditApi.reviewAudit({
      id: reviewForm.id,
      auditStatus: reviewForm.auditStatus,
      auditRemark: reviewForm.auditRemark
    })
    message.success('审核成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    reviewLoading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.bank-card-info {
  .info-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      font-weight: 500;
      color: #606266;
      min-width: 80px;
      margin-right: 12px;
    }
    
    .value {
      color: #303133;
      flex: 1;
    }
  }
}
</style>
