<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="formData.orderNo" placeholder="请输入订单号" disabled />
      </el-form-item>
      <el-form-item label="申请日期" prop="applyDate">
        <el-date-picker
          v-model="formData.applyDate"
          type="datetime"
          value-format="x"
          placeholder="选择申请日期"
          disabled
        />
      </el-form-item>
<!--      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入用户ID" disabled />
      </el-form-item>-->
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="formData.orderStatus" placeholder="请选择订单状态" disabled>
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_ORDER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="用户" prop="userName">
        <el-input v-model="formData.userName"  disabled />
      </el-form-item>
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="formData.projectName" placeholder="请输入项目名称" disabled />
      </el-form-item>
      <el-form-item label="申购份额" prop="subscribeQuantity">
        <el-input v-model="formData.subscribeQuantity" placeholder="请输入申购份额" disabled />
      </el-form-item>
      <el-form-item :label="`单价${formData.investmentCurrency ? '(' + formData.investmentCurrency + ')' : ''}`" prop="price">
        <el-input v-model="formData.price" placeholder="请输入单价" disabled />
      </el-form-item>
      <el-form-item :label="`总价${formData.investmentCurrency ? '(' + formData.investmentCurrency + ')' : ''}`" prop="totalAmount">
        <el-input v-model="formData.totalAmount" placeholder="请输入总价" disabled />
      </el-form-item>
<!--      <el-form-item label="购买确认时间" prop="confirmPurchaseTime">
        <el-date-picker
          v-model="formData.confirmPurchaseTime"
          type="datetime"
          value-format="x"
          placeholder="选择购买确认时间"
          disabled
        />
      </el-form-item>-->
      <el-form-item label="支付方式" prop="payType">

        <el-select v-model="formData.payType" placeholder="请选择支付方式" disabled>
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.BIZ_PAY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="链地址" prop="chainAddress">
        <el-input v-model="formData.chainAddress" placeholder="请输入链地址" disabled />
      </el-form-item>-->
      <el-form-item label="合同号" prop="contractNo">
        <el-input v-model="formData.contractNo" placeholder="请输入合同号" disabled />
      </el-form-item>
      <el-form-item label="支付凭证" prop="payVoucherUrl">
        <UploadFile v-model="formData.payVoucherUrl" disabled />
      </el-form-item>
<!--      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker
          v-model="formData.auditTime"
          type="date"
          value-format="x"
          placeholder="选择审核时间"
          disabled
        />
      </el-form-item>-->
<!--      <el-form-item label="审核人ID" prop="auditUserId">
        <el-input v-model="formData.auditUserId" placeholder="请输入审核人ID" disabled />
      </el-form-item>
      <el-form-item label="审核人名称" prop="auditUserName">
        <el-input v-model="formData.auditUserName" placeholder="请输入审核人名称" disabled />
      </el-form-item>-->
<!--      <el-form-item label="链验证状态" prop="chainStatus">
        <el-radio-group v-model="formData.chainStatus" disabled>
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>-->
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="formData.auditRemark"  :disabled="formType !== 'audit'"/>
      </el-form-item>
      <el-form-item label="合同附件" prop="contractFileUrls" v-if="formType === 'audit'">
        <UploadFileDTO
          v-model="formData.contractFileUrls"
          :limit="10"
          :file-type="['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'zip', 'rar']"
        />
      </el-form-item>
<!--      <el-form-item label="取消时间" prop="cancelTime">
        <el-date-picker
          v-model="formData.cancelTime"
          type="date"
          value-format="x"
          placeholder="选择取消时间"
          disabled
        />
      </el-form-item>
      <el-form-item label="取消原因" prop="cancelReason">
        <el-input v-model="formData.cancelReason" placeholder="请输入取消原因" disabled />
      </el-form-item>
      <el-form-item label="订单过期时间（待支付订单超时时间，如创建后24小时）" prop="expireTime">
        <el-date-picker
          v-model="formData.expireTime"
          type="date"
          value-format="x"
          placeholder="选择订单过期时间（待支付订单超时时间，如创建后24小时）"
          disabled
        />
      </el-form-item>-->
    </el-form>
    <template #footer>
      <div v-if="formType === 'audit'">
        <el-button @click="handleAuditPass" type="success" :disabled="formLoading">审核通过</el-button>
        <el-button @click="handleAuditReject" type="danger" :disabled="formLoading">审核不通过</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
      <div v-else>
        <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import {getIntDictOptions, DICT_TYPE, getStrDictOptions} from '@/utils/dict'
