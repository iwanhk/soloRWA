<template>
  <div class="project-space">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-page-header @back="handleBack">
        <template #content>
          <div class="header-title">
            <span class="project-name">{{ currentProjectData.projectName }}</span>
            <el-tag type="info" class="ml-10px" v-if="latestSnapshot"
              >v{{ projectData.version }}</el-tag
            >
            <el-tag :type="getStatusTagType(projectData.projectStatus)" class="ml-10px">
              {{ getStatusLabel(projectData.projectStatus) }}
              <el-tooltip
                v-if="projectData.projectStatus === 3"
                :content="projectData.auditRemark || '无审核备注'"
                placement="top"
              >
                <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
              </el-tooltip>
            </el-tag>
            <el-tag :type="getAuditStatusTagType(projectData.auditStatus)" class="ml-10px">
              {{ getAuditStatusLabel(projectData.auditStatus) }}
              <el-tooltip
                v-if="projectData.auditStatus === 3"
                :content="projectData.auditRemark || '无审核备注'"
                placement="top"
              >
                <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
              </el-tooltip>
            </el-tag>
          </div>
        </template>
        <template #extra>
          <div class="flex items-center">
            <el-select
              v-model="currentLang"
              class="mr-10px"
              style="width: 120px"
              v-if="langList.length > 1"
            >
              <el-option
                v-for="lang in langList"
                :key="lang"
                :label="dictLangLabel(lang)"
                :value="lang"
              />
            </el-select>
            <el-button type="primary" plain @click="handleRefresh">
              <Icon icon="ep:refresh" class="mr-5px" />刷新
            </el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <!-- 快照状态提示 -->
    <div class="snapshot-alert" v-if="showSnapshotAlert">
      <el-alert :type="snapshotAlertType" :closable="false" show-icon>
        <template #title>
          <span v-if="latestSnapshot.snapshotStatus === 1">
            ⏳ 您的 v{{ latestSnapshot.snapshotVersion }}版本正在审核中，请耐心等待
          </span>
          <span
            v-else-if="latestSnapshot.snapshotStatus === 2 && latestSnapshot.snapshotVersion > 1"
          >
            ✅ 您的编辑已通过审核并生效（版本 v{{ latestSnapshot.snapshotVersion }}）
          </span>
          <span v-else-if="latestSnapshot.snapshotStatus === 3">
            ❌ 您的编辑未通过审核（版本 v{{ latestSnapshot.snapshotVersion }}）
            <el-button type="text" @click="showSnapshotDetail(latestSnapshot.id)" class="ml-10px"
              >查看详情</el-button
            >
          </span>
        </template>
        <div v-if="latestSnapshot.auditRemark" class="mt-5px">
          审核意见：{{ latestSnapshot.auditRemark }}
        </div>
      </el-alert>
    </div>

    <!-- 项目概览 -->
    <div class="project-overview" v-loading="loading">
      <el-row :gutter="24">
        <!-- 左侧：项目封面 -->
        <el-col :span="8">
          <el-card class="cover-card" shadow="hover">
            <el-tabs v-model="activeMediaTab" class="media-tabs">
              <!-- 图片Tab -->
              <el-tab-pane label="图片" name="images" v-if="projectImages.length > 0">
                <el-carousel :interval="5000" height="400px" arrow="never">
                  <el-carousel-item v-for="(img, index) in projectImages" :key="index">
                    <el-image
                      :src="img"
                      fit="contain"
                      class="carousel-image"
                      :preview-src-list="projectImages"
                      :initial-index="index"
                      :hide-on-click-modal="true"
                    >
                      <template #error>
                        <div class="image-placeholder">
                          <Icon icon="ep:picture" :size="48" />
                          <span>图片加载失败</span>
                        </div>
                      </template>
                    </el-image>
                  </el-carousel-item>
                </el-carousel>
              </el-tab-pane>

              <!-- 视频Tab -->
              <el-tab-pane
                label="视频"
                name="video"
                v-if="projectData.projectVideoUrl && projectData.projectVideoUrl.trim()"
              >
                <div class="video-container">
                  <video
                    :src="projectData.projectVideoUrl"
                    controls
                    class="project-video"
                    preload="metadata"
                  >
                    您的浏览器不支持视频播放
                  </video>
                </div>
              </el-tab-pane>

              <!-- 无媒体提示 -->
              <el-tab-pane
                label="暂无媒体"
                name="empty"
                v-if="
                  !projectImages.length &&
                  (!projectData.projectVideoUrl || !projectData.projectVideoUrl.trim())
                "
                disabled
              >
                <div class="empty-media">
                  <Icon icon="ep:picture" :size="64" />
                  <span>暂无图片或视频</span>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

        <!-- 右侧：基本信息 -->
        <el-col :span="16">
          <el-card class="info-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <Icon icon="ep:info-filled" class="header-icon" />
                <span>基本信息</span>
              </div>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="项目类型">
                <dict-tag :type="DICT_TYPE.BIZ_PROJECT_TYPE" :value="projectData.projectType" />
              </el-descriptions-item>
              <el-descriptions-item label="配置类型">
                <dict-tag
                  :type="DICT_TYPE.BIZ_PROJECT_CONFIG_TYPE"
                  :value="projectData.projectConfigType"
                />
              </el-descriptions-item>
              <el-descriptions-item label="基金类型">
                <dict-tag :type="DICT_TYPE.BIZ_ASSET_TYPE" :value="projectData.assetType" />
              </el-descriptions-item>
              <el-descriptions-item label="发行商">{{
                projectData.publisherCompanyName
              }}</el-descriptions-item>
              <el-descriptions-item label="发行链">{{ chainName }}</el-descriptions-item>
              <el-descriptions-item label="发行数量"
                >{{ projectData.issueQuantity }} 份</el-descriptions-item
              >
              <el-descriptions-item label="剩余数量"
                >{{ projectData.remainingQuantity }} 份</el-descriptions-item
              >
              <el-descriptions-item label="发行单价"
                >{{ projectData.issueUnitPrice }}
                {{ projectData.investmentCurrency || 'USDT' }}</el-descriptions-item
              >
              <el-descriptions-item label="预期年化"
                >{{ projectData.expectedAnnualReturn }}%</el-descriptions-item
              >
              <el-descriptions-item label="起购量"
                >{{ projectData.minimumPurchase }} 份</el-descriptions-item
              >
              <el-descriptions-item label="基金时长">
                {{
                  projectData.projectConfigType === 1 && !projectData.duration
                    ? '无限期'
                    : projectData.duration
                      ? projectData.duration + ' 个月'
                      : '-'
                }}
              </el-descriptions-item>
              <el-descriptions-item label="上架状态">
                <el-tag :type="projectData.sellStatus === 1 ? 'success' : 'info'">
                  {{ projectData.sellStatus === 1 ? '已上架' : '未上架' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="投资货币">{{
                projectData.investmentCurrency || '-'
              }}</el-descriptions-item>
              <el-descriptions-item label="收益货币">{{
                projectData.earningCurrency || '-'
              }}</el-descriptions-item>

              <el-descriptions-item label="运行开始时间" v-if="projectData.lockStartTime">
                {{ formatDateDay(projectData.lockStartTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="运行结束时间" v-if="projectData.lockEndTime">
                {{ formatDateDay(projectData.lockEndTime) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>

      <!-- 统计卡片 -->
      <el-row :gutter="16" class="stat-cards">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div
                class="stat-icon"
                style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
              >
                <Icon icon="ep:goods" :size="24" color="#fff" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ salesProgress }}%</div>
                <div class="stat-label">销售进度</div>
              </div>
            </div>
            <el-progress :percentage="salesProgress" :show-text="false" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div
                class="stat-icon"
                style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
              >
                <Icon icon="ep:user" :size="24" color="#fff" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ operationData.investorCount || 0 }}</div>
                <div class="stat-label">投资人数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div
                class="stat-icon"
                style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
              >
                <Icon icon="ep:money" :size="24" color="#fff" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ formatAmount(operationData.totalInvestorIncome) }}</div>
                <div class="stat-label">累计收益</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div
                class="stat-icon"
                style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
              >
                <Icon icon="ep:document" :size="24" color="#fff" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ operationData.dividendApplyCount || 0 }}</div>
                <div class="stat-label">分红次数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 快捷操作 -->
      <el-card class="action-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <Icon icon="ep:operation" class="header-icon" />
            <span>快捷操作</span>
            <el-tooltip content="项目下架后才可以编辑" placement="top">
              <Icon icon="ep:question-filled" class="ml-5px cursor-pointer" />
            </el-tooltip>
          </div>
        </template>
        <div class="action-buttons">
          <!-- 编辑项目 -->
          <el-button
            type="primary"
            @click="handleEdit"
            v-if="canEdit"
            v-hasPermi="['project:info:update']"
          >
            <Icon icon="ep:edit" class="mr-5px" />编辑项目
          </el-button>

          <!-- 提交审核 -->
          <!--          <el-button
            type="warning" 
            @click="handleSubmitAudit"
            v-if="canSubmitAudit"
            v-hasPermi="['project:info:update']"
          >
            <Icon icon="ep:promotion" class="mr-5px" />提交审核
          </el-button>-->

          <!-- 上架/下架 -->
          <el-button
            :type="projectData.sellStatus === 1 ? 'danger' : 'success'"
            @click="handleSellStatus"
            v-if="canChangeSellStatus"
            v-hasPermi="['project:info:sell-status']"
          >
            <Icon :icon="projectData.sellStatus === 1 ? 'ep:bottom' : 'ep:top'" class="mr-5px" />
            {{ projectData.sellStatus === 1 ? '下架' : '上架' }}
          </el-button>

          <!-- 结束运行 -->
          <el-button
            type="danger"
            @click="handleEndRun"
            v-if="
              projectData.auditStatus === 2 &&
              projectData.editStatus !== 1 &&
              projectData.projectStatus !== 1 &&
              projectData.projectStatus !== 4
            "
            v-hasPermi="['project:info:end']"
          >
            <Icon icon="ep:video-pause" class="mr-5px" />结束运行
          </el-button>

          <!-- 项目配置 -->
          <el-button type="info" plain @click="handleConfig" v-hasPermi="['project:info:update']">
            <Icon icon="ep:setting" class="mr-5px" />项目配置
          </el-button>

          <!-- 分红配置 -->
          <el-button
            type="success"
            plain
            @click="handleDividend"
            v-if="projectData.projectType !== 1"
            v-hasPermi="['project:dividend-period:query']"
          >
            <Icon icon="ep:coin" class="mr-5px" />分红配置
          </el-button>

          <!-- 公告管理 -->
          <el-button
            type="primary"
            plain
            @click="handleNotice"
            v-if="projectData.auditStatus === 2"
          >
            <Icon icon="ep:bell" class="mr-5px" />公告管理
          </el-button>

          <!-- 运营统计 -->
          <el-button plain @click="handleOperation" v-hasPermi="['project:operation:query']">
            <Icon icon="ep:data-analysis" class="mr-5px" />运营统计
          </el-button>

          <!-- 账单管理 -->
          <el-button plain @click="handleBill" v-hasPermi="['project:bill:query']">
            <Icon icon="ep:tickets" class="mr-5px" />账单管理
          </el-button>
        </div>
      </el-card>

      <!-- Tab内容区 -->
      <el-card class="content-card" shadow="hover">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="项目介绍" name="intro">
            <div class="intro-content" v-html="currentProjectData.projectIntro || '暂无介绍'"></div>
          </el-tab-pane>
          <el-tab-pane label="赎回规则" name="redemption">
            <div class="redemption-content">
              <h4>赎回规则</h4>
              <p>{{ currentProjectData.redemptionRules || '暂无赎回规则' }}</p>

              <h4 class="mt-20px">提前赎回手续费</h4>
              <el-table :data="feeConfigList" border v-if="feeConfigList.length > 0">
                <el-table-column label="赎回时间" align="center">
                  <template #default="{ row }">{{ row.days }} 天内</template>
                </el-table-column>
                <el-table-column label="手续费比例" align="center">
                  <template #default="{ row }">{{ row.ratio }}%</template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无手续费配置" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="项目资料" name="files">
            <div class="files-content">
              <el-table :data="projectFiles" border v-if="projectFiles.length > 0">
                <el-table-column label="文件名" prop="name" />
                <el-table-column label="操作" width="120" align="center">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="downloadFile(row.url)">下载</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无项目资料" />
            </div>
          </el-tab-pane>
          <el-tab-pane
            label="须知说明"
            name="instructions"
            v-if="
              currentProjectData.purchaseInstructions || currentProjectData.dividendInstructions
            "
          >
            <div class="instructions-content">
              <div v-if="currentProjectData.purchaseInstructions" class="instruction-section">
                <h4>购买须知</h4>
                <div class="instructions-text">{{ currentProjectData.purchaseInstructions }}</div>
              </div>
              <div
                v-if="currentProjectData.dividendInstructions"
                class="instruction-section"
                :class="{ 'mt-20px': currentProjectData.purchaseInstructions }"
              >
                <h4>分红须知</h4>
                <div class="instructions-text">{{ currentProjectData.dividendInstructions }}</div>
              </div>
            </div>
          </el-tab-pane>
          <!-- 编辑历史 Tab -->
          <el-tab-pane label="审核历史" name="history">
            <div class="history-content" v-loading="historyLoading">
              <el-table :data="snapshotList" border v-if="snapshotList.length > 0">
                <el-table-column label="版本" prop="snapshotVersion" width="100" align="center" />
                <el-table-column label="提交时间" prop="createTime" width="180" align="center">
                  <template #default="{ row }">{{ formatDateDay(row.createTime) }}</template>
                </el-table-column>
                <el-table-column label="审核状态" width="120" align="center">
                  <template #default="{ row }">
                    <el-tag v-if="row.snapshotStatus === 1" type="warning">待审核</el-tag>
                    <el-tag v-else-if="row.snapshotStatus === 2" type="success">已通过</el-tag>
                    <el-tag v-else type="danger">已拒绝</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="审核人" prop="operatorName" width="120" align="center" />
                <el-table-column label="审核意见" prop="auditRemark" show-overflow-tooltip />
                <el-table-column label="操作" width="120" align="center">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="showSnapshotDetail(row.id)"
                      >查看详情</el-button
                    >
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-else description="暂无编辑历史" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 项目配置弹窗 -->
    <ProjectConfigDialog ref="configDialogRef" @success="loadProjectData" />
    <!-- 基金项目配置弹窗 -->
    <ProjectFundConfigDialog ref="configFundDialogRef" @success="loadProjectData" />
    <!-- 分红配置弹窗 -->
    <DividendPeriodDialog ref="dividendDialogRef" />
    <!-- 运营统计弹窗 -->
    <OperationStat ref="opStatRef" />

    <!-- 快照详情弹窗 -->
    <SnapshotDetailDialog ref="snapshotDetailDialogRef" />
  </div>
