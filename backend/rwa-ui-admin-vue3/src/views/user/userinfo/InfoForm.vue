<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
      :disabled="formType === 'view'"
    >
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="formData.mobile" placeholder="请输入手机号" />
      </el-form-item>
<!--      <el-form-item label="密码" prop="password">
        <el-input v-model="formData.password" placeholder="请输入密码" />
      </el-form-item>-->
      <el-form-item label="用户姓名" prop="realName">
        <el-input v-model="formData.realName" placeholder="请输入用户姓名" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
      </el-form-item>
      <el-form-item label="身份证有效期" prop="idCardExpire">
        <el-input v-model="formData.idCardExpire" placeholder="请输入身份证有效期" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="formData.auditStatus" placeholder="请选择审核状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.USER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
<!--      <el-form-item label="币种" prop="defaultCurrency">
        <el-input v-model="formData.defaultCurrency" placeholder="请输入币种" />
      </el-form-item>-->
      <el-form-item label="2FA" prop="twoFactorAuthStatus">

          <el-select v-model="formData.twoFactorAuthStatus" >
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.F2A_STATUS)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
       
      </el-form-item>
<!--      <el-form-item label="2FA验证密钥（Base32格式，开启2FA时生成）" prop="twoFactorAuthSecret">
        <el-input v-model="formData.twoFactorAuthSecret" placeholder="请输入2FA验证密钥（Base32格式，开启2FA时生成）" />
      </el-form-item>
      <el-form-item label="2FA验证绑定时间" prop="twoFactorAuthBindTime">
        <el-date-picker
          v-model="formData.twoFactorAuthBindTime"
          type="date"
          value-format="x"
          placeholder="选择2FA验证绑定时间"
        />
      </el-form-item>
      <el-form-item label="2FA验证最后验证时间" prop="twoFactorAuthLastVerifyTime">
        <el-date-picker
          v-model="formData.twoFactorAuthLastVerifyTime"
          type="date"
          value-format="x"
          placeholder="选择2FA验证最后验证时间"
        />
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading" v-if="formType !== 'view'">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { InfoApi, InfoVO } from '@/api/user/userinfo'

/** 用户基础信息 表单 */
defineOptions({ name: 'InfoForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  mobile: undefined,
  password: undefined,
  realName: undefined,
  idCard: undefined,
  idCardExpire: undefined,
  email: undefined,
  phone: undefined,
  auditStatus: undefined,
  status: undefined,
  defaultCurrency: undefined,
  twoFactorAuthStatus: undefined,
  twoFactorAuthSecret: undefined,
  twoFactorAuthBindTime: undefined,
  twoFactorAuthLastVerifyTime: undefined
})
const formRules = reactive({
  mobile: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  defaultCurrency: [{ required: true, message: '币种不能为空', trigger: 'blur' }],
  twoFactorAuthStatus: [{ required: true, message: '2FA验证状态：0-未开启 1-已开启 2-待验证不能为空', trigger: 'blur' }]
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
      formData.value = await InfoApi.getInfo(id)
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
    const data = formData.value as unknown as InfoVO
    if (formType.value === 'create') {
      await InfoApi.createInfo(data)
      message.success(t('common.createSuccess'))
    } else {
      await InfoApi.updateInfo(data)
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
    mobile: undefined,
    password: undefined,
    realName: undefined,
    idCard: undefined,
    idCardExpire: undefined,
    email: undefined,
    phone: undefined,
    auditStatus: undefined,
    status: undefined,
    defaultCurrency: undefined,
    twoFactorAuthStatus: undefined,
    twoFactorAuthSecret: undefined,
    twoFactorAuthBindTime: undefined,
    twoFactorAuthLastVerifyTime: undefined
  }
  formRef.value?.resetFields()
}
</script>

<style lang="scss" scoped>
:deep(.el-input.is-disabled .el-input__inner) {
  &::placeholder {
    color: transparent;
  }
}
</style>
