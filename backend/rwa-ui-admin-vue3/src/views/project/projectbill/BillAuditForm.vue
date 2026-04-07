<template>
  <Dialog title="审核账单" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="formLoading"
    >
      <el-form-item label="流水号">
        <el-input v-model="formData.billNo" disabled />
      </el-form-item>
      <el-form-item label="账单类型">
        <dict-tag :type="DICT_TYPE.BIZ_BILL_TYPE" :value="formData.billType" />
      </el-form-item>
      <el-form-item label="申请时间">
        <el-input :value="formatDate(formData.applyTime)" disabled />
      </el-form-item>
      <el-form-item label="申请用户">
        <el-input v-model="formData.userName" disabled />
      </el-form-item>
      <el-form-item label="所属项目名称">
        <el-input v-model="formData.projectName" disabled />
      </el-form-item>
      <el-form-item label="订单号">
        <el-input v-model="formData.orderNo" disabled />
      </el-form-item>
      <el-form-item label="收款方">
        <el-input v-model="formData.bankAccountName" disabled />
      </el-form-item>
      <el-form-item label="收款账户">
        <el-input v-model="formData.bankAccount" disabled />
      </el-form-item>
      <el-form-item label="开户行">
        <el-input v-model="formData.bankName" disabled />
      </el-form-item>
      <el-form-item label="账单金额">
        <el-input v-model="formData.billAmount" disabled>
          <template #append v-if="formData.billCoin">{{ formData.billCoin }}</template>
        </el-input>
      </el-form-item>
      
      <el-divider content-position="left">审核信息</el-divider>
      
      <!-- 提前赎回显示赎回手续费率 -->
      <el-form-item label="赎回手续费率" v-if="formData.billType === 3">
        <el-input v-model="formData.commissionRate" disabled>
          <template #append>%</template>
        </el-input>
      </el-form-item>

      <!-- 分红显示实际到账币种和汇率 -->
<!--      <template v-if="formData.billType === 1">
        <el-form-item label="实际到账币种" prop="actualCoin">
          <el-select v-model="formData.actualCoin" placeholder="请选择实际到账币种" class="!w-full">
            <el-option
              v-for="coin in fiatCoinList"
              :key="coin.id"
              :label="coin.name"
              :value="coin.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="汇率" prop="actualExchangeRate">
          <el-input-number v-model="formData.actualExchangeRate" :precision="6" :min="0" class="!w-full" />
        </el-form-item>
      </template>-->

      <el-form-item label="实际到账金额" prop="actualAmount">
        <el-input-number v-model="formData.actualAmount" :precision="2" :min="0" class="!w-full" />
        <div class="ml-2 text-gray-500" v-if="formData.actualCoin">单位: {{ formData.actualCoin }}</div>
      </el-form-item>
      <el-form-item label="到账天数" prop="arrivalDays">
        <el-input-number v-model="formData.arrivalDays" :min="0" :max="365" class="!w-200px" />
        <span class="ml-10px text-gray-500">天</span>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input 
          v-model="formData.auditRemark" 
          type="textarea" 
          placeholder="请输入审核备注（审核不通过必填）" 
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleAudit(true)" type="success" :disabled="formLoading">审核通过</el-button>
      <el-button @click="handleAudit(false)" type="danger" :disabled="formLoading">审核不通过</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { BillApi } from '@/api/project/projectbill'
import { SystemCoinApi, SystemCoinSimpleVO } from '@/api/project/systemcoin'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BillAuditForm' })

const message = useMessage()

const dialogVisible = ref(false)
const formLoading = ref(false)
const formData = ref<any>({})
const fiatCoinList = ref<SystemCoinSimpleVO[]>([])

const formRules = reactive({
  actualAmount: [{ required: true, message: '实际到账金额不能为空', trigger: 'blur' }]
})
const formRef = ref()

const open = async (id: number) => {
  dialogVisible.value = true
  formLoading.value = true
  try {
    const [billData, fiatCoins] = await Promise.all([
      BillApi.getBill(id),
      SystemCoinApi.getFiatCoinList()
    ])
    formData.value = billData
    formData.value.arrivalDays = formData.value.arrivalDays || 0
    fiatCoinList.value = fiatCoins
  } finally {
    formLoading.value = false
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])

const handleAudit = async (approved: boolean) => {
  if (!approved && !formData.value.auditRemark) {
    message.error('审核不通过时必须填写审核备注')
    return
  }
  
  // 校验表单
  if (approved) {
    const valid = await formRef.value.validate()
    if (!valid) return
  }

  formLoading.value = true
  try {
    await BillApi.auditBill({
      id: formData.value.id,
      approved: approved,
      auditRemark: formData.value.auditRemark,
      actualAmount: formData.value.actualAmount,
      arrivalDays: formData.value.arrivalDays,
      actualCoin: formData.value.actualCoin,
      actualExchangeRate: formData.value.actualExchangeRate
    })
    message.success('操作成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