</template>

<script setup lang="ts">
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { formatDateDay } from '@/utils/formatTime'
import { InfoApi } from '@/api/project/projectinfo'
import { ChainApi } from '@/api/chain/chain'
import { getLatestSnapshot, getSnapshotPage, SnapshotVO } from '@/api/project/projectsnapshot'
import ProjectConfigDialog from './ProjectConfigDialog.vue'
import ProjectFundConfigDialog from './ProjectFundConfigDialog.vue'
import DividendPeriodDialog from './DividendPeriodDialog.vue'
import OperationStat from './OperationStat.vue'
import SnapshotDetailDialog from './SnapshotDetailDialog.vue'
import { useRouter, useRoute } from 'vue-router'

defineOptions({ name: 'ProjectSpace' })

const router = useRouter()
const route = useRoute()
const message = useMessage()

const projectId = computed(() => Number(route.query.id))
const loading = ref(false)
const activeTab = ref('intro')

// 项目数据
const projectData = ref<any>({})
const operationData = ref<any>({})
const chainName = ref('')
const activeMediaTab = ref('images')

// 快照相关
const latestSnapshot = ref<SnapshotVO | null>(null)
const snapshotList = ref<SnapshotVO[]>([])
const historyLoading = ref(false)
const snapshotDetailDialogRef = ref()
const configFundDialogRef = ref()

