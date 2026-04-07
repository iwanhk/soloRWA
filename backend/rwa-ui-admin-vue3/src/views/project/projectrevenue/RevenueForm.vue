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
            v-for="item in projectList"
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

      <el-form-item label="矿池收益" prop="coinRevenue">
        <el-input-number
          v-model="formData.coinRevenue"
          :precision="8"
          :min="0"
          placeholder="请输入矿池收益"
          style="width: 100%"
        >
          <template #suffix>
            <span>{{ formData.coinCode || '' }}</span>
          </template>
        </el-input-number>
        <el-button type="primary" link @click="handleGetPoolRevenue" :loading="gettingPoolData">
          获取矿池数据
        </el-button>
      </el-form-item>

      <el-form-item label="汇率" prop="exchangeRate">
        <el-input-number
          v-model="formData.exchangeRate"
          :precision="8"
          :min="0"
          placeholder="请输入汇率"
          style="width: 100%"
        >
          <template #suffix>
            <span>USD</span>
          </template>
        </el-input-number>
        <el-button type="primary" link @click="handleGetCoinRate" :loading="gettingRate">
          获取当前汇率
        </el-button>
      </el-form-item>

      <el-divider content-position="left">
        成本配置
        <el-button type="primary" link @click="handleCalculateCosts" :loading="calculating">
          自动计算
        </el-button>
      </el-divider>

      <el-form-item label="电力成本" prop="electricityCost">
        <el-input-number
          v-model="formData.electricityCost"
          :precision="8"
          :min="0"
          placeholder="请输入电力成本"
          style="width: 100%"
        >
          <template #suffix>
            <span>USD</span>
          </template>
        </el-input-number>
      </el-form-item>

      <el-form-item label="人力成本" prop="peopleCost">
        <el-input-number
          v-model="formData.peopleCost"
          :precision="8"
          :min="0"
          placeholder="请输入人力成本"
          style="width: 100%"
        >
          <template #suffix>
            <span>USD</span>
          </template>
        </el-input-number>
      </el-form-item>

      <el-form-item label="项目方收益" prop="projectRevenue">
        <el-input-number
          v-model="formData.projectRevenue"
          :precision="8"
          :min="0"
          placeholder="自动计算项目方收益"
          style="width: 100%"
        >
          <template #suffix>
            <span>{{ formData.coinCode || '' }}</span>
          </template>
        </el-input-number>
      </el-form-item>

      <el-form-item label="可发放收益" prop="availableRevenue">
        <el-input-number
          v-model="formData.availableRevenue"
          :precision="8"
          :min="0"
          placeholder="请输入可发放收益"
          style="width: 100%"
        >
          <template #suffix>
            <span>{{ formData.coinCode || '' }}</span>
          </template>
        </el-input-number>
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

/** 项目收益 表单 */
defineOptions({ name: 'RevenueForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const gettingPoolData = ref(false) // 获取矿池数据中
const calculating = ref(false) // 自动计算中
const gettingRate = ref(false) // 获取汇率中

const formData = ref({
  id: undefined,
  revenueDate: undefined,
  revenue: undefined,
  projectId: undefined,
  projectName: undefined,
  electricityCost: undefined,
  peopleCost: undefined,
  projectRevenue: undefined,
  availableRevenue: undefined,
  coinRevenue: undefined,
  exchangeRate: undefined,
  coinCode: undefined,
  isSend: undefined,
  isSync: undefined,
  remark: undefined
})

const formRules = reactive({
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  revenueDate: [{ required: true, message: '请选择收益日期', trigger: 'change' }],
  revenue: [{ required: true, message: '请输入总收益', trigger: 'blur' }]
})

const formRef = ref() // 表单 Ref
const projectList = ref<any[]>([]) // 项目列表

/** 加载项目列表 */
const loadProjectList = async () => {
  try {
    projectList.value = await InfoApi.getInfoSimple()
  } catch (e) {
    console.error('加载项目列表失败', e)
  }
}

/** 项目选择变化 */
const handleProjectChange = (projectId: number) => {
  const project = projectList.value.find((p) => p.projectId === projectId)
  if (project) {
    formData.value.projectName = project.projectName
  }
}

/** 获取矿池数据 */
const handleGetPoolRevenue = async () => {
  if (!formData.value.projectId) {
    message.warning('请先选择项目')
    return
  }
  if (!formData.value.revenueDate) {
    message.warning('请先选择收益日期')
    return
  }

  gettingPoolData.value = true
  try {
    const result = await RevenueApi.calculatePoolRevenue(
      formData.value.projectId,
      formData.value.revenueDate
    )
    formData.value.coinRevenue = result.coinRevenue as any
    formData.value.exchangeRate = result.exchangeRate as any
    formData.value.revenue = result.revenue as any
    formData.value.coinCode = result.coin as any

    // 如果获取成功，自动计算成本
    //await handleCalculateCosts()

    message.success('矿池数据获取成功')
  } catch (e) {
    console.error('获取矿池数据失败', e)
    message.error('获取矿池数据失败')
  } finally {
    gettingPoolData.value = false
  }
}

/** 获取币种汇率 */
const handleGetCoinRate = async () => {
  gettingRate.value = true
  try {
    const coinCode = 'BTCUSDT' // 默认传 BTC
    const rate = await RevenueApi.getCoinUsdRate(coinCode)
    formData.value.exchangeRate = rate as any
    message.success('汇率获取成功')
  } catch (e) {
    console.error('获取汇率失败', e)
    message.error('获取汇率失败')
  } finally {
    gettingRate.value = false
  }
}

/** 自动计算成本 */
const handleCalculateCosts = async () => {
  if (!formData.value.projectId) {
    message.warning('请先选择项目')
    return
  }
  if (!formData.value.coinRevenue || formData.value.coinRevenue <= 0) {
    // 如果没有收益，就不用计算成本了，或者也可以计算固定成本?
    // message.warning('请先输入总收益')
    // return
    // 修改为支持计算
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
    formData.value.electricityCost = result.electricityCost as any
    formData.value.peopleCost = result.peopleCost as any
    formData.value.projectRevenue = result.projectRevenue as any
    formData.value.availableRevenue = result.availableRevenue as any
    if (formData.value.revenue && formData.value.revenue > 0) {
      message.success('成本计算完成')
    }
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
  dialogTitle.value = t('action.' + type)
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
    const data = formData.value as unknown as RevenueVO
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
    electricityCost: undefined,
    peopleCost: undefined,
    projectRevenue: undefined,
    availableRevenue: undefined,
    coinRevenue: undefined,
    exchangeRate: undefined,
    coinCode: undefined,
    isSend: undefined,
    isSync: undefined,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>
