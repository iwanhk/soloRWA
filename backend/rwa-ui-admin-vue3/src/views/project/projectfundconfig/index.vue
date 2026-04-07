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
      <el-form-item label="月运维成本" prop="operationCost">
        <el-input
          v-model="queryParams.operationCost"
          placeholder="请输入月运维成本"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="运维成本类型:0-固定值/月 1-按天" prop="operationCostType">
        <el-select
          v-model="queryParams.operationCostType"
          placeholder="请选择运维成本类型:0-固定值/月 1-按天"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="团队分成比例(%)" prop="teamShareRatio">
        <el-input
          v-model="queryParams.teamShareRatio"
          placeholder="请输入团队分成比例(%)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="最低收益阈值(日)" prop="thresholdMin">
        <el-input
          v-model="queryParams.thresholdMin"
          placeholder="请输入最低收益阈值(日)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="最高收益阈值(日)" prop="thresholdMax">
        <el-input
          v-model="queryParams.thresholdMax"
          placeholder="请输入最高收益阈值(日)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="阈值预警:0-关 1-开" prop="alertEnabled">
        <el-input
          v-model="queryParams.alertEnabled"
          placeholder="请输入阈值预警:0-关 1-开"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="基金公司配置" prop="fundJson">
        <el-input
          v-model="queryParams.fundJson"
          placeholder="请输入基金公司配置"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:fund-config:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:fund-config:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="项目ID(主键)" align="center" prop="projectId" />
      <el-table-column label="月运维成本" align="center" prop="operationCost" />
      <el-table-column label="运维成本类型:0-固定值/月 1-按天" align="center" prop="operationCostType" />
      <el-table-column label="团队分成比例(%)" align="center" prop="teamShareRatio" />
      <el-table-column label="最低收益阈值(日)" align="center" prop="thresholdMin" />
      <el-table-column label="最高收益阈值(日)" align="center" prop="thresholdMax" />
      <el-table-column label="阈值预警:0-关 1-开" align="center" prop="alertEnabled" />
      <el-table-column label="基金公司配置" align="center" prop="fundJson" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:fund-config:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:fund-config:delete']"
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
  <FundConfigForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { FundConfigApi, FundConfigVO } from '@/api/project/projectfundconfig'
import FundConfigForm from './FundConfigForm.vue'

/** 基金项目配置 列表 */
defineOptions({ name: 'ProjectFundConfig' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<FundConfigVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  operationCost: undefined,
  operationCostType: undefined,
  teamShareRatio: undefined,
  thresholdMin: undefined,
  thresholdMax: undefined,
  alertEnabled: undefined,
  fundJson: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FundConfigApi.getFundConfigPage(queryParams)
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
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await FundConfigApi.deleteFundConfig(id)
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
    const data = await FundConfigApi.exportFundConfig(queryParams)
    download.excel(data, '基金项目配置.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>