// 多语言支持
const currentLang = ref('')
const langList = ref<string[]>([])
const multiLangData = ref<Record<string, any>>({})

// 字段映射 snake_case -> camelCase
const fieldMap: Record<string, string> = {
  project_name: 'projectName',
  project_intro: 'projectIntro',
  redemption_rules: 'redemptionRules',
  purchase_instructions: 'purchaseInstructions',
  dividend_instructions: 'dividendInstructions'
}

// 计算属性

const projectImages = computed(() => {
  if (projectData.value.projectImageUrls) {
    return typeof projectData.value.projectImageUrls === 'string'
      ? projectData.value.projectImageUrls.split(',')
      : projectData.value.projectImageUrls
  }
  return []
})

const salesProgress = computed(() => {
  if (!projectData.value.issueQuantity) return 0
  const sold = projectData.value.issueQuantity - (projectData.value.remainingQuantity || 0)
  return Math.round((sold / projectData.value.issueQuantity) * 100)
})

const feeConfigList = computed(() => {
  if (!projectData.value.earlyRedemptionFeeJson) return []
  try {
    const json = JSON.parse(projectData.value.earlyRedemptionFeeJson)
    return Array.isArray(json)
      ? json.map((item: any) => ({ days: parseInt(item.day), ratio: parseFloat(item.value) }))
      : []
  } catch {
    return []
  }
})

