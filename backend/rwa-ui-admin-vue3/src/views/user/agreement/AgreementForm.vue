<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
<!--      <el-form-item label="协议类型" prop="agreementType">
        <el-select v-model="formData.agreementType" placeholder="请选择协议类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AGREEMENT_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
      <el-form-item label="协议标题" prop="agreementTitle">
        <el-input v-model="formData.agreementTitle" placeholder="请输入协议标题" />
      </el-form-item>
      <el-form-item label="协议键" prop="agreementKey">
        <el-input v-model="formData.agreementKey" placeholder="请输入协议键" />
      </el-form-item>
      <el-form-item label="协议内容" prop="agreementContent">
        <Editor v-model="formData.agreementContent" height="150px" />
      </el-form-item>
<!--      <el-form-item label="协议版本号" prop="version">
        <el-input v-model="formData.version" placeholder="请输入协议版本号" />
      </el-form-item>
      <el-form-item label="是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）" prop="isCurrent">
        <el-radio-group v-model="formData.isCurrent">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生效时间" prop="effectiveTime">
        <el-date-picker
          v-model="formData.effectiveTime"
          type="date"
          value-format="x"
          placeholder="选择生效时间"
        />
      </el-form-item>
      <el-form-item label="过期时间" prop="expireTime">
        <el-date-picker
          v-model="formData.expireTime"
          type="date"
          value-format="x"
          placeholder="选择过期时间"
        />
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { AgreementApi, AgreementVO } from '@/api/user/agreement'

/** 系统协议表 表单 */
defineOptions({ name: 'AgreementForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  agreementType: undefined,
  agreementKey: undefined,
  agreementTitle: undefined,
  agreementContent: undefined,
  version: undefined,
  isCurrent: undefined,
  effectiveTime: undefined,
  expireTime: undefined
})
const formRules = reactive({
  agreementTitle: [{ required: true, message: '协议标题不能为空', trigger: 'blur' }],
  agreementContent: [{ required: true, message: '协议内容不能为空', trigger: 'blur' }],

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
      formData.value = await AgreementApi.getAgreement(id)
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
    const data = formData.value as unknown as AgreementVO
    if (formType.value === 'create') {
      await AgreementApi.createAgreement(data)
      message.success(t('common.createSuccess'))
    } else {
      await AgreementApi.updateAgreement(data)
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
    agreementType: undefined,
    agreementTitle: undefined,
    agreementKey: undefined,
    agreementContent: undefined,
    version: undefined,
    isCurrent: undefined,
    effectiveTime: undefined,
    expireTime: undefined
  }
  formRef.value?.resetFields()
}
</script>
