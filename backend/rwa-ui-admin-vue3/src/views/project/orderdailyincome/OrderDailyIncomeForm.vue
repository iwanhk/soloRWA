<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="订单id" prop="orderId">
        <el-input v-model="formData.orderId" placeholder="请输入订单id" />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="formData.orderNo" placeholder="请输入订单号" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入用户ID" />
      </el-form-item>
      <el-form-item label="项目ID" prop="projectId">
        <el-input v-model="formData.projectId" placeholder="请输入项目ID" />
      </el-form-item>
      <el-form-item label="收益日期" prop="incomeDate">
        <el-date-picker
          v-model="formData.incomeDate"
          type="date"
          value-format="x"
          placeholder="选择收益日期"
        />
      </el-form-item>
      <el-form-item label="收益id" prop="projectRevenueId">
        <el-input v-model="formData.projectRevenueId" placeholder="请输入收益id" />
      </el-form-item>
      <el-form-item label="当日持有份额" prop="holdQuantity">
        <el-input v-model="formData.holdQuantity" placeholder="请输入当日持有份额" />
      </el-form-item>
      <el-form-item label="收益发放时间" prop="issueTime">
        <el-date-picker
          v-model="formData.issueTime"
          type="date"
          value-format="x"
          placeholder="选择收益发放时间"
        />
      </el-form-item>
      <el-form-item label="当日收益" prop="dailyIncome">
        <el-input v-model="formData.dailyIncome" placeholder="请输入当日收益" />
      </el-form-item>
      <el-form-item label="当日收益率" prop="incomeRate">
        <el-input v-model="formData.incomeRate" placeholder="请输入当日收益率" />
      </el-form-item>
      <el-form-item label="累计收益" prop="cumulativeIncome">
        <el-input v-model="formData.cumulativeIncome" placeholder="请输入累计收益" />
      </el-form-item>
      <el-form-item label="收益状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择收益状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_INCOME_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { OrderDailyIncomeApi, OrderDailyIncomeVO } from '@/api/project/orderdailyincome'

/** 订单每日收益统计 表单 */
defineOptions({ name: 'OrderDailyIncomeForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  orderId: undefined,
  orderNo: undefined,
  userId: undefined,
  projectId: undefined,
  incomeDate: undefined,
  projectRevenueId: undefined,
  holdQuantity: undefined,
  issueTime: undefined,
  dailyIncome: undefined,
  incomeRate: undefined,
  cumulativeIncome: undefined,
  status: undefined
})
const formRules = reactive({
  orderId: [{ required: true, message: '订单id不能为空', trigger: 'blur' }],
  orderNo: [{ required: true, message: '订单号不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  projectId: [{ required: true, message: '项目ID不能为空', trigger: 'blur' }],
  incomeDate: [{ required: true, message: '收益日期不能为空', trigger: 'blur' }],
  holdQuantity: [{ required: true, message: '当日持有份额不能为空', trigger: 'blur' }],
  dailyIncome: [{ required: true, message: '当日收益不能为空', trigger: 'blur' }],
  incomeRate: [{ required: true, message: '当日收益率不能为空', trigger: 'blur' }],
  cumulativeIncome: [{ required: true, message: '累计收益不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '收益状态不能为空', trigger: 'change' }]
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
      formData.value = await OrderDailyIncomeApi.getOrderDailyIncome(id)
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
    const data = formData.value as unknown as OrderDailyIncomeVO
    if (formType.value === 'create') {
      await OrderDailyIncomeApi.createOrderDailyIncome(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderDailyIncomeApi.updateOrderDailyIncome(data)
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
    orderId: undefined,
    orderNo: undefined,
    userId: undefined,
    projectId: undefined,
    incomeDate: undefined,
    projectRevenueId: undefined,
    holdQuantity: undefined,
    issueTime: undefined,
    dailyIncome: undefined,
    incomeRate: undefined,
    cumulativeIncome: undefined,
    status: undefined
  }
  formRef.value?.resetFields()
}
</script>