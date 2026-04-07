<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="登录用户ID" prop="userId">
        <el-input v-model="formData.userId" placeholder="请输入登录用户ID" />
      </el-form-item>
      <el-form-item label="登录方式：1-密码登录 2-验证码登录" prop="loginType">
        <el-select v-model="formData.loginType" placeholder="请选择登录方式：1-密码登录 2-验证码登录">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="登录时间" prop="loginTime">
        <el-date-picker
          v-model="formData.loginTime"
          type="date"
          value-format="x"
          placeholder="选择登录时间"
        />
      </el-form-item>
      <el-form-item label="登录IP地址" prop="loginIp">
        <el-input v-model="formData.loginIp" placeholder="请输入登录IP地址" />
      </el-form-item>
      <el-form-item label="登录城市（如“中国 香港”）" prop="loginCity">
        <el-input v-model="formData.loginCity" placeholder="请输入登录城市（如“中国 香港”）" />
      </el-form-item>
      <el-form-item label="设备信息（可选：如手机型号/浏览器标识）" prop="deviceInfo">
        <el-input v-model="formData.deviceInfo" placeholder="请输入设备信息（可选：如手机型号/浏览器标识）" />
      </el-form-item>
      <el-form-item label="登录状态：1-成功 2-失败（失败时可记录原因）" prop="loginStatus">
        <el-select v-model="formData.loginStatus" placeholder="请选择登录状态：1-成功 2-失败（失败时可记录原因）">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.LOGIN_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="登录失败原因（如“密码错误”，登录成功时为空）" prop="failReason">
        <el-input v-model="formData.failReason" placeholder="请输入登录失败原因（如“密码错误”，登录成功时为空）" />
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
import { LoginLogApi, LoginLogVO } from '@/api/user/userloginlog'

/** 用户登录日志 表单 */
defineOptions({ name: 'LoginLogForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  userId: undefined,
  loginType: undefined,
  loginTime: undefined,
  loginIp: undefined,
  loginCity: undefined,
  deviceInfo: undefined,
  loginStatus: undefined,
  failReason: undefined
})
const formRules = reactive({
  userId: [{ required: true, message: '登录用户ID不能为空', trigger: 'blur' }],
  loginType: [{ required: true, message: '登录方式：1-密码登录 2-验证码登录不能为空', trigger: 'change' }],
  loginTime: [{ required: true, message: '登录时间不能为空', trigger: 'blur' }],
  loginIp: [{ required: true, message: '登录IP地址不能为空', trigger: 'blur' }],
  loginStatus: [{ required: true, message: '登录状态：1-成功 2-失败（失败时可记录原因）不能为空', trigger: 'change' }]
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
      formData.value = await LoginLogApi.getLoginLog(id)
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
    const data = formData.value as unknown as LoginLogVO
    if (formType.value === 'create') {
      await LoginLogApi.createLoginLog(data)
      message.success(t('common.createSuccess'))
    } else {
      await LoginLogApi.updateLoginLog(data)
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
    loginType: undefined,
    loginTime: undefined,
    loginIp: undefined,
    loginCity: undefined,
    deviceInfo: undefined,
    loginStatus: undefined,
    failReason: undefined
  }
  formRef.value?.resetFields()
}
</script>