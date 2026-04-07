<template>
  <Dialog v-model="dialogVisible" width="900px">
    <template #title>
      <div class="dialog-header">
        <span>快照详情 v{{ currentSnapshot?.snapshotVersion }}</span>
        <div class="lang-selector" v-if="langList.length > 1" @click.stop>
          <el-radio-group v-model="currentLang" size="small">
            <el-radio-button 
              v-for="lang in langList" 
              :key="lang" 
              :label="lang"
            >
              {{ dictLangLabel(lang) }}
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </template>
    <div v-loading="loading" class="snapshot-detail-dialog">
      <!-- 快照基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:document" />
            <span>快照信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border v-if="currentSnapshot">
          <el-descriptions-item label="版本号">v{{ currentSnapshot.snapshotVersion }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag v-if="currentSnapshot.snapshotStatus === 1" type="warning">待审核</el-tag>
            <el-tag v-else-if="currentSnapshot.snapshotStatus === 2" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateDay(currentSnapshot.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ currentSnapshot.operatorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核意见" :span="2">{{ currentSnapshot.auditRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 项目基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:document" />
            <span>基本信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ displayData.projectName }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">
            <dict-tag :type="DICT_TYPE.BIZ_PROJECT_TYPE" :value="displayData.projectType" />
          </el-descriptions-item>
          <el-descriptions-item label="基金类型">
            <dict-tag :type="DICT_TYPE.BIZ_ASSET_TYPE" :value="displayData.assetType" />
          </el-descriptions-item>
          <el-descriptions-item label="发行商">{{ displayData.publisherCompanyName }}</el-descriptions-item>
          <el-descriptions-item label="发行数量">{{ displayData.issueQuantity }} 份</el-descriptions-item>
          <el-descriptions-item label="发行单价">{{ displayData.issueUnitPrice }} {{ displayData.investmentCurrency || '' }}</el-descriptions-item>
          <el-descriptions-item label="预期年化">{{ displayData.expectedAnnualReturn }}%</el-descriptions-item>
          <el-descriptions-item label="起购量">{{ displayData.minimumPurchase }} 份</el-descriptions-item>
          <el-descriptions-item label="基金时长">{{ displayData.duration || '-' }} 个月</el-descriptions-item>
          <el-descriptions-item label="投资货币">{{ displayData.investmentCurrency || '-' }}</el-descriptions-item>
          <el-descriptions-item label="收益货币">{{ displayData.earningCurrency || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 项目介绍 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:picture" />
            <span>项目介绍</span>
          </div>
        </template>
        <div class="intro-content" v-html="displayData.projectIntro || '暂无介绍'"></div>
      </el-card>

      <!-- 项目图片 -->
      <el-card class="info-card" shadow="never" v-if="projectImages.length > 0">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:picture-filled" />
            <span>项目图片</span>
          </div>
        </template>
        <div class="image-gallery">
          <el-image
            v-for="(url, index) in projectImages"
            :key="index"
            :src="url"
            :preview-src-list="projectImages"
            :initial-index="index"
            fit="cover"
            class="project-image"
          />
        </div>
      </el-card>

      <!-- 项目视频 -->
      <el-card class="info-card" shadow="never" v-if="displayData.projectVideoUrl">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:video-camera" />
            <span>项目视频</span>
          </div>
        </template>
        <div class="video-container">
          <video 
            :src="displayData.projectVideoUrl" 
            controls 
            class="project-video"
          ></video>
        </div>
      </el-card>

      <!-- 项目资料 -->
      <el-card class="info-card" shadow="never" v-if="projectFiles.length > 0">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:folder" />
            <span>项目资料（{{ projectFiles.length }} 个文件）</span>
          </div>
        </template>
        <div class="file-list">
          <div v-for="(file, index) in projectFiles" :key="index" class="file-item">
            <Icon icon="ep:document" class="file-icon" />
            <span class="file-name">{{ file.name || `文件${index + 1}` }}</span>
            <el-button type="primary" link @click="downloadFile(file.url)">
              <Icon icon="ep:download" class="mr-5px" />下载
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 赎回规则 -->
      <el-card class="info-card" shadow="never" v-if="displayData.redemptionRules || feeConfigList.length > 0">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:money" />
            <span>赎回规则</span>
          </div>
        </template>
        <div v-if="displayData.redemptionRules" class="rules-text">{{ displayData.redemptionRules }}</div>
        <div v-if="feeConfigList.length > 0" class="fee-config">
          <h4>提前赎回手续费</h4>
          <el-table :data="feeConfigList" border size="small">
            <el-table-column label="赎回时间" align="center">
              <template #default="{ row }">{{ row.days }} 天内</template>
            </el-table-column>
            <el-table-column label="手续费比例" align="center">
              <template #default="{ row }">{{ row.ratio }}%</template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <!-- 须知说明 -->
      <el-card class="info-card" shadow="never" v-if="displayData.purchaseInstructions || displayData.dividendInstructions">
        <template #header>
          <div class="section-header">
            <Icon icon="ep:document" />
            <span>须知说明</span>
          </div>
        </template>
        <div v-if="displayData.purchaseInstructions" class="instruction-section">
          <h4>购买须知</h4>
          <div class="instructions-text">{{ displayData.purchaseInstructions }}</div>
        </div>
        <div v-if="displayData.dividendInstructions" class="instruction-section" :class="{ 'mt-16px': displayData.purchaseInstructions }">
          <h4>分红须知</h4>
          <div class="instructions-text">{{ displayData.dividendInstructions }}</div>
        </div>
      </el-card>
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { formatDateDay } from '@/utils/formatTime'
import { getSnapshot, SnapshotVO } from '@/api/project/projectsnapshot'

defineOptions({ name: 'SnapshotDetailDialog' })

const dialogVisible = ref(false)
const loading = ref(false)

const currentSnapshot = ref<SnapshotVO | null>(null)
const snapshotData = ref<any>(null)

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

// 显示数据：合并多语言
const displayData = computed(() => {
  const baseData = snapshotData.value || {}
  
  if (!currentLang.value || currentLang.value === baseData.language) {
    return baseData
  }
  
  const langData = multiLangData.value[currentLang.value] || {}
  return {
    ...baseData,
    ...langData
  }
})

// 获取语言标签
const dictLangLabel = (lang: string) => {
  const dict = getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE).find((d: any) => d.value === lang)
  return dict ? dict.label : lang
}

// 项目图片列表
const projectImages = computed(() => {
  if (displayData.value.projectImageUrls) {
    return typeof displayData.value.projectImageUrls === 'string'
      ? displayData.value.projectImageUrls.split(',')
      : displayData.value.projectImageUrls
  }
  return []
})

// 手续费配置
const feeConfigList = computed(() => {
  if (!displayData.value.earlyRedemptionFeeJson) return []
  try {
    const json = JSON.parse(displayData.value.earlyRedemptionFeeJson)
    return Array.isArray(json)
      ? json.map((item: any) => ({ days: parseInt(item.day), ratio: parseFloat(item.value) }))
      : []
  } catch {
    return []
  }
})

// 项目资料文件
const projectFiles = computed(() => {
  if (!displayData.value.projectFileUrls) return []
  try {
    const list = typeof displayData.value.projectFileUrls === 'string'
      ? JSON.parse(displayData.value.projectFileUrls)
      : displayData.value.projectFileUrls
    return Array.isArray(list) ? list : []
  } catch {
    return []
  }
})

// 下载文件
const downloadFile = (url: string) => {
  window.open(url, '_blank')
}

/** 打开弹窗 */
const open = async (snapshotId: number) => {
  dialogVisible.value = true
  loading.value = true
  
  try {
    currentSnapshot.value = await getSnapshot(snapshotId)
    snapshotData.value = JSON.parse(currentSnapshot.value.projectData)
    
    // 初始化多语言
    langList.value = []
    multiLangData.value = {}
    if (snapshotData.value.language) {
      currentLang.value = snapshotData.value.language
      langList.value.push(snapshotData.value.language)
    }
    
    // 解析多语言数据
    if (snapshotData.value.projectJson) {
      try {
        const json = JSON.parse(snapshotData.value.projectJson)
        Object.keys(json).forEach(lang => {
          if (lang !== snapshotData.value.language) {
            if (!langList.value.includes(lang)) {
              langList.value.push(lang)
            }
            // 转换数据 keys
            const source = json[lang]
            const target: Record<string, any> = {}
            Object.keys(source).forEach(key => {
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
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>

<style lang="scss" scoped>
.dialog-header {
  display: flex;
  align-items: center;
  
  span {
    font-size: 16px;
    font-weight: 500;
  }
  
  .lang-selector {
    margin-left: 24px;
    display: flex;
    align-items: center;
  }
}

.snapshot-detail-dialog {
  max-height: 60vh;
  overflow-y: auto;
}

.info-card {
  margin-bottom: 16px;
  
  :deep(.el-card__header) {
    padding: 12px 16px;
    background: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
  }
  
  :deep(.el-card__body) {
    padding: 16px;
  }
}

.section-header {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  
  :deep(.iconify) {
    margin-right: 8px;
    color: #409eff;
  }
}

.intro-content {
  line-height: 1.8;
  color: #606266;
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  
  .project-image {
    width: 120px;
    height: 90px;
    border-radius: 4px;
    cursor: pointer;
  }
}

.video-container {
  .project-video {
    max-width: 100%;
    max-height: 300px;
    border-radius: 8px;
  }
}

.file-list {
  .file-item {
    display: flex;
    align-items: center;
    padding: 10px 12px;
    background: #f5f7fa;
    border-radius: 6px;
    margin-bottom: 8px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .file-icon {
      color: #409eff;
      font-size: 18px;
      margin-right: 10px;
    }
    
    .file-name {
      flex: 1;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.mr-5px {
  margin-right: 5px;
}

.rules-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
}

.fee-config {
  h4 {
    font-size: 14px;
    color: #303133;
    margin-bottom: 12px;
  }
}

.instruction-section {
  h4 {
    font-size: 14px;
    color: #303133;
    margin-bottom: 8px;
    font-weight: 600;
  }
  
  .instructions-text {
    white-space: pre-wrap;
    line-height: 1.8;
    color: #606266;
  }
}

.mt-16px {
  margin-top: 16px;
}
</style>
