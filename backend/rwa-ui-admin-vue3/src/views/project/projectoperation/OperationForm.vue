<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="参与投资人数" prop="investorCount">
        <el-input v-model="formData.investorCount" placeholder="请输入参与投资人数" />
      </el-form-item>
      <el-form-item label="申请分红人数" prop="dividendApplyCount">
        <el-input v-model="formData.dividendApplyCount" placeholder="请输入申请分红人数" />
      </el-form-item>
      <el-form-item label="申请分红金额(U)" prop="dividendApplyAmount">
        <el-input v-model="formData.dividendApplyAmount" placeholder="请输入申请分红金额(U)" />
      </el-form-item>
      <el-form-item label="提前赎回人数" prop="earlyRedemptionPeople">
        <el-input v-model="formData.earlyRedemptionPeople" placeholder="请输入提前赎回人数" />
      </el-form-item>
      <el-form-item label="提前赎回金额(U)" prop="earlyRedemptionAmount">
        <el-input v-model="formData.earlyRedemptionAmount" placeholder="请输入提前赎回金额(U)" />
      </el-form-item>
      <el-form-item label="提前赎回份额" prop="earlyRedemptionCount">
        <el-input v-model="formData.earlyRedemptionCount" placeholder="请输入提前赎回份额" />
      </el-form-item>
      <el-form-item label="到期赎回人数" prop="maturityRedemptionCount">
        <el-input v-model="formData.maturityRedemptionCount" placeholder="请输入到期赎回人数" />
      </el-form-item>
      <el-form-item label="到期赎回金额(U)" prop="maturityRedemptionAmount">
        <el-input v-model="formData.maturityRedemptionAmount" placeholder="请输入到期赎回金额(U)" />
      </el-form-item>
      <el-form-item label="投资人总收益(U)" prop="totalInvestorIncome">
        <el-input v-model="formData.totalInvestorIncome" placeholder="请输入投资人总收益(U)" />
      </el-form-item>
      <el-form-item label="投资人总收益率(%)" prop="totalInvestorYield">
        <el-input v-model="formData.totalInvestorYield" placeholder="请输入投资人总收益率(%)" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { OperationApi, OperationVO } from '@/api/project/projectoperation'

/** 项目运营统计表 表单 */
defineOptions({ name: 'OperationForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  projectId: undefined,
  investorCount: undefined,
  dividendApplyCount: undefined,
  dividendApplyAmount: undefined,
  earlyRedemptionPeople: undefined,
  earlyRedemptionAmount: undefined,
  earlyRedemptionCount: undefined,
  maturityRedemptionCount: undefined,
  maturityRedemptionAmount: undefined,
  totalInvestorIncome: undefined,
  totalInvestorYield: undefined
})
const formRules = reactive({
  investorCount: [{ required: true, message: '参与投资人数不能为空', trigger: 'blur' }],
  dividendApplyCount: [{ required: true, message: '申请分红人数不能为空', trigger: 'blur' }],
  dividendApplyAmount: [{ required: true, message: '申请分红金额(U)不能为空', trigger: 'blur' }],
  earlyRedemptionPeople: [{ required: true, message: '提前赎回人数不能为空', trigger: 'blur' }],
  earlyRedemptionAmount: [{ required: true, message: '提前赎回金额(U)不能为空', trigger: 'blur' }],
  maturityRedemptionCount: [{ required: true, message: '到期赎回人数不能为空', trigger: 'blur' }],
  maturityRedemptionAmount: [{ required: true, message: '到期赎回金额(U)不能为空', trigger: 'blur' }],
  totalInvestorIncome: [{ required: true, message: '投资人总收益(U)不能为空', trigger: 'blur' }],
  totalInvestorYield: [{ required: true, message: '投资人总收益率(%)不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await OperationApi.getOperation(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as OperationVO
    if (formType.value === 'create') {
      await OperationApi.createOperation(data)
      message.success(t('common.createSuccess'))
    } else {
      await OperationApi.updateOperation(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    projectId: undefined,
    investorCount: undefined,
    dividendApplyCount: undefined,
    dividendApplyAmount: undefined,
    earlyRedemptionPeople: undefined,
    earlyRedemptionAmount: undefined,
    earlyRedemptionCount: undefined,
    maturityRedemptionCount: undefined,
    maturityRedemptionAmount: undefined,
    totalInvestorIncome: undefined,
    totalInvestorYield: undefined
  }
  formRef.value?.resetFields()
}
</script>
