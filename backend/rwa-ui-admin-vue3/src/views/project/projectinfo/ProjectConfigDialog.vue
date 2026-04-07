<template>
  <Dialog :title="'项目配置：' + projectName" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="loading"
    >
      <el-divider content-position="left">算力与电费</el-divider>
      
      <el-form-item label="单T功耗(W)" prop="powerConsumption">
        <el-input-number
          v-model="formData.powerConsumption"
          :min="0"
          :precision="2"
          placeholder="请输入单T功耗"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="电费(U/度)" prop="electricityPrice">
        <el-input-number
          v-model="formData.electricityPrice"
          :min="0"
          :precision="4"
          placeholder="请输入电费单价"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="每日电费示例">
         <el-tag type="info" class="w-full truncate">
           示例：{{ totalPower || 0 }} T × {{ formData.powerConsumption || 0 }} W/T × {{ formData.electricityPrice || 0 }} U/度 × 24h ÷ 1000 = {{ dailyElectricityCost }} U/天
         </el-tag>
         <div class="text-xs text-gray-400 mt-1">
           公式：总算力(发行量) × 单T功耗 × 电费单价 × 24 ÷ 1000
         </div>
      </el-form-item>
      
      <el-divider content-position="left">运营成本</el-divider>
      
      <el-form-item label="运维成本(U)" prop="operationCost">
        <el-input-number
          v-model="formData.operationCost"
          :min="0"
          :precision="2"
          placeholder="请输入运维成本"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="成本类型" prop="operationCostType">
        <el-radio-group v-model="formData.operationCostType">
          <el-radio :value="0">固定值/月(30天)</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="团队分成比例(%)" prop="teamShareRatio">
        <el-input-number
          v-model="formData.teamShareRatio"
          :min="0"
          :max="100"
          :precision="2"
          placeholder="请输入团队分成比例"
          style="width: 100%"
        />
      </el-form-item>
      
<!--      <el-divider content-position="left">收益阈值与预警</el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="最低阈值(日)" prop="thresholdMin">
             <el-input-number
              v-model="formData.thresholdMin"
              :min="0"
              :precision="6"
              placeholder="最低收益"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最高阈值(日)" prop="thresholdMax">
            <el-input-number
              v-model="formData.thresholdMax"
              :min="0"
              :precision="6"
              placeholder="最高收益"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>-->


      
      <el-divider content-position="left">矿池配置</el-divider>
      
      <el-form-item label="矿池AccessKey" prop="poolAccessKey">
        <el-input v-model="formData.poolAccessKey" placeholder="请输入矿池AccessKey" />
      </el-form-item>

      <el-form-item label="矿池私钥" prop="poolPrivateKey">
        <el-input 
          v-model="formData.poolPrivateKey" 
          type="password" 
          placeholder="请输入矿池私钥" 
          show-password
        />
        <el-text v-if="hasPoolPrivateKey" type="success" size="small" class="ml-2">
          <Icon icon="ep:check" /> 已配置
        </el-text>
        <el-text v-else type="info" size="small" class="ml-2">
          未配置
        </el-text>
      </el-form-item>
      
      <el-form-item label="结算账户" prop="poolName">
        <el-input v-model="formData.poolName" placeholder="请输入结算账户名称" />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        保 存
      </el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { InfoApi, ProjectConfigVO } from '@/api/project/projectinfo'

defineOptions({ name: 'ProjectConfigDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const projectId = ref<number>()
const projectName = ref('')
const hasPoolPrivateKey = ref(false)
const totalPower = ref(0) // 总算力(来自项目发行量)

const formRef = ref()
const formData = ref<ProjectConfigVO>({
  projectId: 0,
  powerConsumption: undefined,
  electricityPrice: undefined,
  operationCost: undefined,
  operationCostType: 0,
  teamShareRatio: undefined,
  thresholdMin: undefined,
  thresholdMax: undefined,
  alertEnabled: 0,
  poolAccessKey: '',
  poolPrivateKey: '',
  poolName: ''
})

const formRules = reactive({
  powerConsumption: [{ required: true, message: '单T功耗不能为空', trigger: 'blur' }],
  electricityPrice: [{ required: true, message: '电费不能为空', trigger: 'blur' }],
  operationCost: [{ required: true, message: '运维成本不能为空', trigger: 'blur' }]
})

// 计算每日电费
const dailyElectricityCost = computed(() => {
  const power = totalPower.value || 0
  const consumption = formData.value.powerConsumption || 0
  const price = formData.value.electricityPrice || 0
  if (!power || !consumption || !price) return '0.00'
  return ((power * consumption * price * 24) / 1000).toFixed(2)
})

// 打开弹窗
const open = async (id: number, name: string) => {
  dialogVisible.value = true
  projectId.value = id
  projectName.value = name
  hasPoolPrivateKey.value = false
  
  // 重置表单
  formData.value = {
    projectId: id,
    powerConsumption: undefined,
    electricityPrice: undefined,
    operationCost: undefined,
    operationCostType: 0,
    teamShareRatio: undefined,
    thresholdMin: undefined,
    thresholdMax: undefined,
    alertEnabled: 0,
    poolAccessKey: '',
    poolPrivateKey: '',
    poolName: ''
  }
  
  // 加载配置
  await loadConfig(id)
  // 加载项目基础信息以获取总算力
  await loadProjectInfo(id)
}

// 加载项目基础信息
const loadProjectInfo = async (id: number) => {
  try {
    const info = await InfoApi.getInfo(id)
    if (info) {
      // 假设发行数量即为总算力(T)
      totalPower.value = info.issueQuantity
    }
  } catch (e) {
    console.error('加载项目信息失败', e)
  }
}

// 加载配置
const loadConfig = async (id: number) => {
  loading.value = true
  try {
    const data = await InfoApi.getConfig(id)
    if (data) {
      formData.value = {
        ...formData.value,
        ...data,
        poolPrivateKey: '', // 私钥不回显
        operationCostType: data.operationCostType ?? 0 // 默认为0
      }
      // 记录是否有私钥
      hasPoolPrivateKey.value = !!data.hasPoolPrivateKey
    }
  } catch (e) {
    console.error('加载项目配置失败', e)
  } finally {
    loading.value = false
  }
}

// 提交配置
const handleSubmit = async () => {
  await formRef.value?.validate()
  
  submitting.value = true
  try {
    const submitData = {
      ...formData.value,
      projectId: projectId.value!
    }
    // 如果没有输入私钥，则不提交该字段（后端判断非空才更新）
    if (!submitData.poolPrivateKey) {
      delete submitData.poolPrivateKey
    }

    await InfoApi.updateConfig(submitData)
    message.success('配置保存成功')
    dialogVisible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

const emit = defineEmits(['success'])
defineExpose({ open })
</script>

<style scoped>
.ml-2 {
  margin-left: 8px;
}
</style>