const projectFiles = computed(() => {
  if (!projectData.value.projectFileUrls) return []
  try {
    const json = JSON.parse(projectData.value.projectFileUrls as string)
    return Array.isArray(json) ? json : []
  } catch {
    return []
  }
})

const currentProjectData = computed(() => {
  if (!currentLang.value || currentLang.value === projectData.value.language) {
    return projectData.value
  }
  const langData = multiLangData.value[currentLang.value] || {}
  return {
    ...projectData.value,
    ...langData
  }
})

const snapshotAlertType = computed(() => {
  if (!latestSnapshot.value) return 'info'
  if (latestSnapshot.value.snapshotStatus === 1) return 'warning'
  if (latestSnapshot.value.snapshotStatus === 2) return 'success'
  return 'error'
})

const showSnapshotAlert = computed(() => {
  if (!latestSnapshot.value) return false
  // 如果不是已通过状态，总是显示
  if (latestSnapshot.value.snapshotStatus !== 2) return true
  // 如果是已通过状态
  // 1. 如果版本号 > 1，显示（提示编辑已生效）
  if (latestSnapshot.value.snapshotVersion > 1) return true
  // 2. 如果有审核备注，显示
  if (latestSnapshot.value.auditRemark) return true
  // 否则（版本1且无备注），不显示
  return false
})

