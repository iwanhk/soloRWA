<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="formLoading"
      :disabled="formType === 'audit'"
    >
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="formData.projectName" placeholder="请输入项目名称" />
      </el-form-item>
      <el-form-item label="项目类" prop="projectType">
        <el-select v-model="formData.projectType" placeholder="请选择项目类">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select v-model="formData.assetType" placeholder="请选择资产类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_ASSET_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="发行数量(份)" prop="issueQuantity">
        <el-input-number v-model="formData.issueQuantity" placeholder="请输入发行数量(份)" class="!w-full" :min="0" />
      </el-form-item>
      <el-form-item label="发行单价(U)" prop="issueUnitPrice">
        <el-input-number v-model="formData.issueUnitPrice" placeholder="请输入发行单价(U)" class="!w-full" :precision="2" :step="0.01" :min="0" />
      </el-form-item>
      <el-form-item label="剩余数量(份)" prop="remainingQuantity" v-if="formType !== 'create'">
        <el-input v-model="formData.remainingQuantity" placeholder="请输入剩余数量(份)" disabled />
      </el-form-item>
      <el-form-item label="预期年化收益" prop="expectedAnnualReturn">
        <el-input v-model="formData.expectedAnnualReturn" placeholder="请输入预期年化收益" type="number">
          <template #append>%</template>
        </el-input>
      </el-form-item>
      <el-form-item label="起购量" prop="minimumPurchase">
        <el-input-number v-model="formData.minimumPurchase" placeholder="请输入起购量" class="!w-full" :min="0" />
      </el-form-item>
      <el-form-item label="发行链" prop="issueChainId">
        <el-select v-model="formData.issueChainId" placeholder="请选择发行链">
          <el-option
            v-for="chain in chainList"
            :key="chain.id"
            :label="chain.name"
            :value="chain.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="认购期-开始" prop="subscriptionStartTime">
        <el-date-picker
          v-model="formData.subscriptionStartTime"
          type="date"
          value-format="x"
          placeholder="选择认购期-开始"
        />
      </el-form-item>
      <el-form-item label="认购期-结束" prop="subscriptionEndTime">
        <el-date-picker
          v-model="formData.subscriptionEndTime"
          type="date"
          value-format="x"
          placeholder="选择认购期-结束"
        />
      </el-form-item>
<!--      <el-form-item label="分红期-开始" prop="dividendsStartTime">
        <el-date-picker
          v-model="formData.dividendsStartTime"
          type="date"
          value-format="x"
          placeholder="选择分红期-开始"
        />
      </el-form-item>
      <el-form-item label="分红期-结束" prop="dividendsEndTime">
        <el-date-picker
          v-model="formData.dividendsEndTime"
          type="date"
          value-format="x"
          placeholder="选择分红期-结束"
        />
      </el-form-item>-->
      <el-form-item label="锁定期-开始" prop="lockStartTime">
        <el-date-picker
          v-model="formData.lockStartTime"
          type="date"
          value-format="x"
          placeholder="选择锁定期-开始"
        />
      </el-form-item>
      <el-form-item label="锁定期-结束" prop="lockEndTime">
        <el-date-picker
          v-model="formData.lockEndTime"
          type="date"
          value-format="x"
          placeholder="选择锁定期-结束"
        />
      </el-form-item>
