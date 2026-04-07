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
      <el-form-item label="用户手机号（冗余sys_user.phonenumber）" prop="userPhone">
        <el-input
          v-model="queryParams.userPhone"
          placeholder="请输入用户手机号（冗余sys_user.phonenumber）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="注册时间（冗余sys_user.create_time）" prop="registerTime">
        <el-date-picker
          v-model="queryParams.registerTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败" prop="identityAuthStatus">
        <el-select
          v-model="queryParams.identityAuthStatus"
          placeholder="请选择身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败"
          clearable
          class="!w-240px"
        >
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="认证身份（如“企业法人”“经办人”）" prop="authIdentity">
        <el-input
          v-model="queryParams.authIdentity"
          placeholder="请输入认证身份（如“企业法人”“经办人”）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="公司名称（企业全称，与营业执照一致）" prop="companyName">
        <el-input
          v-model="queryParams.companyName"
          placeholder="请输入公司名称（企业全称，与营业执照一致）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="公司统一社会信用代码（唯一，18位）" prop="companyCreditCode">
        <el-input
          v-model="queryParams.companyCreditCode"
          placeholder="请输入公司统一社会信用代码（唯一，18位）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="营业执照URL（图片/文件）" prop="businessLicenseUrl">
        <el-input
          v-model="queryParams.businessLicenseUrl"
          placeholder="请输入营业执照URL（图片/文件）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="资质文件URL（多个用,分隔）" prop="qualificationFileUrls">
        <el-input
          v-model="queryParams.qualificationFileUrls"
          placeholder="请输入资质文件URL（多个用,分隔）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="授权文件URL（多个用,分隔）" prop="authorizationFileUrls">
        <el-input
          v-model="queryParams.authorizationFileUrls"
          placeholder="请输入授权文件URL（多个用,分隔）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份证姓名" prop="idCardName">
        <el-input
          v-model="queryParams.idCardName"
          placeholder="请输入身份证姓名"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份证有效期" prop="idCardExpireTime">
        <el-date-picker
          v-model="queryParams.idCardExpireTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="身份证正面URL" prop="idCardFrontUrl">
        <el-input
          v-model="queryParams.idCardFrontUrl"
          placeholder="请输入身份证正面URL"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="身份证背面URL" prop="idCardBackUrl">
        <el-input
          v-model="queryParams.idCardBackUrl"
          placeholder="请输入身份证背面URL"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="queryParams.email"
          placeholder="请输入邮箱"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="开户名（与公司名称/法人姓名一致）" prop="bankAccountName">
        <el-input
          v-model="queryParams.bankAccountName"
          placeholder="请输入开户名（与公司名称/法人姓名一致）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="银行账户（卡号）" prop="bankAccount">
        <el-input
          v-model="queryParams.bankAccount"
          placeholder="请输入银行账户（卡号）"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input
          v-model="queryParams.bankName"
          placeholder="请输入开户行"
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
          v-hasPermi="['system:publisher-info:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['system:publisher-info:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="发行商ID （sys_user.id，发行商对应的用户账号）" align="center" prop="id" />
      <el-table-column label="用户手机号（冗余sys_user.phonenumber）" align="center" prop="userPhone" />
      <el-table-column
        label="注册时间（冗余sys_user.create_time）"
        align="center"
        prop="registerTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败" align="center" prop="identityAuthStatus" />
      <el-table-column label="认证身份（如“企业法人”“经办人”）" align="center" prop="authIdentity" />
      <el-table-column label="公司名称（企业全称，与营业执照一致）" align="center" prop="companyName" />
      <el-table-column label="公司统一社会信用代码（唯一，18位）" align="center" prop="companyCreditCode" />
      <el-table-column label="营业执照URL（图片/文件）" align="center" prop="businessLicenseUrl" />
      <el-table-column label="资质文件URL（多个用,分隔）" align="center" prop="qualificationFileUrls" />
      <el-table-column label="授权文件URL（多个用,分隔）" align="center" prop="authorizationFileUrls" />
      <el-table-column label="身份证姓名" align="center" prop="idCardName" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" />
      <el-table-column label="身份证有效期" align="center" prop="idCardExpireTime" />
      <el-table-column label="身份证正面URL" align="center" prop="idCardFrontUrl" />
      <el-table-column label="身份证背面URL" align="center" prop="idCardBackUrl" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="开户名（与公司名称/法人姓名一致）" align="center" prop="bankAccountName" />
      <el-table-column label="银行账户（卡号）" align="center" prop="bankAccount" />
      <el-table-column label="开户行" align="center" prop="bankName" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="180px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openViewForm(scope.row.id)"
            v-hasPermi="['system:publisher-info:query']"
          >
            查看
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleRole(scope.row)"
            v-hasPermi="['system:permission:assign-user-role']"
          >
            分配角色
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['system:publisher-info:delete']"
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
  <PublisherInfoForm ref="formRef" @success="getList" />
  <!-- 表单弹窗：查看 -->
  <PublisherInfoViewForm ref="viewFormRef" />
  <!-- 分配角色 -->
  <PublisherAssignRoleForm ref="assignRoleFormRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { PublisherInfoApi, PublisherInfoVO } from '@/api/system/publisherinfo'
import PublisherInfoForm from './PublisherInfoForm.vue'
import PublisherInfoViewForm from './PublisherInfoViewForm.vue'
import PublisherAssignRoleForm from './PublisherAssignRoleForm.vue'

/** 发行商 列表 */
defineOptions({ name: 'PublisherInfo' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<PublisherInfoVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userPhone: undefined,
  registerTime: [],
  identityAuthStatus: undefined,
  authIdentity: undefined,
  companyName: undefined,
  companyCreditCode: undefined,
  businessLicenseUrl: undefined,
  qualificationFileUrls: undefined,
  authorizationFileUrls: undefined,
  idCardName: undefined,
  idCardNo: undefined,
  idCardExpireTime: [],
  idCardFrontUrl: undefined,
  idCardBackUrl: undefined,
  email: undefined,
  bankAccountName: undefined,
  bankAccount: undefined,
  bankName: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PublisherInfoApi.getPublisherInfoPage(queryParams)
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

/** 查看操作 */
const viewFormRef = ref()
const openViewForm = (id: number) => {
  viewFormRef.value.open(id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await PublisherInfoApi.deletePublisherInfo(id)
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
    const data = await PublisherInfoApi.exportPublisherInfo(queryParams)
    download.excel(data, '发行商.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 分配角色 */
const assignRoleFormRef = ref()
const handleRole = (row: PublisherInfoVO) => {
  assignRoleFormRef.value.open(row)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>