// 权限判断
const canEdit = computed(() => {
  const { sellStatus, auditStatus, projectStatus, editStatus } = projectData.value
  // 下架(sellStatus !== 1)且非审核中(auditStatus !== 1)且非结束状态(projectStatus !== 4)
  return sellStatus !== 1 && auditStatus !== 1 && projectStatus !== 4 && editStatus !== 1
})

const canSubmitAudit = computed(
  () => projectData.value.auditStatus === 0 || projectData.value.auditStatus === 3
)

const canChangeSellStatus = computed(() => {
  // 封闭型基金(assetType=2)在运行中(projectStatus=2)时不显示上下架按钮
  if (projectData.value.projectType === 2 && projectData.value.projectStatus === 2) {
    return false
  }
  // 结束的项目不显示上下架按钮
  if (projectData.value.projectStatus === 4) {
    return false
  }
  // 只有已上线的项目才能上下架
  return projectData.value.auditStatus === 2
})

// 状态标签
const getStatusLabel = (status: number) => {
  const map: Record<number, string> = {
    0: '未运行',
    1: '待运行审核',
    2: '运行审核通过',
    3: '运行审核不通过',
    4: '已结束'
  }
  return map[status] || '未知'
}

const getStatusTagType = (status: number) => {
  const map: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'info'
  }
  return (map[status] || 'info') as any
}