<!--      <el-form-item label="状态" prop="projectStatus">
        <el-select v-model="formData.projectStatus" placeholder="请选择状态：1-未开售 2-出售中 3-已售罄 4-盈利中">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
      <el-form-item label="项目介绍（图文）" prop="projectIntro">
        <div v-if="formType === 'audit'" v-html="formData.projectIntro" class="w-full border p-2 rounded bg-gray-50 min-h-[100px]"></div>
        <Editor v-else v-model="formData.projectIntro" placeholder="请输入项目介绍（图文）" />
      </el-form-item>
      <el-form-item label="项目资料URL" prop="projectFileUrls">
        <UploadFileDTO
          v-model="formData.projectFileUrls"
          :limit="10"
          :file-type="['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'zip', 'rar']"
        />
      </el-form-item>
      
      <!-- 认购合同 -->
      <el-form-item label="认购合同" prop="subscriptionContractIds">
        <div class="flex gap-2 w-full">
          <el-select
            v-model="formData.subscriptionContractIds"
            placeholder="选择认购合同"
            class="w-full"
            filterable
            clearable
          >
            <el-option
              v-for="item in agreementList"
              :key="item.id"
              :label="item.agreementTitle"
              :value="item.id + ''"
            />
          </el-select>
          <el-button 
            v-if="formData.subscriptionContractIds" 
            type="primary" 
            link 
            @click="handlePreview(formData.subscriptionContractIds)"
          >
            查看
          </el-button>
        </div>
      </el-form-item>

      <!-- 分红合同 -->
      <el-form-item label="分红合同" prop="dividendContractIds">
        <div class="flex gap-2 w-full">
          <el-select
            v-model="formData.dividendContractIds"
            placeholder="选择分红合同"
            class="w-full"
            filterable
            clearable
          >
            <el-option
              v-for="item in agreementList"
              :key="item.id"
              :label="item.agreementTitle"
              :value="item.id + ''"
            />
          </el-select>
          <el-button 
            v-if="formData.dividendContractIds" 
            type="primary" 
            link 
            @click="handlePreview(formData.dividendContractIds)"
          >
            查看
          </el-button>
        </div>
      </el-form-item>

      <!-- 到期赎回合同 -->
      <el-form-item label="到期赎回合同" prop="maturityRedemptionContractIds">
        <div class="flex gap-2 w-full">
          <el-select
            v-model="formData.maturityRedemptionContractIds"
            placeholder="选择到期赎回合同"
            class="w-full"
            filterable
            clearable
          >
            <el-option
              v-for="item in agreementList"
              :key="item.id"
              :label="item.agreementTitle"
              :value="item.id + ''"
            />
          </el-select>
          <el-button 
            v-if="formData.maturityRedemptionContractIds" 
            type="primary" 
            link 
            @click="handlePreview(formData.maturityRedemptionContractIds)"
          >
            查看
          </el-button>
        </div>
      </el-form-item>

      <!-- 提前赎回合同 -->
      <el-form-item label="提前赎回合同" prop="earlyRedemptionContractIds">
        <div class="flex gap-2 w-full">
          <el-select
            v-model="formData.earlyRedemptionContractIds"
            placeholder="选择提前赎回合同"
            class="w-full"
            filterable
            clearable
          >
            <el-option
              v-for="item in agreementList"
              :key="item.id"
              :label="item.agreementTitle"
              :value="item.id + ''"
            />
          </el-select>
          <el-button 
            v-if="formData.earlyRedemptionContractIds" 
            type="primary" 
            link 
            @click="handlePreview(formData.earlyRedemptionContractIds)"
          >
            查看
          </el-button>
        </div>
      </el-form-item>
      <el-form-item label="赎回规则" prop="redemptionRules">
        <el-input v-model="formData.redemptionRules" placeholder="请输入赎回规则" type="textarea">
          <template #append>%</template>
        </el-input>
      </el-form-item>
      <el-form-item label="提前赎回手续费配置" prop="earlyRedemptionFeeJson">
        <div class="w-full">
          <div v-for="(item, index) in feeConfigList" :key="index" class="flex gap-2 mb-2">
            <el-input v-model="item.days" placeholder="天数" type="number" class="w-1/3">
              <template #append>天</template>
            </el-input>
            <el-input v-model="item.ratio" placeholder="比例" type="number" class="w-1/3">
              <template #append>%</template>
            </el-input>
            <el-button link type="danger" icon="ep:delete" @click="removeFeeConfig(index)">删除</el-button>
          </div>
          <el-button type="primary" plain icon="ep:plus" @click="addFeeConfig">添加配置</el-button>
        </div>
      </el-form-item>

      <el-form-item label="项目图片URL" prop="projectImageUrls">
        <UploadImgs v-model="formData.projectImageUrls" />
      </el-form-item>
      <el-form-item label="项目视频URL" prop="projectVideoUrl">
        <UploadFile
          v-model="formData.projectVideoUrl"
          :file-type="['mp4', 'avi', 'mov', 'wmv']"
          :limit="1"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <!-- 审核模式下的按钮 -->
      <template v-if="formType === 'audit'">
        <el-button @click="handleAudit(2)" type="success" :disabled="formLoading">审核通过</el-button>
        <el-button @click="handleAudit(3)" type="danger" :disabled="formLoading">审核不通过</el-button>
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </template>
      <!-- 编辑/新增模式下的按钮 -->
      <template v-else>
        <el-button @click="submitForm(1,1)" type="primary" :disabled="formLoading">提交审核</el-button>
        <el-button @click="submitForm(0,0)" type="info" :disabled="formLoading">存为草稿</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </template>
    </template>
  </Dialog>
  
  <!-- 协议预览弹窗 -->
  <Dialog title="协议内容" v-model="previewDialogVisible" width="60%">
    <div v-html="previewContent" class="p-4 bg-white min-h-[300px] border rounded"></div>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions,getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { InfoApi, InfoVO } from '@/api/project/projectinfo'
