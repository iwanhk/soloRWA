<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="auto"
    >
      <el-form-item label="发行商名" prop="name">
        <el-input v-model="formData.name" placeholder="请输入发行商名" />
      </el-form-item>
      <el-form-item label="套餐" prop="packageId">
        <el-select v-model="formData.packageId" clearable placeholder="请选择发行商套餐">
          <el-option
            v-for="item in packageList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="formData.contactName" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系手机" prop="contactMobile">
        <el-input v-model="formData.contactMobile" placeholder="请输入联系手机" />
      </el-form-item>
      <el-form-item v-if="formData.id === undefined" label="登录账号" prop="username">
        <el-input v-model="formData.username" placeholder="请输入用户名称" />
      </el-form-item>
      <el-form-item v-if="formData.id === undefined" label="登录密码" prop="password">
        <el-input
          v-model="formData.password"
          placeholder="请输入用户密码"
          show-password
          type="password"
        />
      </el-form-item>
<!--      <el-form-item label="绑定域名" prop="websites">
        <el-input-tag
          v-model="formData.websites"
          placeholder="请输入绑定域名，按回车添加"
          class="w-full"
        />
      </el-form-item>-->
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.id === undefined" label="审核状态" prop="auditStatus">
        <el-radio-group v-model="formData.auditStatus">
          <el-radio :value="0">待提交审核</el-radio>
          <el-radio :value="2">审核通过</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <!-- 发行商信息（仅当审核状态为已通过时显示） -->
      <template v-if="formData.id === undefined && formData.auditStatus === 2">
        <el-divider content-position="left">发行商信息</el-divider>

        <el-form-item label="企业类型" prop="companyType">
          <el-select v-model="formData.companyType" placeholder="请选择企业类型">
            <el-option label="中国大陆企业" :value="1" />
            <el-option label="中国香港企业" :value="2" />
            <el-option label="海外企业" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="注册时间" prop="registerTime">
          <el-date-picker
            v-model="formData.registerTime"
            type="datetime"
            placeholder="选择注册时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="认证身份" prop="authIdentity">
          <el-select v-model="formData.authIdentity" placeholder="请选择认证身份">
            <template v-if="formData.companyType === 1">
              <el-option label="企业法人" value="企业法人" />
              <el-option label="经办人" value="经办人" />
            </template>
            <template v-else-if="formData.companyType === 2">
              <el-option label="授权董事" value="授权董事" />
              <el-option label="授权签字人" value="授权签字人" />
            </template>
            <template v-else>
              <el-option label="董事" value="董事" />
              <el-option label="授权签字人" value="授权签字人" />
            </template>
          </el-select>
        </el-form-item>
        
        <el-form-item :label="formData.companyType === 1 ? '统一社会信用代码' : '企业注册代码'" prop="companyCreditCode">
          <el-input v-model="formData.companyCreditCode" :placeholder="formData.companyType === 1 ? '18位统一社会信用代码' : '请输入企业注册代码'" />
        </el-form-item>
        
        <el-form-item label="营业执照" prop="businessLicenseUrl">
          <UploadImg v-model="formData.businessLicenseUrl" />
        </el-form-item>
        
        <el-form-item label="资质文件" prop="qualificationFileUrls">
          <UploadFile v-model="formData.qualificationFileUrls" />
        </el-form-item>
        
        <el-form-item label="授权文件" prop="authorizationFileUrls">
          <UploadFile v-model="formData.authorizationFileUrls" />
        </el-form-item>
        
        <el-form-item label="身份证姓名" prop="idCardName">
          <el-input v-model="formData.idCardName" placeholder="请输入身份证姓名" />
        </el-form-item>
        
        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="formData.idCardNo" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="身份证有效期" prop="idCardExpireTime">
          <el-date-picker
            v-model="formData.idCardExpireTime"
            type="date"
            placeholder="选择有效期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="身份证正面" prop="idCardFrontUrl">
          <UploadImg v-model="formData.idCardFrontUrl" />
        </el-form-item>
        
        <el-form-item label="身份证背面" prop="idCardBackUrl">
          <UploadImg v-model="formData.idCardBackUrl" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="开户名" prop="bankAccountName">
          <el-input v-model="formData.bankAccountName" placeholder="与公司名称/法人姓名一致" />
        </el-form-item>
        
        <el-form-item label="银行账户" prop="bankAccount">
          <el-input v-model="formData.bankAccount" placeholder="请输入银行卡号" />
        </el-form-item>
        
        <el-form-item label="开户行" prop="bankName">
          <el-input v-model="formData.bankName" placeholder="请输入开户行" />
        </el-form-item>
      </template>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as TenantApi from '@/api/system/tenant'
