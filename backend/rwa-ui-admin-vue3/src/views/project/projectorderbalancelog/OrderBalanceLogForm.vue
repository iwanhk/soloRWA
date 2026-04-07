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
      <el-form-item label="操作金额" prop="amount">
        <el-input v-model="formData.amount" placeholder="请输入操作金额" />
      </el-form-item>
      <el-form-item label="操作后金额" prop="afterAmount">
        <el-input v-model="formData.afterAmount" placeholder="请输入操作后金额" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_BALANCE_TYPE)"
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
import { OrderBalanceLogApi, OrderBalanceLogVO } from '@/api/project/projectorderbalancelog'

/** 用户项目余额记录 表单 */
defineOptions({ name: 'OrderBalanceLogForm' })

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
  amount: undefined,
  afterAmount: undefined,
  type: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  orderId: [{ required: true, message: '订单ID不能为空', trigger: 'blur' }],
  afterAmount: [{ required: true, message: '操作后金额不能为空', trigger: 'blur' }]
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
      formData.value = await OrderBalanceLogApi.getOrderBalanceLog(id)
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
    const data = formData.value as unknown as OrderBalanceLogVO
    if (formType.value === 'create') {
      await OrderBalanceLogApi.createOrderBalanceLog(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderBalanceLogApi.updateOrderBalanceLog(data)
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
    amount: undefined,
    afterAmount: undefined,
    type: undefined
  }
  formRef.value?.resetFields()
}
</script>