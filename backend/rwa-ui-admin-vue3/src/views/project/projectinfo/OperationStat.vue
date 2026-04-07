<template>
  <el-dialog v-model="visible" title="运营统计" width="800px" append-to-body>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="参与投资人数">
        {{ formData.investorCount || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="申请分红人数">
        {{ formData.dividendApplyCount || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="申请分红金额" :span="2">
        <span class="amount-text">{{ fenToYuan(formData.dividendApplyAmount || 0, 8) }}</span>
        <span class="currency-label ml-5px">{{ earningCurrency }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="提前赎回人数">
        {{ formData.earlyRedemptionPeople || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="提前赎回金额" :span="2">
        <span class="amount-text">{{ fenToYuan(formData.earlyRedemptionAmount || 0, 8) }}</span>
        <span class="currency-label ml-5px">{{ investmentCurrency }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="提前赎回份额">
        {{ formData.earlyRedemptionCount || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="到期赎回人数">
        {{ formData.maturityRedemptionCount || 0 }}
      </el-descriptions-item>
      <el-descriptions-item label="到期赎回金额" :span="2">
        <span class="amount-text">{{ fenToYuan(formData.maturityRedemptionAmount || 0, 8) }}</span>
        <span class="currency-label ml-5px">{{ investmentCurrency }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="投资人总收益" :span="2">
        <span class="amount-text">{{ fenToYuan(formData.totalInvestorIncome || 0, 8) }}</span>
        <span class="currency-label ml-5px">{{ earningCurrency }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="项目方收益" :span="2">
        <span class="amount-text">{{ fenToYuan(formData.projectIncome || 0, 8) }}</span>
        <span class="currency-label ml-5px">{{ earningCurrency }}</span>
      </el-descriptions-item>
<!--      <el-descriptions-item label="投资人总收益率" :span="2">
        {{ formData.totalInvestorYield || 0 }} %
      </el-descriptions-item>-->
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { OperationApi, OperationVO } from '@/api/project/projectoperation'
import { fenToYuan } from '@/utils'

const visible = ref(false)
const loading = ref(false)
const investmentCurrency = ref('')
const earningCurrency = ref('')
const formData = ref<Partial<OperationVO>>({})

const open = async (projectId: number, investCurr: string = '', earnCurr: string = '') => {
  visible.value = true
  loading.value = true
  investmentCurrency.value = investCurr
  earningCurrency.value = earnCurr
  try {
    const data = await OperationApi.getOperation(projectId)
    if (data) {
      formData.value = data
    } else {
      formData.value = {}
    }
  } catch (e) {
    // 忽略错误，避免弹窗关闭
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.amount-text {
  font-weight: bold;
  font-family: 'Courier New', Courier, monospace;
}
.currency-label {
  color: #909399;
  font-size: 13px;
}
</style>