import { ChainApi, ChainVO } from '@/api/chain/chain'
import { AgreementApi, AgreementVO, AgreementSimpleRespVO } from '@/api/user/agreement'
import Editor from '@/components/Editor/src/Editor.vue'
import UploadImgs from '@/components/UploadFile/src/UploadImgs.vue'
import UploadFile from '@/components/UploadFile/src/UploadFile.vue'
import UploadFileDTO from '@/components/UploadFile/src/UploadFileDTO.vue'

/** 项目核心表（基础+状态） 表单 */
defineOptions({ name: 'InfoForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  projectId: undefined,
  projectName: undefined,
  projectType: undefined,
  assetType: undefined,
  publisherUserId: undefined,
  publisherCompanyName: undefined,
  issueQuantity: undefined,
  issueUnitPrice: undefined,
  remainingQuantity: undefined,
  expectedAnnualReturn: undefined,
  minimumPurchase: undefined,
  issueChainId: undefined,
  subscriptionStartTime: undefined,
  subscriptionEndTime: undefined,
  dividendsStartTime: undefined,
  dividendsEndTime: undefined,
  lockStartTime: undefined,
  lockEndTime: undefined,
  projectStatus: undefined,
  projectIntro: undefined,
  projectFileUrls: [],
  subscriptionContractIds: undefined,
  dividendContractIds: undefined,
  maturityRedemptionContractIds: undefined,
  earlyRedemptionContractIds: undefined,
  earlyRedemptionFeeJson: undefined,
  projectImageUrls: [],
  projectVideoUrl: undefined,
  redemptionRules: undefined,
})

// 时间校验规则
const validateSubscriptionTime = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('认购期-结束不能为空'))
  } else if (formData.value.subscriptionStartTime && value < formData.value.subscriptionStartTime) {
    callback(new Error('认购期-结束时间不能早于开始时间'))
  } else {
    callback()
  }
}

const validateDividendsTime = (rule: any, value: any, callback: any) => {
  if (formData.value.dividendsStartTime && value && value < formData.value.dividendsStartTime) {
    callback(new Error('分红期-结束时间不能早于开始时间'))
  } else {
    callback()
  }
}

const validateLockTime = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('锁定期-结束不能为空'))
  } else if (formData.value.lockStartTime && value < formData.value.lockStartTime) {
    callback(new Error('锁定期-结束时间不能早于开始时间'))
  } else {
    callback()
  }
}