import { OrderApi, OrderVO } from '@/api/project/projectorder'
import UploadFileDTO from '@/components/UploadFile/src/UploadFileDTO.vue'

/** 项目认购订单 表单 */
defineOptions({ name: 'OrderForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；audit - 审核
const formData = ref({
  id: undefined,
  orderNo: undefined,
  applyDate: undefined,
  userId: undefined,
  orderStatus: undefined,
  projectId: undefined,
  projectName: undefined,
  subscribeQuantity: undefined,
  price: undefined,
  totalAmount: undefined,
  confirmPurchaseTime: undefined,
  payType: undefined,
  chainAddress: undefined,
  contractNo: undefined,
  payVoucherUrl: undefined,
  auditTime: undefined,
  auditUserId: undefined,
  auditUserName: undefined,
  chainStatus: undefined,
  auditRemark: undefined,
  cancelTime: undefined,
  cancelReason: undefined,
  expireTime: undefined,
  userName: undefined,
  contractFileUrls: [] as string[],
  investmentCurrency: undefined
})
const formRules = reactive({
  orderNo: [{ required: true, message: '订单号不能为空', trigger: 'blur' }],
  applyDate: [{ required: true, message: '申请日期不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
  orderStatus: [{ required: true, message: '订单状态不能为空', trigger: 'change' }],
  projectId: [{ required: true, message: '项目ID不能为空', trigger: 'blur' }],
  projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  subscribeQuantity: [{ required: true, message: '申购份额(份)不能为空', trigger: 'blur' }],
  totalAmount: [{ required: true, message: '总价不能为空', trigger: 'blur' }],
  payType: [{ required: true, message: '支付方式不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  if (type === 'audit') {
    dialogTitle.value = '审核订单'
  } else {
    dialogTitle.value = t('action.' + type)
  }
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await OrderApi.getOrder(id)
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
    const data = formData.value as unknown as OrderVO
    if (formType.value === 'create') {
      await OrderApi.createOrder(data)
      message.success(t('common.createSuccess'))
    } else {
      await OrderApi.updateOrder(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 审核通过 */
const handleAuditPass = async () => {
  await handleAudit(true)
}

/** 审核不通过 */
const handleAuditReject = async () => {
  if (!formData.value.auditRemark) {
    message.error('审核不通过时必须填写审核备注')
    return
  }
  await handleAudit(false)
}

/** 执行审核 */
const handleAudit = async (approved: boolean) => {
  formLoading.value = true
  try {
    // 转换合同附件URL为JSON字符串
    let contractFileUrls: string | string[] = formData.value.contractFileUrls
    if (Array.isArray(contractFileUrls)) {
      contractFileUrls = JSON.stringify(contractFileUrls)
    }
    
    await OrderApi.auditOrder({
      id: formData.value.id,
      approved,
      auditRemark: formData.value.auditRemark,
      contractFileUrls: contractFileUrls as string
    })
    message.success('操作成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    orderNo: undefined,
    applyDate: undefined,
    userId: undefined,
    orderStatus: undefined,
    projectId: undefined,
    projectName: undefined,
    subscribeQuantity: undefined,
    price: undefined,
    totalAmount: undefined,
    confirmPurchaseTime: undefined,
    payType: undefined,
    chainAddress: undefined,
    contractNo: undefined,
    payVoucherUrl: undefined,
    auditTime: undefined,
    auditUserId: undefined,
    auditUserName: undefined,
    chainStatus: undefined,
    auditRemark: undefined,
    cancelTime: undefined,
    cancelReason: undefined,
    expireTime: undefined,
    userName: undefined,
    contractFileUrls: [],
    investmentCurrency: undefined
  }
  formRef.value?.resetFields()
}
</script>