import { CommonStatusEnum } from '@/utils/constants'
import * as TenantPackageApi from '@/api/system/tenantPackage'
import { UploadImg, UploadFile } from '@/components/UploadFile'

defineOptions({ name: 'SystemTenantForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  name: undefined,
  packageId: undefined,
  contactName: undefined,
  contactMobile: undefined,
  accountCount: undefined,
  expireTime: undefined,
  websites: [],
  status: CommonStatusEnum.ENABLE,
  // 新增专属
  username: undefined,
  password: undefined,
  // 审核状态
  auditStatus: undefined,
  // 发行商信息（公司名和用户手机号从发行商信息自动填充）
  registerTime: undefined,
  companyType: 1, // 1-中国大陆 2-香港 3-海外
  authIdentity: undefined,
  companyName: undefined,
  companyCreditCode: undefined,
  businessLicenseUrl: undefined,
  qualificationFileUrls: undefined,
  authorizationFileUrls: undefined,
  idCardName: undefined,
  idCardNo: undefined,
  idCardExpireTime: undefined,
  idCardFrontUrl: undefined,
  idCardBackUrl: undefined,
  email: undefined,
  bankAccountName: undefined,
  bankAccount: undefined,
  bankName: undefined
})
const formRules = reactive({
  name: [{ required: true, message: '发行商名不能为空', trigger: 'blur' }],
  packageId: [{ required: true, message: '发行商套餐不能为空', trigger: 'blur' }],
  contactName: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '发行商状态不能为空', trigger: 'blur' }],
  username: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '用户密码不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const packageList = ref([] as TenantPackageApi.TenantPackageVO[]) // 发行商套餐

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
      formData.value = await TenantApi.getTenant(id)
    } finally {
      formLoading.value = false
    }
  }
  // 加载套餐列表
  packageList.value = await TenantPackageApi.getTenantPackageList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    const data = JSON.parse(JSON.stringify(formData.value)) as unknown as TenantApi.TenantVO
    
    // 处理多文件上传字段：将文件数组转换为逗号分隔的URL字符串
    if (data.qualificationFileUrls && Array.isArray(data.qualificationFileUrls)) {
      // @ts-ignore
      data.qualificationFileUrls = data.qualificationFileUrls.map((file) => file.url || file).join(',')
    }
    if (data.authorizationFileUrls && Array.isArray(data.authorizationFileUrls)) {
      // @ts-ignore
      data.authorizationFileUrls = data.authorizationFileUrls.map((file) => file.url || file).join(',')
    }
    
    if (formType.value === 'create') {
      // 如果选择了审核状态，使用新接口
      await TenantApi.createTenantWithAudit(data)
      message.success(t('common.createSuccess'))
    } else {
      await TenantApi.updateTenant(data)
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
    name: undefined,
    packageId: undefined,
    contactName: undefined,
    contactMobile: undefined,
    accountCount: 99999,
    expireTime: new Date('2099-12-31').getTime(),
    websites: [],
    status: CommonStatusEnum.ENABLE,
    username: undefined,
    password: undefined,
    auditStatus: undefined,
    registerTime: undefined,
    companyType: 1,
    authIdentity: undefined,
    companyName: undefined,
    companyCreditCode: undefined,
    businessLicenseUrl: undefined,
    qualificationFileUrls: undefined,
    authorizationFileUrls: undefined,
    idCardName: undefined,
    idCardNo: undefined,
    idCardExpireTime: undefined,
    idCardFrontUrl: undefined,
    idCardBackUrl: undefined,
    email: undefined,
    bankAccountName: undefined,
    bankAccount: undefined,
    bankName: undefined
  }
  formRef.value?.resetFields()
}
</script>
