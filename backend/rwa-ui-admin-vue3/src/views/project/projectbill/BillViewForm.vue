<template>
  <Dialog title="查看账单" v-model="dialogVisible">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="流水号">{{ formData.billNo }}</el-descriptions-item>
      <el-descriptions-item label="账单类型">
        <dict-tag :type="DICT_TYPE.BIZ_BILL_TYPE" :value="formData.billType" />
      </el-descriptions-item>
      <el-descriptions-item label="申请时间">{{ formatDate(formData.applyTime) }}</el-descriptions-item>
      <el-descriptions-item label="申请用户">{{ formData.userName }}</el-descriptions-item>
      <el-descriptions-item label="所属项目">{{ formData.projectName }}</el-descriptions-item>
      <el-descriptions-item label="订单号">{{ formData.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="收款方">{{ formData.bankAccountName }}</el-descriptions-item>
      <el-descriptions-item label="收款账户">{{ formData.bankAccount }}</el-descriptions-item>
      <el-descriptions-item label="开户行">{{ formData.bankName }}</el-descriptions-item>
      <el-descriptions-item label="账单金额">{{ formData.billAmount }} {{ formData.billCoin || '元' }}</el-descriptions-item>
      
      <!-- 提前赎回显示赎回手续费率 -->
      <el-descriptions-item label="赎回手续费率" v-if="formData.billType === 3">
        {{ formData.commissionRate || 0 }}%
      </el-descriptions-item>

      <!-- 分红显示实际到账币种和汇率 -->
      <template v-if="formData.billType === 1">
        <el-descriptions-item label="实际到账币种">{{ formData.actualCoin || '-' }}</el-descriptions-item>
        <el-descriptions-item label="汇率">{{ formData.actualExchangeRate || '-' }}</el-descriptions-item>
      </template>

      <el-descriptions-item label="实际到账">
        {{ formData.actualAmount }} {{ formData.actualCoin}}
      </el-descriptions-item>
      <el-descriptions-item label="到账天数">{{ formData.arrivalDay || '-' }} 天</el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="formData.auditStatus" />
      </el-descriptions-item>
      <el-descriptions-item label="审核人">{{ formData.auditUserName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ formData.auditTime ? formatDate(formData.auditTime) : '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核备注" :span="2">{{ formData.auditRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="支付凭证" :span="2">
        <template v-if="formData.payVoucherUrl">
          <el-link type="primary" :underline="false" @click="downloadFile(formData.payVoucherUrl)">
            <Icon icon="ep:download" class="mr-1" /> 下载凭证
          </el-link>
        </template>
        <template v-else>-</template>
      </el-descriptions-item>
      <el-descriptions-item label="支付时间">{{ formData.payTime ? formatDate(formData.payTime) : '-' }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { BillApi } from '@/api/project/projectbill'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BillViewForm' })

const dialogVisible = ref(false)
const formData = ref<any>({})

const open = async (id: number) => {
  dialogVisible.value = true
  formData.value = await BillApi.getBill(id)
}

const downloadFile = (url: string) => {
  window.open(url, '_blank')
}

defineExpose({ open })
</script>
