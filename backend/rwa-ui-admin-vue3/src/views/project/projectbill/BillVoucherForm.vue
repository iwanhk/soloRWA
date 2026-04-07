<template>
  <Dialog title="上传支付凭证" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-form-item label="流水号">
        <el-input v-model="formData.billNo" disabled />
      </el-form-item>
      <el-form-item label="账单金额">
        <el-input v-model="formData.billAmount" disabled>
          <template #append v-if="formData.billCoin">{{ formData.billCoin }}</template>
        </el-input>
      </el-form-item>
      <el-form-item label="实际到账" prop="actualAmount">
        <el-input-number v-model="formData.actualAmount" :precision="2" :min="0" class="!w-full" />
        <div class="ml-2 text-gray-500" v-if="formData.actualCoin">单位: {{ formData.actualCoin }}</div>
      </el-form-item>
      <template v-if="formData.billType === 1">
        <el-form-item label="实际到账币种" prop="actualCoin">
          <el-select v-model="formData.actualCoin" placeholder="请选择实际到账币种" class="!w-full">
            <el-option
              v-for="coin in fiatCoinList"
              :key="coin.id"
              :label="`${coin.coinName} (${coin.coinCode})`"
              :value="coin.coinCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="汇率" prop="actualExchangeRate">
          <el-input-number v-model="formData.actualExchangeRate" :precision="6" :min="0" class="!w-full" />
        </el-form-item>
      </template>
      <el-form-item label="支付凭证" prop="payVoucherUrl">
        <UploadFile 
          v-model="formData.payVoucherUrl" 
          :limit="1" 
          :file-type="['jpg', 'png', 'jpeg', 'pdf', 'doc', 'docx']" 
        />
      </el-form-item>
      <el-form-item label="支付时间" prop="payTime">
        <el-date-picker
          v-model="formData.payTime"
          type="datetime"
          value-format="x"
          placeholder="选择支付时间"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { BillApi } from '@/api/project/projectbill'
import { UploadFile } from '@/components/UploadFile'
import {SystemCoinApi, SystemCoinSimpleVO} from "@/api/project/systemcoin";

defineOptions({ name: 'BillVoucherForm' })

const message = useMessage()
const fiatCoinList = ref<SystemCoinSimpleVO[]>([])
const dialogVisible = ref(false)
const formLoading = ref(false)
const formData = ref<any>({})
const formRules = reactive({
  payVoucherUrl: [{ required: true, message: '请上传支付凭证', trigger: 'blur' }],
  payTime: [{ required: true, message: '请选择支付时间', trigger: 'blur' }]
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
    fiatCoinList.value = fiatCoins
  } finally {
    formLoading.value = false
  }

}
defineExpose({ open })

const emit = defineEmits(['success'])

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    await BillApi.uploadVoucher({
      id: formData.value.id,
      payVoucherUrl: formData.value.payVoucherUrl,
      payTime: formData.value.payTime,
      actualAmount: formData.value.actualAmount,
      actualCoin: formData.value.actualCoin,
      actualExchangeRate: formData.value.actualExchangeRate
    })
    message.success('上传成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}
</script>
