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
      <el-form-item label="模板名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item >
      <el-form-item label="模板编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入模板编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_NOTICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option label="禁用" :value="0" />
          <el-option label="启用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['user:notice-template:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="模板ID" align="center" prop="id" width="80" />
      <el-table-column label="模板名称" align="center" prop="name" min-width="240" />
      <el-table-column label="模板编码" align="center" prop="code" min-width="240" />
<!--      <el-table-column label="模板内容" align="center" prop="content" min-width="200" show-overflow-tooltip />-->
      <el-table-column label="类型" align="center" prop="type" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BIZ_NOTICE_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
<!--      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>-->
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="180px" fixed="right">
        <template #default="scope">
          <el-button

            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['user:notice-template:update']"
          >
            编辑
          </el-button>
          <el-button

            type="success"
            @click="handleSend(scope.row)"
            v-hasPermi="['user:notice-template:send']"
          >
            测试
          </el-button>
          <el-button

            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['user:notice-template:delete']"
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
  <TemplateForm ref="formRef" @success="getList" />

  <!-- 发送测试消息弹窗 -->
  <el-dialog title="发送测试消息" v-model="sendDialogVisible" width="500px">
    <el-form :model="sendForm" label-width="100px">
      <el-form-item label="消息类型">
        <el-radio-group v-model="sendForm.messageType">
          <el-radio :label="1">系统消息</el-radio>
          <el-radio :label="2">个人消息</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="用户ID" v-if="sendForm.messageType === 2">
        <el-input v-model="sendForm.userId" placeholder="请输入用户ID" type="number" />
      </el-form-item>
      <el-form-item label="模板参数">
        <el-input
          v-model="sendForm.templateParamsStr"
          type="textarea"
          :rows="4"
          placeholder='请输入JSON格式参数，如：{"userName":"张三","time":"2024-01-01"}'
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="sendDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitSend">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { NoticeTemplateApi, NoticeTemplateVO } from '@/api/user/noticetemplate'
import TemplateForm from './TemplateForm.vue'
import {DICT_TYPE, getIntDictOptions} from '@/utils/dict'

/** 消息模板 列表 */
defineOptions({ name: 'NoticeTemplate' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<NoticeTemplateVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  code: undefined,
  type: undefined,
  status: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await NoticeTemplateApi.getNoticeTemplatePage(queryParams)
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
    await message.delConfirm()
    await NoticeTemplateApi.deleteNoticeTemplate(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 发送测试消息 */
const sendDialogVisible = ref(false)
const sendForm = reactive({
  templateCode: '',
  messageType: 1,
  userId: undefined,
  templateParamsStr: ''
})

const handleSend = (row: NoticeTemplateVO) => {
  sendForm.templateCode = row.code
  sendForm.messageType = 1
  sendForm.userId = undefined
  sendForm.templateParamsStr = ''
  sendDialogVisible.value = true
}

const submitSend = async () => {
  try {
    let templateParams = {}
    if (sendForm.templateParamsStr) {
      try {
        templateParams = JSON.parse(sendForm.templateParamsStr)
      } catch (e) {
        message.error('模板参数格式错误，请输入正确的JSON格式')
        return
      }
    }

    await NoticeTemplateApi.sendMessage({
      templateCode: sendForm.templateCode,
      messageType: sendForm.messageType,
      userId: sendForm.userId,
      templateParams
    })
    message.success('发送成功')
    sendDialogVisible.value = false
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
