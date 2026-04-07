<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <div v-loading="loading">
      <!-- 工具栏 -->
      <div class="mb-10px">
        <el-button type="primary" @click="handleAdd">
          <Icon icon="ep:plus" class="mr-5px" /> 新增分红期
        </el-button>
      </div>

      <!-- 分红期列表 -->
      <el-table :data="list" :stripe="true" border style="width: 100%">
        <el-table-column label="分红期数" align="center" width="120">
          <template #default="scope">
            第 {{ scope.row.periodSeq }} 期
          </template>
        </el-table-column>
        <el-table-column label="分红日期" align="center" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.unlockDate) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="isUnlocked(scope.row.unlockDate) ? 'success' : 'warning'">
              {{ isUnlocked(scope.row.unlockDate) ? '已开始' : '未开始' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <el-button
              v-if="!isUnlocked(scope.row.unlockDate)"
              link
              type="primary"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <span v-else class="text-gray-400">已开始不可编辑</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="list.length === 0" description="暂无分红期配置" />
    </div>

    <!-- 编辑表单弹窗 -->
    <Dialog :title="formTitle" v-model="formVisible" width="400px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="分红期数" prop="periodSeq">
          <el-input-number
            v-model="formData.periodSeq"
            :min="1"
            placeholder="请输入分红期数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="解锁日期" prop="unlockDate">
          <el-date-picker
            v-model="formData.unlockDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择解锁日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="formLoading">确 定</el-button>
      </template>
    </Dialog>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DividendPeriodApi, DividendPeriodVO } from '@/api/project/dividendperiod'

defineOptions({ name: 'DividendPeriodDialog' })

const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const loading = ref(false)
const list = ref<DividendPeriodVO[]>([])
const projectId = ref<number>()

// 表单相关
const formVisible = ref(false)
const formTitle = ref('')
const formLoading = ref(false)
const formType = ref<'create' | 'update'>('create')
const formRef = ref()
const formData = ref<DividendPeriodVO>({
  projectId: 0,
  periodSeq: 1,
  unlockDate: ''
})
const formRules = reactive({
  periodSeq: [{ required: true, message: '请输入分红期数', trigger: 'blur' }],
  unlockDate: [{ required: true, message: '请选择解锁日期', trigger: 'change' }]
})

/** 格式化日期：将 [year, month, day] 数组转换为 YYYY-MM-DD 字符串 */
const formatDate = (unlockDate: number[] | string): string => {
  if (!unlockDate) return ''
  if (Array.isArray(unlockDate)) {
    const [year, month, day] = unlockDate
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  }
  return String(unlockDate)
}

/** 判断是否已开始分红 */
const isUnlocked = (unlockDate: number[] | string): boolean => {
  if (!unlockDate) return false
  let dateStr: string
  if (Array.isArray(unlockDate)) {
    const [year, month, day] = unlockDate
    dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  } else {
    dateStr = String(unlockDate)
  }
  return new Date(dateStr) <= new Date()
}

/** 打开弹窗 */
const open = async (id: number, projectName: string) => {
  dialogVisible.value = true
  dialogTitle.value = `分红配置 - ${projectName}`
  projectId.value = id
  await loadList()
}

/** 加载列表 */
const loadList = async () => {
  loading.value = true
  try {
    const res = await DividendPeriodApi.getDividendPeriodPage({
      projectId: projectId.value,
      pageNo: 1,
      pageSize: 100
    })
    list.value = res.list || []
    // 按期数排序
    list.value.sort((a, b) => (a.periodSeq || 0) - (b.periodSeq || 0))
  } finally {
    loading.value = false
  }
}

/** 新增 */
const handleAdd = () => {
  formType.value = 'create'
  formTitle.value = '新增分红期'
  formData.value = {
    projectId: projectId.value as number,
    periodSeq: list.value.length + 1,
    unlockDate: ''
  }
  formVisible.value = true
}

/** 编辑 */
const handleEdit = (row: DividendPeriodVO) => {
  formType.value = 'update'
  formTitle.value = '编辑分红期'
  // 将日期数组格式转换为字符串格式
  const unlockDate = Array.isArray(row.unlockDate) 
    ? formatDate(row.unlockDate as any) 
    : row.unlockDate
  formData.value = { ...row, unlockDate }
  formVisible.value = true
}

/** 删除 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await DividendPeriodApi.deleteDividendPeriod(id)
    message.success('删除成功')
    await loadList()
  } catch {}
}

/** 提交表单 */
const submitForm = async () => {
  await formRef.value?.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await DividendPeriodApi.createDividendPeriod(formData.value)
      message.success('新增成功')
    } else {
      await DividendPeriodApi.updateDividendPeriod(formData.value)
      message.success('修改成功')
    }
    formVisible.value = false
    await loadList()
  } finally {
    formLoading.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.text-gray-400 {
  color: #9ca3af;
  font-size: 12px;
}
</style>
