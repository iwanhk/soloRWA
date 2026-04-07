<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="关联用户ID">{{ formData.userId }}</el-descriptions-item>
      <el-descriptions-item label="开户名">{{ formData.bankAccountName }}</el-descriptions-item>
      <el-descriptions-item label="银行卡号">{{ formData.bankAccount }}</el-descriptions-item>
      <el-descriptions-item label="开户行">{{ formData.bankName }}</el-descriptions-item>
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
import { BankApi, BankVO } from '@/api/user/userbank'

defineOptions({ name: 'BankDetail' })

const dialogVisible = ref(false)
const dialogTitle = ref('银行卡详情')
const formData = ref({} as BankVO)

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  formData.value = {} as BankVO // 重置
  if (id) {
    formData.value = await BankApi.getBank(id)
  }
}

defineExpose({ open })
</script>
