<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="650px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="110px"
      v-loading="formLoading"
    >
      <el-form-item label="项目" prop="projectId">
        <el-select
          v-model="formData.projectId"
          placeholder="请选择项目"
          filterable
          style="width: 100%"
          :disabled="formType === 'update'"
          @change="handleProjectChange"
        >
          <el-option
            v-for="item in fundProjectList"
            :key="item.projectId"
            :label="item.projectName"
            :value="item.projectId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="收益日期" prop="revenueDate">
        <el-date-picker
          v-model="formData.revenueDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="选择收益日期"
          style="width: 100%"
          :disabled="formType === 'update'"
        />
      </el-form-item>
      <el-form-item label="币种" prop="coinCode">
        <el-input
          v-model="formData.coinCode"
          style="width: 100%"
          :disabled="formType === 'update'"
        />
      </el-form-item>

      <el-form-item label="总收益" prop="coinRevenue">
        <el-input-number
          v-model="formData.coinRevenue"
          :precision="2"
          :min="0"
          placeholder="请输入基金当日总收益"
          style="width: 100%"
        />
      </el-form-item>

      <el-divider content-position="left">
        成本与分成
        <el-button type="primary" link @click="handleCalculateCosts" :loading="calculating">
          自动计算
        </el-button>
      </el-divider>

      <el-form-item label="运营分成" prop="peopleCost">
        <el-input-number
          v-model="formData.peopleCost"
          :precision="2"
          :min="0"
          placeholder="自动计算运维与团队分成"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="项目方收益" prop="projectRevenue">
        <el-input-number
          v-model="formData.projectRevenue"
          :precision="8"
          :min="0"
          placeholder="自动计算项目方收益"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="可发放收益" prop="availableRevenue">
        <el-input-number
          v-model="formData.availableRevenue"
          :precision="8"
          :min="0"
          placeholder="请输入可发放收益"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { RevenueApi, RevenueVO } from '@/api/project/projectrevenue'
import { InfoApi } from '@/api/project/projectinfo'

/** 基金收益 表单 */
defineOptions({ name: 'RevenueFundForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const calculating = ref(false) // 自动计算中

const formData = ref({
  id: undefined,
  revenueDate: undefined,
  revenue: undefined,
  projectId: undefined,
  projectName: undefined,
  electricityCost: 0,
  peopleCost: undefined,
  projectRevenue: undefined,
  availableRevenue: undefined,
  coinRevenue: undefined,
  exchangeRate: 1, // 基金型通常为 1
  coinCode: undefined,
  isSend: undefined,
  isSync: undefined,
  remark: undefined
})

const formRules = reactive({
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  revenueDate: [{ required: true, message: '请选择收益日期', trigger: 'change' }],
  revenue: [{ required: true, message: '请输入基金当日总收益', trigger: 'blur' }]
})

const formRef = ref() // 表单 Ref
const fundProjectList = ref<any[]>([]) // 基金项目列表

/** 加载项目列表 */
const loadProjectList = async () => {
  try {
    const allProjects = await InfoApi.getInfoSimple()
    // 仅展示基金型项目 (projectConfigType === 1)
    fundProjectList.value = allProjects.filter((p) => p.projectConfigType === 1)
  } catch (e) {
    console.error('加载项目列表失败', e)
  }
}

/** 项目选择变化 */
const handleProjectChange = (projectId: number) => {
  const project = fundProjectList.value.find((p) => p.projectId === projectId)
  if (project) {
    formData.value.projectName = project.projectName
    formData.value.coinCode = project.investmentCurrency
  }
}

/** 自动计算成本 */
const handleCalculateCosts = async () => {
  if (!formData.value.projectId) {
    message.warning('请先选择项目')
    return
  }

  calculating.value = true
  try {
    const revenue = formData.value.coinRevenue || 0
    if (!formData.value.revenueDate) {
      message.warning('请先选择收益日期')
      return
    }
    const result = await RevenueApi.calculateCosts(
      formData.value.projectId,
      revenue,
      formData.value.revenueDate as string
    )
    formData.value.peopleCost = result.peopleCost as any
    formData.value.projectRevenue = result.projectRevenue as any
    formData.value.availableRevenue = result.availableRevenue as any
    message.success('成本计算完成')
  } catch (e) {
    console.error('计算成本失败', e)
    message.error('计算成本失败')
  } finally {
    calculating.value = false
  }
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type) + ' - 基金收益'
  formType.value = type
  resetForm()

  // 加载项目列表
  await loadProjectList()

  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const res = await RevenueApi.getRevenue(id)
      if (Array.isArray(res.revenueDate)) {
        const year = res.revenueDate[0]
        const month = String(res.revenueDate[1]).padStart(2, '0')
        const day = String(res.revenueDate[2]).padStart(2, '0')
        res.revenueDate = `${year}-${month}-${day}`
      }
      formData.value = res
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
    // 补充剩余字段，确保后端兼容
    const data = {
      ...formData.value,
      electricityCost: 0,
      exchangeRate: 1
    } as unknown as RevenueVO

    if (formType.value === 'create') {
      await RevenueApi.createRevenue(data)
      message.success(t('common.createSuccess'))
    } else {
      await RevenueApi.updateAvailableRevenue(data)
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
    revenueDate: undefined,
    revenue: undefined,
    projectId: undefined,
    projectName: undefined,
    electricityCost: 0,
    peopleCost: undefined,
    projectRevenue: undefined,
    availableRevenue: undefined,
    coinRevenue: undefined,
    exchangeRate: 1,
    coin: undefined,
    isSend: undefined,
    isSync: undefined,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>