const getAuditStatusLabel = (status: number) => {
  const map: Record<number, string> = {
    0: '待提交',
    1: '待审核',
    2: '上线审核通过',
    3: '已拒绝'
  }
  return map[status] || '未知'
}

const getAuditStatusTagType = (status: number) => {
  const map: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return (map[status] || 'info') as any
}

// 获取语言标签
const dictLangLabel = (lang: string) => {
  const dict = getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE).find((d: any) => d.value === lang)
  return dict ? dict.label : lang
}

const formatAmount = (amount: any) => {
  if (!amount) return '0'
  return Number(amount).toLocaleString()
}

// 加载数据
const loadProjectData = async () => {
  if (!projectId.value) return
  loading.value = true
  try {
    const data = await InfoApi.getInfo(projectId.value)
    projectData.value = data

    // 初始化多语言
    langList.value = []
    multiLangData.value = {}
    if (data.language) {
      currentLang.value = data.language
      langList.value.push(data.language)
    }

    if (data.projectJson) {
      try {
        const json = JSON.parse(data.projectJson)
        Object.keys(json).forEach((lang) => {
          if (lang !== data.language) {
            if (!langList.value.includes(lang)) {
              langList.value.push(lang)
            }
            // 转换数据 keys
            const source = json[lang]
            const target: Record<string, any> = {}
            Object.keys(source).forEach((key) => {
              // 如果 fieldMap 中有对应 key，则转换
              // 修正：我们 fieldMap 是 snake -> camel. json key 是 snake.
              // 所以可以直接用 map
              const mappedKey = fieldMap[key]
              if (mappedKey) {
                target[mappedKey] = source[key]
              }
            })
            multiLangData.value[lang] = target
          }
        })
      } catch (e) {
        console.error('多语言解析失败', e)
      }
    }

    // 获取链名称
    if (data.issueChainId) {
      const chains = await ChainApi.getActiveChainList()
      const chain = chains.find((c: any) => c.id === data.issueChainId)
      chainName.value = chain?.name || '-'
    }

    // TODO: 获取运营数据
    // operationData.value = await OperationApi.getByProjectId(projectId.value)

    // 获取最新快照
    await loadLatestSnapshot()
  } finally {
    loading.value = false
  }
}

// 加载最新快照
const loadLatestSnapshot = async () => {
  if (!projectId.value) return
  try {
    latestSnapshot.value = await getLatestSnapshot(projectId.value)
  } catch (e) {
    latestSnapshot.value = null
  }
}

// 加载快照历史
const loadSnapshotHistory = async () => {
  if (!projectId.value) return
  historyLoading.value = true
  try {
    const result = await getSnapshotPage({ projectId: projectId.value, pageNo: 1, pageSize: 100 })
    snapshotList.value = result.list || []
  } finally {
    historyLoading.value = false
  }
}

// 显示快照详情
const showSnapshotDetail = (snapshotId: number) => {
  snapshotDetailDialogRef.value?.open(snapshotId)
}

// 操作方法
const handleBack = () => {
  router.push({ name: 'ProjectInfo' })
}

const handleRefresh = () => {
  loadProjectData()
}

const handleEdit = () => {
  router.push({ name: 'ProjectCreate', query: { id: projectId.value } })
}

const handleSubmitAudit = async () => {
  try {
    await message.confirm('确认提交审核吗？')
    await InfoApi.submitAudit({ projectId: projectId.value })
    message.success('提交审核成功')
    loadProjectData()
  } catch {}
}

const handleSellStatus = async () => {
  const newStatus = projectData.value.sellStatus === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'
  try {
    await message.confirm(`确认要${statusText}该项目吗？`)
    await InfoApi.updateSellStatus({ projectId: projectId.value, sellStatus: newStatus })
    message.success(`${statusText}成功`)
    loadProjectData()
  } catch {}
}

const handleEndRun = async () => {
  try {
    await message.confirm('确认要结束运行吗？此操作不可逆！')
    await InfoApi.endRun(projectId.value)
    message.success('结束运行成功')
    loadProjectData()
  } catch {}
}

