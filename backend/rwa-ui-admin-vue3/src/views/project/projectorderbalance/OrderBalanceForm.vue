<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="项目ID" prop="projectId">
        <el-input v-model="formData.projectId" placeholder="请输入项目ID" />
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
      </el-form-item>
      <el-form-item label="本金金额(U)" prop="principalAmount">
        <el-input v-model="formData.principalAmount" placeholder="请输入本金金额(U)" />
      </el-form-item>
      <el-form-item label="持有金额" prop="holdAmount">
        <el-input v-model="formData.holdAmount" placeholder="请输入持有金额" />
      </el-form-item>
      <el-form-item label="购买份额" prop="buyQuantity">
        <el-input v-model="formData.buyQuantity" placeholder="请输入购买份额" />
      </el-form-item>
      <el-form-item label="当前持有份额(份)（赎回后扣减）" prop="holdQuantity">
        <el-input v-model="formData.holdQuantity" placeholder="请输入当前持有份额(份)（赎回后扣减）" />
      </el-form-item>
      <el-form-item label="累计总收益(U)（含未提取）" prop="totalIncome">
        <el-input v-model="formData.totalIncome" placeholder="请输入累计总收益(U)（含未提取）" />
      </el-form-item>
      <el-form-item label="已提取分红(U)" prop="withdrawnDividend">
        <el-input v-model="formData.withdrawnDividend" placeholder="请输入已提取分红(U)" />
      </el-form-item>
      <el-form-item label="冻结的分红(U)" prop="freezeDividend">
        <el-input v-model="formData.freezeDividend" placeholder="请输入冻结的分红(U)" />
      </el-form-item>
      <el-form-item label="未提取分红(U)（=总收益-已提取）" prop="unwithdrawnDividend">
        <el-input v-model="formData.unwithdrawnDividend" placeholder="请输入未提取分红(U)（=总收益-已提取）" />
      </el-form-item>
      <el-form-item label="累计赎回本金(U)（赎回时累加）" prop="totalRedemptionAmount">
        <el-input v-model="formData.totalRedemptionAmount" placeholder="请输入累计赎回本金(U)（赎回时累加）" />
      </el-form-item>
      <el-form-item label="最后一次收益计算时间" prop="lastIncomeCalcTime">
        <el-date-picker
          v-model="formData.lastIncomeCalcTime"
          type="date"
          value-format="x"
          placeholder="选择最后一次收益计算时间"
        />
      </el-form-item>
      <el-form-item label="最后一次分红提取时间" prop="lastWithdrawTime">
        <el-date-picker
          v-model="formData.lastWithdrawTime"
          type="date"
          value-format="x"
          placeholder="选择最后一次分红提取时间"
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
import { OrderBalanceApi, OrderBalanceVO } from '@/api/project/projectorderbalance'

/** 用户项目余额表 表单 */
defineOptions({ name: 'OrderBalanceForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userId: undefined,
  projectId: undefined,
  orderId: undefined,
  principalAmount: undefined,
  holdAmount: undefined,
  buyQuantity: undefined,
  holdQuantity: undefined,
  totalIncome: undefined,
  withdrawnDividend: undefined,
  freezeDividend: undefined,
  unwithdrawnDividend: undefined,
  totalRedemptionAmount: undefined,
  lastIncomeCalcTime: undefined,
  lastWithdrawTime: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  orderId: [{ required: true, message: '订单ID不能为空', trigger: 'blur' }],
  principalAmount: [{ required: true, message: '本金金额(U)不能为空', trigger: 'blur' }],
  holdQuantity: [{ required: true, message: '当前持有份额(份)（赎回后扣减）不能为空', trigger: 'blur' }],
  totalIncome: [{ required: true, message: '累计总收益(U)（含未提取）不能为空', trigger: 'blur' }],
  withdrawnDividend: [{ required: true, message: '已提取分红(U)不能为空', trigger: 'blur' }],
  unwithdrawnDividend: [{ required: true, message: '未提取分红(U)（=总收益-已提取）不能为空', trigger: 'blur' }],
  totalRedemptionAmount: [{ required: true, message: '累计赎回本金(U)（赎回时累加）不能为空', trigger: 'blur' }]
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
      formData.value = await OrderBalanceApi.getOrderBalance(id)
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
    const data = formData.value as unknown as OrderBalanceVO
    if (formType.value === 'create') {
      await OrderBalanceApi.createOrderBalance(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderBalanceApi.updateOrderBalance(data)
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
    id: undefined,
    userId: undefined,
    projectId: undefined,
    orderId: undefined,
    principalAmount: undefined,
    holdAmount: undefined,
    buyQuantity: undefined,
    holdQuantity: undefined,
    totalIncome: undefined,
    withdrawnDividend: undefined,
    freezeDividend: undefined,
    unwithdrawnDividend: undefined,
    totalRedemptionAmount: undefined,
    lastIncomeCalcTime: undefined,
    lastWithdrawTime: undefined
  }
  formRef.value?.resetFields()
}
</script>