const formRules = reactive({
  projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  projectType: [{ required: true, message: '项目类不能为空', trigger: 'change' }],
  assetType: [{ required: true, message: '资产类型不能为空', trigger: 'change' }],
  issueQuantity: [{ required: true, message: '发行数量(份)不能为空', trigger: 'blur' }],
  issueUnitPrice: [{ required: true, message: '发行单价(U)不能为空', trigger: 'blur' }],
  // remainingQuantity: [{ required: true, message: '剩余数量(份)【高频更新】不能为空', trigger: 'blur' }],
  expectedAnnualReturn: [{ required: true, message: '预期年化收益不能为空', trigger: 'blur' }],
  minimumPurchase: [{ required: true, message: '起购量不能为空', trigger: 'blur' }],
  issueChainId: [{ required: true, message: '发行链不能为空', trigger: 'change' }],
  subscriptionStartTime: [{ required: true, message: '认购期-开始不能为空', trigger: 'blur' }],
  subscriptionEndTime: [{ required: true, validator: validateSubscriptionTime, trigger: 'blur' }],
  dividendsEndTime: [{ validator: validateDividendsTime, trigger: 'blur' }],
  lockStartTime: [{ required: true, message: '锁定期-开始不能为空', trigger: 'blur' }],
  lockEndTime: [{ required: true, validator: validateLockTime, trigger: 'blur' }],
//  projectStatus: [{ required: true, message: '状态：1-未开售 2-出售中 3-已售罄 4-盈利中不能为空', trigger: 'change' }],
  projectImageUrls: [{ required: true, message: '项目图片URL不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

// 发行链列表
const chainList = ref<ChainVO[]>([])

// 协议列表
const agreementList = ref<AgreementSimpleRespVO[]>([])

// 手续费配置
const feeConfigList = ref<{ days: number; ratio: number }[]>([])

// 获取发行链列表
const getChainList = async () => {
  try {
    const data = await ChainApi.getActiveChainList()
    chainList.value = data
  } catch (e) {
    console.error('获取发行链失败', e)
  }
}

// 获取协议列表
const getAgreementList = async () => {
  try {
    const data = await AgreementApi.getAgreementSimple()
    agreementList.value = data
  } catch (e) {
    console.error('获取协议列表失败', e)
  }
}

// 手续费配置操作
const addFeeConfig = () => {
  feeConfigList.value.push({ days: 0, ratio: 0 })
}
const removeFeeConfig = (index: number) => {
  feeConfigList.value.splice(index, 1)
}

// 协议预览
const previewDialogVisible = ref(false)
const previewContent = ref('')
const handlePreview = async (contractId: string) => {
  if (!contractId) return
  try {
    const id = parseInt(contractId)
    if (isNaN(id)) return
    const agreement = await AgreementApi.getAgreement(id)
    if (agreement) {
      previewContent.value = agreement.agreementContent || '暂无内容'
      previewDialogVisible.value = true
    }
  } catch (e) {
    console.error('获取协议详情失败', e)
  }
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  
  // 获取发行链列表
  getChainList()
  
  // 获取协议列表
  getAgreementList()
  
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await InfoApi.getInfo(id)
      
      // 确保合同为字符串类型，避免 el-select 类型不匹配导致的问题
      if (data.subscriptionContractIds) data.subscriptionContractIds = String(data.subscriptionContractIds)
      if (data.dividendContractIds) data.dividendContractIds = String(data.dividendContractIds)
      if (data.maturityRedemptionContractIds) data.maturityRedemptionContractIds = String(data.maturityRedemptionContractIds)
      if (data.earlyRedemptionContractIds) data.earlyRedemptionContractIds = String(data.earlyRedemptionContractIds)

      // 确保图片URL为数组
      if (data.projectImageUrls && typeof data.projectImageUrls === 'string') {
        data.projectImageUrls = data.projectImageUrls.split(',')
      }

      // 确保项目资料URL为JSON对象或空数组 (UploadFileDTO 需要数组)
      if (data.projectFileUrls) {
          try {
             // 尝试解析 JSON
             const list = JSON.parse(data.projectFileUrls)
             if(Array.isArray(list)){
                formData.value.projectFileUrls = list
             } else {
                 // 可能是旧格式字符串，转为对象数组
                if (typeof data.projectFileUrls === 'string') {
                    formData.value.projectFileUrls = data.projectFileUrls.split(',').map(url => ({
                        name: url.substring(url.lastIndexOf('/') + 1),
                        url
                    }))
                }
             }
          } catch(e) {
               // 解析失败，可能是旧格式逗号分隔字符串
               if (typeof data.projectFileUrls === 'string') {
                    formData.value.projectFileUrls = data.projectFileUrls.split(',').map(url => ({
                        name: url.substring(url.lastIndexOf('/') + 1),
                        url
                    }))
                }
          }
      } else {
          formData.value.projectFileUrls = []
      }

      formData.value = data
      
      // 解析手续费配置
      if (data.earlyRedemptionFeeJson) {
        try {
          const json = JSON.parse(data.earlyRedemptionFeeJson)
          if (Array.isArray(json)) {
            feeConfigList.value = json.map((item: any) => ({
              days: parseInt(item.day),
              ratio: parseFloat(item.value)
            }))
          } else {
            feeConfigList.value = []
          }
        } catch (e) {
          console.error('解析手续费JSON失败', e)
          feeConfigList.value = []
        }
      } else {
        feeConfigList.value = []
      }
      
    } finally {
      formLoading.value = false
    }
  } else {
    feeConfigList.value = []
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async (auditStatus: number,projectStatus:number) => {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  
  // 提交请求
  formLoading.value = true
  try {
    const data = JSON.parse(JSON.stringify(formData.value))
    
    // 设置审核状态
    data.auditStatus = auditStatus
    data.projectStatus = projectStatus
    
    // 转换手续费配置为JSON
    if (feeConfigList.value.length > 0) {
      const list = feeConfigList.value.map(item => ({
        day: String(Number(item.days)),
        value: String(Number(item.ratio))
      }))
      data.earlyRedemptionFeeJson = JSON.stringify(list)
    } else {
      data.earlyRedemptionFeeJson = ''
    }
    
    // 转换图片URL数组为字符串
    if (Array.isArray(data.projectImageUrls)) {
      data.projectImageUrls = data.projectImageUrls.join(',')
    }

    // 转换项目资料URL数组为JSON字符串
    if (Array.isArray(data.projectFileUrls)) {
      // 保存完整信息的 JSON 字符串
      data.projectFileUrls = JSON.stringify(data.projectFileUrls)
    }

    if (formType.value === 'create') {
      // 新增时，剩余数量默认为发行数量
      if (data.remainingQuantity === undefined || data.remainingQuantity === null) {
        data.remainingQuantity = data.issueQuantity
      }
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
    projectId: undefined,
    projectName: undefined,
    projectType: undefined,
    assetType: undefined,
    publisherUserId: undefined,
    publisherCompanyName: undefined,
    issueQuantity: undefined,
    issueUnitPrice: undefined,
    remainingQuantity: undefined,
    expectedAnnualReturn: undefined,
    minimumPurchase: undefined,
    issueChainId: undefined,
    subscriptionStartTime: undefined,
    subscriptionEndTime: undefined,
    lockStartTime: undefined,
    lockEndTime: undefined,
    projectStatus: undefined,
    projectIntro: undefined,
    projectFileUrls: undefined,
    subscriptionContractIds: undefined,
    dividendContractIds: undefined,
    maturityRedemptionContractIds: undefined,
    earlyRedemptionContractIds: undefined,
    earlyRedemptionFeeJson: undefined,
    projectImageUrls: [],
    projectVideoUrl: ''
  }
  formRef.value?.resetFields()
  
  feeConfigList.value = []
}

/** 审核操作 */
const handleAudit = async (status: number) => {
  // 注意：formData.value.id 是 undefined，因为 resetForm 时清空了。应该用 formData.value.projectId 或者在 open 时保存 id
  if (!formData.value.projectId) return
  try {
    if (status === 2) {
      await message.confirm('确认审核通过该项目吗？')
      const data = {
        projectId: formData.value.projectId,
        approved: true,
        auditRemark: '审核通过'
      }
      await InfoApi.auditProject(data)
      message.success('审核完成')
    } else {
      const { value } = await ElMessageBox.prompt('请输入审核不通过原因', '审核不通过', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '审核不通过原因不能为空',
        inputType: 'textarea',
      })
      const data = {
        projectId: formData.value.projectId,
        approved: false,
        auditRemark: value
      }
      await InfoApi.auditProject(data)
      message.success('审核完成')
    }
    dialogVisible.value = false
    emit('success')
  } catch {}
}
</script>
