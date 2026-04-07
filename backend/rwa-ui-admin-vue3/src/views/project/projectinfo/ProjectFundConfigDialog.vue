<template>
  <Dialog :title="'项目配置：' + projectName" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="loading"
    >
      <el-divider content-position="left">运营成本</el-divider>
      
      <el-form-item :label="`月运维成本(${currency})`" prop="operationCost">
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
<!--          <el-radio :value="1">按天计算</el-radio>-->
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
      </el-row>

      <el-form-item label="开启阈值预警" prop="alertEnabled">
        <el-switch
          v-model="formData.alertEnabled"
          :active-value="1"
          :inactive-value="0"
        />
      </el-form-item>
      -->
      <el-divider content-position="left">基金公司配置</el-divider>
      
      <el-form-item label="API详细配置" prop="fundJson">
        <el-input
          v-model="formData.fundJson"
          type="textarea"
          :rows="4"
          placeholder="请以此输入基金公司相关配置信息"
        />
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
import { FundConfigApi, FundConfigVO } from '@/api/project/projectfundconfig'

defineOptions({ name: 'ProjectFundConfigDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const projectId = ref<number>()
const projectName = ref('')
const currency = ref('U')

const formRef = ref()
const formData = ref<FundConfigVO>({
  projectId: 0,
  operationCost: undefined as any,
  operationCostType: 0,
  teamShareRatio: undefined as any,
  thresholdMin: undefined as any,
  thresholdMax: undefined as any,
  alertEnabled: 0,
  fundJson: ''
})

const formRules = reactive({
  operationCost: [{ required: true, message: '运维成本不能为空', trigger: 'blur' }],
  teamShareRatio: [{ required: true, message: '团队分成比例不能为空', trigger: 'blur' }]
})

// 打开弹窗
const open = async (id: number, name: string, investmentCurrency?: string) => {
  dialogVisible.value = true
  projectId.value = id
  projectName.value = name
  currency.value = investmentCurrency || 'U'
  
  // 重置表单
  formData.value = {
    projectId: id,
    operationCost: undefined as any,
    operationCostType: 0,
    teamShareRatio: undefined as any,
    thresholdMin: undefined as any,
    thresholdMax: undefined as any,
    alertEnabled: 0,
    fundJson: ''
  }
  
  // 加载配置
  await loadConfig(id)
}

// 加载配置
const loadConfig = async (id: number) => {
  loading.value = true
  try {
    const data = await FundConfigApi.getFundConfig(id)
    if (data) {
      formData.value = {
        ...formData.value,
        ...data,
        operationCostType: data.operationCostType ?? 0
      }
    }
  } catch (e) {
    console.error('加载基金项目配置失败', e)
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

    // 后端 update 接口已改为 Upsert 逻辑（存在则更新，不存在则插入）
    await FundConfigApi.updateFundConfig(submitData)
    message.success('配置保存成功')
    dialogVisible.value = false
    emit('success')
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    submitting.value = false
  }
}

const emit = defineEmits(['success'])
defineExpose({ open })
</script>

<style scoped>
</style>