const configDialogRef = ref()
const handleConfig = () => {
  if (projectData.value.projectConfigType === 1) {
    configFundDialogRef.value.open(
      projectId.value,
      projectData.value.projectName,
      projectData.value.investmentCurrency
    )
  } else {
    configDialogRef.value.open(projectId.value, projectData.value.projectName)
  }
}

const dividendDialogRef = ref()
const handleDividend = () => {
  dividendDialogRef.value.open(projectId.value, projectData.value.projectName)
}

const handleNotice = () => {
  router.push({ name: 'ProjectNotice', query: { projectId: projectId.value } })
}

const opStatRef = ref()
const handleOperation = () => {
  opStatRef.value.open(
    projectId.value,
    projectData.value.investmentCurrency,
    projectData.value.earningCurrency
  )
}

const handleBill = () => {
  router.push({ name: 'ProjectBill', query: { projectId: projectId.value } })
}

const downloadFile = (url: string) => {
  window.open(url, '_blank')
}

// 监听 Tab 切换，加载快照历史
watch(activeTab, (newTab) => {
  if (newTab === 'history' && snapshotList.value.length === 0) {
    loadSnapshotHistory()
  }
})

// 监听路由变化,当projectId变化时重新加载数据
watch(
  () => route.query.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadProjectData()
      loadSnapshotHistory()
    }
  }
)

onMounted(() => {
  loadProjectData()
  loadSnapshotHistory()
})
</script>

<style lang="scss" scoped>
.project-space {
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.page-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  .header-title {
    display: flex;
    align-items: center;
  }

  .project-name {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.snapshot-alert {
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.project-overview {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.cover-card {
  height: 100%;

  :deep(.el-card__body) {
    padding: 0;
    min-height: 450px;
  }

  .media-tabs {
    :deep(.el-tabs__header) {
      margin: 0;
      padding: 12px 16px 0;
      background: #f5f7fa;
    }

    :deep(.el-tabs__content) {
      padding: 16px;
    }
  }

  .carousel-image {
    width: 100%;
    height: 400px;
    border-radius: 4px;
  }

  :deep(.el-carousel__container) {
    height: 400px;
  }

  :deep(.el-carousel__item) {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
  }

  :deep(.el-carousel__indicators) {
    .el-carousel__indicator {
      .el-carousel__button {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: rgba(0, 0, 0, 0.3);
      }

      &.is-active .el-carousel__button {
        background-color: #409eff;
      }
    }
  }

  .video-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400px;
    background: #000;
    border-radius: 8px;
    overflow: hidden;

    .project-video {
      width: 100%;
      max-height: 400px;
      object-fit: contain;
    }
  }

  .empty-media {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 400px;
    color: #909399;
    background: #f5f7fa;
    border-radius: 8px;

    span {
      margin-top: 16px;
      font-size: 14px;
    }
  }

  .image-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #909399;
    background: #f5f7fa;

    span {
      margin-top: 12px;
      font-size: 14px;
    }
  }

  :deep(.el-carousel__item) {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.info-card {
  height: 100%;

  .card-header {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 500;

    .header-icon {
      margin-right: 8px;
      color: #409eff;
    }
  }
}

.stat-cards {
  margin-top: 24px;

  .stat-card {
    :deep(.el-card__body) {
      padding: 16px;
    }

    .stat-content {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

.action-card {
  margin-top: 24px;

  .card-header {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 500;

    .header-icon {
      margin-right: 8px;
      color: #409eff;
    }
  }

  .action-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }
}

.content-card {
  margin-top: 24px;

  .intro-content {
    min-height: 200px;
    line-height: 1.8;
  }

  .redemption-content {
    min-height: 200px;

    h4 {
      margin-bottom: 12px;
      color: #303133;
    }
  }

  .files-content {
    min-height: 200px;
  }

  .instructions-content {
    min-height: 200px;

    .instruction-section {
      h4 {
        margin-bottom: 12px;
        color: #303133;
        font-weight: 600;
      }
    }
  }
}

.ml-10px {
  margin-left: 10px;
}

.mr-5px {
  margin-right: 5px;
}

.mt-20px {
  margin-top: 20px;
}

.instructions-text {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #606266;
}
</style>
