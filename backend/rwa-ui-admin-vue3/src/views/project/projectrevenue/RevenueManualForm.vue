<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="项目" prop="projectId">
        <el-select
          v-model="formData.projectId"
          placeholder="请选择项目"
          filterable
          style="width: 100%"
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
        />
      </el-form-item>
      
      <el-form-item label="总收益(Fiat)" prop="revenue">
        <el-input-number
          v-model="formData.revenue"
          :precision="2"
          :min="0"
          placeholder="请输入该日总收益"
          style="width: 100%"
        />
      </el-form-item>
      
      <div style="color: #909399; font-size: 12px; margin-left: 100px; margin-bottom: 20px;">
        * 此操作将根据项目配置模拟生成当日收益记录，包含自动计算的各项成本。
      </div>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :loading="formLoading">确 定 生 成</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { RevenueApi } from '@/api/project/projectrevenue'
import { InfoApi } from '@/api/project/projectinfo'

/** 手动生成收益试模拟表单 */
defineOptions({ name: 'RevenueManualForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('手动模拟生成收益') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中

const formData = ref({
  projectId: undefined,
  revenueDate: undefined,
  revenue: 0
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

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
  await loadProjectList()
}
defineExpose({ open })

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    await RevenueApi.generateManualRevenue(formData.value as any)
    message.success('模拟生成成功')
    dialogVisible.value = false
    emit('success')
  } catch (e) {
    console.error('生成失败', e)
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    projectId: undefined,
    revenueDate: undefined,
    revenue: 0
  }
  formRef.value?.resetFields()
}
</script>
