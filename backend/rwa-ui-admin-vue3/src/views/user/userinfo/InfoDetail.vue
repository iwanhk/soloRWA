<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="60%">
    <el-tabs v-model="activeTab">
      <!-- 基础信息 -->
      <el-tab-pane label="基础信息" name="basic">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户头像">
            <el-image
              v-if="formData.avatar"
              class="h-40px w-40px rounded-full"
              :src="formData.avatar"
              :preview-src-list="[formData.avatar]"
              preview-teleported
              fit="cover"
            />
            <span v-else>暂无</span>
          </el-descriptions-item>
          <el-descriptions-item label="用户昵称">{{ formData.nickName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ formData.mobile }}</el-descriptions-item>
          <el-descriptions-item label="用户姓名">{{ formData.realName }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ formData.idCard }}</el-descriptions-item>
          <el-descriptions-item label="身份证有效期">{{ formData.idCardExpire }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ formData.email }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ formData.phone }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="formData.auditStatus" v-if="formData.auditStatus !== undefined" />
          </el-descriptions-item>
          <el-descriptions-item label="2FA状态">
            <dict-tag :type="DICT_TYPE.F2A_STATUS" :value="formData.twoFactorAuthStatus" v-if="formData.twoFactorAuthStatus !== undefined" />
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <!-- 审核记录 -->
      <el-tab-pane label="审核信息" name="audit">
        <el-table :data="auditList" border stripe>
          <el-table-column label="审核记录ID" prop="id" width="100" />
          <el-table-column label="用户姓名" prop="realName" />
          <el-table-column label="审核状态" prop="auditStatus">
            <template #default="scope">
              <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
            </template>
          </el-table-column>
           <el-table-column label="审核备注" prop="auditRemark" />
          <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
          <el-table-column label="操作" align="center" width="100">
            <template #default="scope">
              <el-button link type="primary" @click="openAuditDetail(scope.row)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 银行卡信息 -->
<!--      <el-tab-pane label="银行卡信息" name="bank">
        <el-table :data="bankList" border stripe>
          <el-table-column label="银行" prop="bankName" />
          <el-table-column label="卡号" prop="bankAccount" />
          <el-table-column label="开户名" prop="bankAccountName" />
          <el-table-column label="状态" prop="auditStatus">
            <template #default="scope">
               <dict-tag :type="DICT_TYPE.AUDIT_STATUS" :value="scope.row.auditStatus" />
            </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
        </el-table>
      </el-tab-pane>-->
      <!-- 链地址信息 -->
      <el-tab-pane label="链地址信息" name="chain">
        <el-table :data="chainList" border stripe>

          <el-table-column label="链地址" prop="chainAddress" />
          <el-table-column label="状态" prop="chainStatus">
             <template #default="scope">
               <dict-tag :type="DICT_TYPE.BIZ_USER_CHAIN_STATUS" :value="scope.row.chainStatus" />
             </template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" :formatter="dateFormatter" width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
  <UserAuditReviewDialog ref="auditDetailRef" :readonly="true" />
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { InfoApi, InfoVO } from '@/api/user/userinfo'
import { AuditApi } from '@/api/user/useraudit'
import { ChainApi as UserChainApi } from '@/api/user/userchain'
import { BankApi } from '@/api/user/userbank'
import { dateFormatter } from '@/utils/formatTime'
import UserAuditReviewDialog from './UserAuditReviewDialog.vue'

defineOptions({ name: 'InfoDetail' })

const dialogVisible = ref(false)
const dialogTitle = ref('用户详情')
const formData = ref<Partial<InfoVO>>({})
const activeTab = ref('basic')
const auditList = ref<any[]>([])
const bankList = ref<any[]>([])
const chainList = ref<any[]>([])

const auditDetailRef = ref()
const openAuditDetail = (row: any) => {
  auditDetailRef.value.open(row.id, 'audit')
}

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  activeTab.value = 'basic'
  formData.value = {}
  auditList.value = []
  bankList.value = []
  
  if (id) {
    formData.value = await InfoApi.getInfo(id)
    
    // 获取审核记录
    const auditRes = await AuditApi.getAuditPage({ userId: id, pageSize: 20 })
    auditList.value = auditRes.list
    
    // 获取银行卡
    const bankRes = await BankApi.getBankPage({ userId: id, pageSize: 20 })
    bankList.value = bankRes.list
    
    // 获取链地址
    const chainRes = await UserChainApi.getChainPage({ userId: id, pageSize: 20 })
    chainList.value = chainRes.list
  }
}

defineExpose({ open })
</script>
