<template>
<!--  <ContentWrap>
    &lt;!&ndash; 搜索工作栏 &ndash;&gt;
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="币种标识" prop="coinCode">
        <el-input
          v-model="queryParams.coinCode"
          placeholder="请输入币种标识"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="币种名称" prop="coinName">
        <el-input
          v-model="queryParams.coinName"
          placeholder="请输入币种名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="英文名称" prop="coinNameEn">
        <el-input
          v-model="queryParams.coinNameEn"
          placeholder="请输入英文名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="币种类型" prop="coinType">
        <el-select
          v-model="queryParams.coinType"
          placeholder="请选择币种类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_COIN_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="显示符号" prop="symbol">
        <el-input
          v-model="queryParams.symbol"
          placeholder="请输入显示符号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="币安交易对符号(如BTCUSDT)" prop="binanceSymbol">
        <el-input
          v-model="queryParams.binanceSymbol"
          placeholder="请输入币安交易对符号(如BTCUSDT)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="矿池币种符号(如btc)" prop="poolSymbol">
        <el-input
          v-model="queryParams.poolSymbol"
          placeholder="请输入矿池币种符号(如btc)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="币种图标" prop="iconUrl">
        <el-input
          v-model="queryParams.iconUrl"
          placeholder="请输入币种图标"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="小数精度" prop="decimals">
        <el-input
          v-model="queryParams.decimals"
          placeholder="请输入小数精度"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="排序(越小越前)" prop="sort">
        <el-input
          v-model="queryParams.sort"
          placeholder="请输入排序(越小越前)"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态:0-禁用 1-启用" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态:0-禁用 1-启用"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          placeholder="请输入备注"
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
          v-hasPermi="['project:system-coin:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:system-coin:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>-->

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="币种标识" align="center" prop="coinCode" />
      <el-table-column label="币种名称" align="center" prop="coinName" />
<!--      <el-table-column label="英文名称" align="center" prop="coinNameEn" />-->
      <el-table-column label="币种类型" align="center" prop="coinType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_COIN_TYPE" :value="scope.row.coinType" />
        </template>
      </el-table-column>
      <el-table-column label="显示符号" align="center" prop="symbol" />
<!--      <el-table-column label="币安交易对符号(如BTCUSDT)" align="center" prop="binanceSymbol" />
      <el-table-column label="矿池币种符号(如btc)" align="center" prop="poolSymbol" />
      <el-table-column label="币种图标" align="center" prop="iconUrl" />
      <el-table-column label="小数精度" align="center" prop="decimals" />
      <el-table-column label="排序(越小越前)" align="center" prop="sort" />
      <el-table-column label="状态:0-禁用 1-启用" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />-->
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:system-coin:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:system-coin:delete']"
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
  <SystemCoinForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { SystemCoinApi, SystemCoinVO } from '@/api/project/systemcoin'
import SystemCoinForm from './SystemCoinForm.vue'

/** 币种管理 列表 */
defineOptions({ name: 'SystemCoin' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<SystemCoinVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  coinCode: undefined,
  coinName: undefined,
  coinNameEn: undefined,
  coinType: undefined,
  symbol: undefined,
  binanceSymbol: undefined,
  poolSymbol: undefined,
  iconUrl: undefined,
  decimals: undefined,
  sort: undefined,
  status: undefined,
  remark: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SystemCoinApi.getSystemCoinPage(queryParams)
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
    await SystemCoinApi.deleteSystemCoin(id)
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
    const data = await SystemCoinApi.exportSystemCoin(queryParams)
    download.excel(data, '币种管理.xls')
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
