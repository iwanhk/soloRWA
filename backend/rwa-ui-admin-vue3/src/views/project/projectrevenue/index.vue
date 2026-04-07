<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="收益日期" prop="revenueDate">
        <el-date-picker
          v-model="queryParams.revenueDate"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="是否发放" prop="isSend">
        <el-select
          v-model="queryParams.isSend"
          placeholder="请选择是否发放收益"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_INCOME_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!--      <el-form-item label="是否同步" prop="isSync">
        <el-select
          v-model="queryParams.isSync"
          placeholder="请选择是否同步收益"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>-->
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <!--        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:revenue:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>-->
        <!--        <el-button type="warning" plain @click="openManualForm">
          <Icon icon="ep:magic-stick" class="mr-5px" /> 手动模拟生成
        </el-button>-->
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:revenue:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" width="80" />

      <el-table-column
        label="收益日期"
        align="center"
        prop="revenueDate"
        :formatter="dateFormatter2"
        width="180px"
      />
      <el-table-column label="产品名称" align="center" prop="projectName" min-width="150" />
      <el-table-column label="总收益" align="center" prop="revenue" width="140">
        <template #default="scope">
          <span class="text-primary font-bold">
            {{ formatMoney(scope.row.coinRevenue) }} {{ scope.row.coinCode || '' }}
          </span>
        </template>
      </el-table-column>
      <!--      <el-table-column label="电力成本" align="center" prop="electricityCost" width="100" />
      <el-table-column label="人力成本" align="center" prop="peopleCost" width="100" />-->
      <el-table-column label="可发放收益" align="center" prop="availableRevenue" width="140">
        <template #default="scope">
          <span class="text-success font-bold">
            {{ formatMoney(scope.row.availableRevenue) }} {{ scope.row.coinCode || '' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="发放状态" align="center" prop="isSend" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isSend === 1 ? 'success' : 'warning'">
            {{ scope.row.isSend === 1 ? '已发放' : '未发放' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="120" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="170"
      />
      <el-table-column label="操作" align="center" min-width="120" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:revenue:update']"
            v-if="scope.row.isSend === 0"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:revenue:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <RevenueForm ref="formRef" @success="getList" />
  <RevenueFundForm ref="fundFormRef" @success="getList" />
  <RevenueManualForm ref="manualFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter, dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import { RevenueApi, RevenueVO } from '@/api/project/projectrevenue'
import { InfoApi } from '@/api/project/projectinfo'
import RevenueForm from './RevenueForm.vue'
import RevenueFundForm from './RevenueFundForm.vue'
import RevenueManualForm from './RevenueManualForm.vue'
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'

/** 格式化金额，最多保留8位小数 */
const formatMoney = (val: any) => {
  if (val === undefined || val === null || val === '') return '0'
  return parseFloat(Number(val).toFixed(8)).toString()
}

/** 项目收益 列表 */
defineOptions({ name: 'ProjectRevenue' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<RevenueVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const projectList = ref<any[]>([]) // 项目简易列表

/** 加载项目列表 */
const loadProjectList = async () => {
  projectList.value = await InfoApi.getInfoSimple()
}

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  revenueDate: [],
  revenue: undefined,
  projectId: undefined,
  projectName: undefined,
  isSend: undefined,
  isSync: undefined,
  remark: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await RevenueApi.getRevenuePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const fundFormRef = ref()
const manualFormRef = ref()
const openManualForm = () => {
  manualFormRef.value.open()
}
const openForm = async (type: string, id?: number) => {
  if (id) {
    // 获取收益详情以确定项目类型
    const revenue = await RevenueApi.getRevenue(id)
    const project = projectList.value.find((p) => p.projectId === revenue.projectId)
    if (project?.projectConfigType === 1) {
      fundFormRef.value.open(type, id)
    } else {
      formRef.value.open(type, id)
    }
  } else {
    // 新增时目前默认打开挖矿型，或者可以进一步优化
    formRef.value.open(type)
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await RevenueApi.deleteRevenue(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await RevenueApi.exportRevenue(queryParams)
    download.excel(data, '项目收益.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  loadProjectList()
})
</script>
