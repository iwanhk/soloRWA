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

     <el-form-item label="项目" prop="projectId">
        <el-select
          v-model="queryParams.projectId"
          placeholder="请选择项目"
          clearable
          filterable
          class="!w-240px"
          @change="handleQuery"
        >
          <el-option
            v-for="item in projectList"
            :key="item.projectId"
            :label="item.projectName"
            :value="item.projectId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="通告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入通告标题"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
<!--      <el-form-item label="通告类型" prop="noticeType">
        <el-select
          v-model="queryParams.noticeType"
          placeholder="请选择通告类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_NOTICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
      <el-form-item label="通告状态" prop="noticeStatus">
        <el-select
          v-model="queryParams.noticeStatus"
          placeholder="请选择通告状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_NOTICE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['project:notice:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['project:notice:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="通告ID" align="center" prop="id" />
<!--      <el-table-column label="通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）" align="center" prop="noticeNo" />
      <el-table-column label="关联项目ID（0表示全局通告，关联project_core.id）" align="center" prop="projectId" />-->
      <el-table-column label="项目名称" align="center" prop="projectName" />
      <el-table-column label="通告标题" align="center" prop="noticeTitle" />
<!--      <el-table-column label="通告类型" align="center" prop="noticeType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_PROJECT_NOTICE_TYPE" :value="scope.row.noticeType" />
        </template>
      </el-table-column>-->
<!--      <el-table-column label="通告内容" align="center" prop="noticeContent" />-->
<!--      <el-table-column label="附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）" align="center" prop="attachUrls" />-->
<!--      <el-table-column label="发布人ID（关联sys_user.id）" align="center" prop="publishUserId" />-->
      <el-table-column label="发布人" align="center" prop="publishUserName" />
      <el-table-column
        label="发布时间"
        align="center"
        prop="publishTime"
        :formatter="dateFormatter"
        width="180px"
      />
<!--      <el-table-column
        label="开始展示时间（NULL表示立即展示）"
        align="center"
        prop="showStartTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column
        label="结束展示时间（NULL表示永久展示）"
        align="center"
        prop="showEndTime"
        :formatter="dateFormatter"
        width="180px"
      />-->
     <!-- <el-table-column label="是否置顶：1-是 0-否（同一项目/全局仅1个置顶）" align="center" prop="isTop" /> -->
      <el-table-column label="通告状态" align="center" prop="noticeStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_PROJECT_NOTICE_STATUS" :value="scope.row.noticeStatus" />
        </template>
      </el-table-column>
<!--      <el-table-column label="阅读次数" align="center" prop="readCount" />-->
<!--      <el-table-column label="是否弹窗展示：1-是 0-否（用户进入页面时弹窗）" align="center" prop="isPopup" />-->
<!--      <el-table-column label="备注（仅运营可见，如“临时通告，3天后下架”）" align="center" prop="remark" />-->
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
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['project:notice:update']"
          >
            编辑
          </el-button>
          <el-button

            type="success"
            @click="handleStatus(scope.row.id, 2)"
            v-if="scope.row.noticeStatus === 1 || scope.row.noticeStatus === 3"
            v-hasPermi="['project:notice:update']"
          >
            发布
          </el-button>
          <el-button

            type="warning"
            @click="handleStatus(scope.row.id, 3)"
            v-if="scope.row.noticeStatus === 2"
            v-hasPermi="['project:notice:update']"
          >
            撤销
          </el-button>
          <el-button

            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['project:notice:delete']"
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
  <NoticeForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { NoticeApi, NoticeVO } from '@/api/project/projectnotice'
import NoticeForm from './NoticeForm.vue'
import { useRoute } from 'vue-router'
import { InfoApi, InfoSimpleVO } from '@/api/project/projectinfo'

/** 项目通告表（含全局通告） 列表 */
defineOptions({ name: 'ProjectNotice' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<NoticeVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const projectList = ref<InfoSimpleVO[]>([]) // 项目列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  noticeNo: undefined,
  projectId: undefined,
  projectName: undefined,
  noticeTitle: undefined,
  noticeType: undefined,
  noticeContent: undefined,
  attachUrls: undefined,
  publishUserId: undefined,
  publishUserName: undefined,
  publishTime: [],
  showStartTime: [],
  showEndTime: [],
  isTop: undefined,
  noticeStatus: undefined,
  readCount: undefined,
  isPopup: undefined,
  remark: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

const route = useRoute() // 路由

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await NoticeApi.getNoticePage(queryParams)
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
  formRef.value.open(type, id, queryParams.projectId)
}

/** 更改通告状态操作 */
const handleStatus = async (id: number, status: number) => {
  try {
    const statusName = status === 2 ? '发布' : '撤销'
    // 二次确认
    await message.confirm(`是否确认${statusName}该通告?`)
    // 发起修改
    const data = { id, noticeStatus: status } as unknown as NoticeVO
    await NoticeApi.updateNotice(data)
    message.success(t('common.updateSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await NoticeApi.deleteNotice(id)
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
    const data = await NoticeApi.exportNotice(queryParams)
    download.excel(data, '项目通告表（含全局通告）.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化时，如果路由参数中有projectId，则自动填入并搜索 */
onMounted(async () => {
  projectList.value = await InfoApi.getInfoSimple()
  if (route.query.projectId) {
    queryParams.projectId = Number(route.query.projectId)
    getList()
  } else {
    getList()
  }
})
</script>
