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
      <el-form-item label="登录用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入登录用户ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="登录方式：1-密码登录 2-验证码登录" prop="loginType">
        <el-select
          v-model="queryParams.loginType"
          placeholder="请选择登录方式：1-密码登录 2-验证码登录"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="登录时间" prop="loginTime">
        <el-date-picker
          v-model="queryParams.loginTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="登录IP地址" prop="loginIp">
        <el-input
          v-model="queryParams.loginIp"
          placeholder="请输入登录IP地址"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="登录城市（如“中国 香港”）" prop="loginCity">
        <el-input
          v-model="queryParams.loginCity"
          placeholder="请输入登录城市（如“中国 香港”）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="设备信息（可选：如手机型号/浏览器标识）" prop="deviceInfo">
        <el-input
          v-model="queryParams.deviceInfo"
          placeholder="请输入设备信息（可选：如手机型号/浏览器标识）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="登录状态：1-成功 2-失败（失败时可记录原因）" prop="loginStatus">
        <el-select
          v-model="queryParams.loginStatus"
          placeholder="请选择登录状态：1-成功 2-失败（失败时可记录原因）"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.LOGIN_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="登录失败原因（如“密码错误”，登录成功时为空）" prop="failReason">
        <el-input
          v-model="queryParams.failReason"
          placeholder="请输入登录失败原因（如“密码错误”，登录成功时为空）"
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
          v-hasPermi="['user:login-log:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['user:login-log:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="日志ID" align="center" prop="id" />
      <el-table-column label="登录用户ID" align="center" prop="userId" />
      <el-table-column label="登录方式：1-密码登录 2-验证码登录" align="center" prop="loginType" />
      <el-table-column
        label="登录时间"
        align="center"
        prop="loginTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="登录IP地址" align="center" prop="loginIp" />
      <el-table-column label="登录城市（如“中国 香港”）" align="center" prop="loginCity" />
      <el-table-column label="设备信息（可选：如手机型号/浏览器标识）" align="center" prop="deviceInfo" />
      <el-table-column label="登录状态：1-成功 2-失败（失败时可记录原因）" align="center" prop="loginStatus">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.LOGIN_STATUS" :value="scope.row.loginStatus" />
        </template>
      </el-table-column>
      <el-table-column label="登录失败原因（如“密码错误”，登录成功时为空）" align="center" prop="failReason" />
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
            v-hasPermi="['user:login-log:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['user:login-log:delete']"
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
  <LoginLogForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { LoginLogApi, LoginLogVO } from '@/api/user/userloginlog'
import LoginLogForm from './LoginLogForm.vue'

/** 用户登录日志 列表 */
defineOptions({ name: 'UserLoginLog' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<LoginLogVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  loginType: undefined,
  loginTime: [],
  loginIp: undefined,
  loginCity: undefined,
  deviceInfo: undefined,
  loginStatus: undefined,
  failReason: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await LoginLogApi.getLoginLogPage(queryParams)
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
    await LoginLogApi.deleteLoginLog(id)
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
    const data = await LoginLogApi.exportLoginLog(queryParams)
    download.excel(data, '用户登录日志.xls')
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