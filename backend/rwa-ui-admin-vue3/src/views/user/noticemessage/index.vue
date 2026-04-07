<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="消息类型" prop="noticeType">
        <el-select
          v-model="queryParams.noticeType"
          placeholder="请选择消息类型"
          clearable
          class="!w-200px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_NOTICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-button type="primary" @click="openSystemMessageForm" v-hasPermi="['user:notice-message:create']">
      <Icon icon="ep:plus" class="mr-5px" /> 发布系统消息
    </el-button>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" class="mt-10px">
      <el-table-column label="消息ID" align="center" prop="id" />
      <el-table-column label="消息类型" align="center" prop="noticeType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_NOTICE_TYPE" :value="scope.row.noticeType" />
        </template>
      </el-table-column>
<!--      <el-table-column label="模版编号" align="center" prop="templateId" />-->
<!--      <el-table-column label="模板编码" align="center" prop="templateCode" />-->
      <el-table-column label="消息标题" align="center" prop="templateTitle" />
<!--      <el-table-column label="模版发送人名称" align="center" prop="templateNickname" />-->
<!--      <el-table-column label="模版内容" align="center" prop="templateContent" />-->
<!--      <el-table-column label="模版类型" align="center" prop="templateType" />
      <el-table-column label="模版参数" align="center" prop="templateParams" />-->
<!--      <el-table-column label="订单id" align="center" prop="orderId" />
      <el-table-column label="消息地址" align="center" prop="noticeUrl" />-->
<!--      <el-table-column label="是否已读" align="center" prop="readStatus">
        <template #default="scope">
          <el-tag v-if="scope.row.readStatus" type="success">已读</el-tag>
          <el-tag v-else type="info">未读</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="阅读时间"
        align="center"
        prop="readTime"
        :formatter="dateFormatter"
        width="180px"
      />-->
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

            type="primary"
            @click="openForm('view', scope.row.id)"
            v-hasPermi="['user:notice-message:query']"
          >
            查看
          </el-button>
          <el-button

            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['user:notice-message:delete']"
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
  <NoticeMessageForm ref="formRef" @success="getList" />
  <!-- 发布系统消息弹窗 -->
  <SystemMessageForm ref="systemMessageFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { NoticeMessageApi, NoticeMessageVO } from '@/api/user/noticemessage'
import NoticeMessageForm from './NoticeMessageForm.vue'
import SystemMessageForm from './SystemMessageForm.vue'

/** 用户消息 列表 */
defineOptions({ name: 'NoticeMessage' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<NoticeMessageVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  noticeType: undefined,
  templateId: undefined,
  templateCode: undefined,
  templateNickname: undefined,
  templateContent: undefined,
  templateType: undefined,
  templateParams: undefined,
  orderId: undefined,
  noticeUrl: undefined,
  readStatus: undefined,
  readTime: [],
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await NoticeMessageApi.getNoticeMessagePage(queryParams)
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

/** 发布系统消息 */
const systemMessageFormRef = ref()
const openSystemMessageForm = () => {
  systemMessageFormRef.value.open()
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await NoticeMessageApi.deleteNoticeMessage(id)
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
    const data = await NoticeMessageApi.exportNoticeMessage(queryParams)
    download.excel(data, '用户消息.xls')
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
