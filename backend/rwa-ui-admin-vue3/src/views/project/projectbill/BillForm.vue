<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="formLoading"
    >
      <el-form-item label="流水号" prop="billNo">
        <el-input v-model="formData.billNo" placeholder="请输入流水号" disabled />
      </el-form-item>
      <el-form-item label="账单类型" prop="billType">
        <el-select v-model="formData.billType" placeholder="请选择账单类型" disabled>
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_BILL_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间" prop="applyTime">
        <el-date-picker
          v-model="formData.applyTime"
          type="date"
          value-format="x"
          placeholder="选择申请时间"
          disabled
        />
      </el-form-item>
      <el-form-item label="申请用户" prop="userName">
        <el-input v-model="formData.userName" disabled />
      </el-form-item>
<!--      <el-form-item label="项目ID" prop="projectId">
        <el-input v-model="formData.projectId" placeholder="请输入项目ID" disabled />
      </el-form-item>-->
      <el-form-item label="所属项目名称" prop="projectName">
        <el-input v-model="formData.projectName" placeholder="请输入所属项目名称" disabled />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="formData.orderNo" disabled />
      </el-form-item>
      <el-form-item label="收款方" prop="bankAccount">
        <el-input v-model="formData.bankAccountName" placeholder="请输入收款方" disabled />
      </el-form-item>
      <el-form-item label="收款账户" prop="bankAccount">
        <el-input v-model="formData.bankAccount" placeholder="请输入收款账户" disabled />
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input v-model="formData.bankName" placeholder="请输入开户行" disabled />
      </el-form-item>
      <el-form-item label="账单金额（元）" prop="billAmount">
        <el-input v-model="formData.billAmount" placeholder="请输入账单金额（元）" disabled />
      </el-form-item>
      
      <el-form-item label="实际到账金额（元）" prop="actualAmount" >
        <el-input v-model="formData.actualAmount" placeholder="请输入实际到账金额（元）" :disabled="formType !== 'audit'"/>
      </el-form-item>

<!--      <el-form-item label="审核状态" prop="auditStatus" >
        <el-select v-model="formData.auditStatus" placeholder="请选择审核状态" disabled>
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->

      <el-form-item label="支付凭证" prop="payVoucherUrl" :disabled="formType !== 'audit'">
        <UploadFile v-model="formData.payVoucherUrl" :limit="1" :file-type="['jpg', 'png', 'jpeg', 'pdf', 'doc', 'docx']" />
      </el-form-item>

      <el-form-item label="支付时间" prop="payTime" :disabled="formType !== 'audit'">
        <el-date-picker
          v-model="formData.payTime"
          type="date"
          value-format="x"
          placeholder="选择支付时间"
        />
      </el-form-item>
      
      <el-form-item label="审核备注" prop="auditRemark" :disabled="formType !== 'audit'">
        <el-input 
          v-model="formData.auditRemark" 
          type="textarea" 
          placeholder="请输入审核备注（审核不通过原因）" 
        />
      </el-form-item>

<!--      <template v-if="formType !== 'audit' && formType !== 'create'">
&lt;!&ndash;        <el-form-item label="审核人ID" prop="auditUserId">
          <el-input v-model="formData.auditUserId" placeholder="请输入审核人ID" />
        </el-form-item>&ndash;&gt;
        <el-form-item label="审核人名称" prop="auditUserName">
          <el-input v-model="formData.auditUserName" placeholder="请输入审核人名称" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker
            v-model="formData.auditTime"
            type="date"
            value-format="x"
            placeholder="选择审核时间"
          />
        </el-form-item>
        <el-form-item label="支付凭证" prop="payVoucherUrl">
          <UploadFile v-model="formData.payVoucherUrl" :limit="1" :file-type="['jpg', 'png', 'jpeg', 'pdf', 'doc', 'docx']" />
        </el-form-item>
        <el-form-item label="支付时间" prop="payTime">
          <el-date-picker
            v-model="formData.payTime"
            type="date"
            value-format="x"
            placeholder="选择支付时间"
          />
        </el-form-item>
      </template>-->
    </el-form>
    <template #footer>
      <div v-if="formType === 'audit'">
        <el-button @click="handleAudit(true)" type="success" :disabled="formLoading">审核通过</el-button>
        <el-button @click="handleAudit(false)" type="danger" :disabled="formLoading">审核不通过</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
<!--      <div v-else>
        <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>-->
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { BillApi, BillVO } from '@/api/project/projectbill'

import { UploadFile } from '@/components/UploadFile'

/** 项目账单管理表 表单 */
defineOptions({ name: 'BillForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；audit - 审核
const formData = ref({
  id: undefined,
  billNo: undefined,
  billType: undefined,
  applyTime: undefined,
  userId: undefined,
  projectId: undefined,
  projectName: undefined,
  payee: undefined,
  bankAccount: undefined,
  bankName: undefined,
  billAmount: undefined,
  actualAmount: undefined,
  auditStatus: undefined,
  auditUserId: undefined,
  auditUserName: undefined,
  auditTime: undefined,
  auditRemark: undefined,
  payVoucherUrl: undefined,
  payTime: undefined,
  userName: undefined,
  orderNo: undefined,
})
const formRules = reactive({
  billNo: [{ required: true, message: '流水号不能为空', trigger: 'blur' }],
  billType: [{ required: true, message: '账单类型不能为空', trigger: 'change' }],
  applyTime: [{ required: true, message: '申请时间不能为空', trigger: 'blur' }],
  userId: [{ required: true, message: '申请用户ID不能为空', trigger: 'blur' }],
  projectId: [{ required: true, message: '项目ID不能为空', trigger: 'blur' }],
  projectName: [{ required: true, message: '所属项目名称不能为空', trigger: 'blur' }],
  payee: [{ required: true, message: '收款方不能为空', trigger: 'blur' }],
  bankAccount: [{ required: true, message: '收款账户不能为空', trigger: 'blur' }],
  bankName: [{ required: true, message: '开户行不能为空', trigger: 'blur' }],
  billAmount: [{ required: true, message: '账单金额不能为空', trigger: 'blur' }],
  // auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'audit' ? '审核账单' : t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await BillApi.getBill(id)
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
    const data = formData.value as unknown as BillVO
    if (formType.value === 'create') {
      await BillApi.createBill(data)
      message.success(t('common.createSuccess'))
    } else {
      await BillApi.updateBill(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 审核 */
const handleAudit = async (approved: boolean) => {
  if (!approved && !formData.value.auditRemark) {
    message.error('审核不通过时必须填写审核备注')
    return
  }

  
  formLoading.value = true
  try {
    await BillApi.auditBill({
      id: formData.value.id,
      approved: approved,
      auditRemark: formData.value.auditRemark,
      actualAmount: formData.value.actualAmount,
      payVoucherUrl: formData.value.payVoucherUrl,
      payTime: formData.value.payTime,
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
    billNo: undefined,
    billType: undefined,
    applyTime: undefined,
    userId: undefined,
    projectId: undefined,
    projectName: undefined,
    payee: undefined,
    bankAccount: undefined,
    bankName: undefined,
    billAmount: undefined,
    actualAmount: undefined,
    auditStatus: undefined,
    auditUserId: undefined,
    auditUserName: undefined,
    auditTime: undefined,
    auditRemark: undefined,
    payVoucherUrl: undefined,
    payTime: undefined
  }
  formRef.value?.resetFields()
}
</script>
