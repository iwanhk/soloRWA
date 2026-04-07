<template>
  <div class="project-create-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <el-page-header @back="handleBack">
          <template #content>
            <span class="page-title">{{ isEdit ? '编辑项目' : '创建新项目' }}</span>
          </template>
        </el-page-header>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="140px"
        v-loading="formLoading"
        class="project-form"
      >
        <!-- 语言配置卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:reading" class="header-icon" />
              <span>多语言配置</span>
            </div>
          </template>

          <el-form-item label="主语言" prop="language">
            <el-select
              v-model="formData.language"
              placeholder="请选择主语言"
              class="w-full"
              :disabled="isEdit && !!formData.language"
            >
              <el-option
                v-for="dict in getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>

          <div class="lang-tabs" v-if="formData.language">
            <div class="lang-header">
              <span>编辑语言：</span>
            </div>
            <div class="lang-list">
              <div
                v-for="lang in langList"
                :key="lang"
                class="lang-item"
                :class="{ active: activeLang === lang }"
                @click="activeLang = lang"
              >
                {{ dictLangLabel(lang) }}
                <Icon
                  v-if="lang !== formData.language"
                  icon="ep:close"
                  class="remove-icon ml-5px"
                  @click.stop="handleRemoveLang(lang)"
                />
              </div>

              <el-dropdown trigger="click" @command="handleAddLang">
                <el-button type="primary" link icon="ep:plus" class="add-lang-btn"
                  >添加语言</el-button
                >
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="dict in getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE)"
                      :key="dict.value"
                      :command="dict.value"
                      :disabled="langList.includes(dict.value)"
                    >
                      {{ dict.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-card>

        <!-- 基本信息卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:document" class="header-icon" />
              <span>基本信息</span>
            </div>
          </template>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="项目名称" prop="projectName">
                <el-input v-model="currentFormData.projectName" placeholder="请输入项目名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="配置类型" prop="projectConfigType">
                <el-select
                  v-model="formData.projectConfigType"
                  placeholder="请选择配置类型"
                  class="w-full"
                  :disabled="isApproved"
                >
                  <el-option
                    v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_CONFIG_TYPE)"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
<!--                <el-select
                  v-model="formData.projectConfigType"
                  placeholder="请选择配置类型"
                  class="w-full"
                  :disabled="isApproved"
                >
                  <el-option label="挖矿型" :value="0" />
                  <el-option label="基金型" :value="1" />
                </el-select>-->
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="项目类型" prop="projectType">
                <el-select
                  v-model="formData.projectType"
                  placeholder="请选择项目类型"
                  class="w-full"
                  :disabled="isApproved || formData.projectConfigType === 0"
                >
                  <el-option
                    v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_TYPE)"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="基金类型" prop="assetType">
                <el-select
                  v-model="formData.assetType"
                  placeholder="请选择基金类型"
                  class="w-full"
                  :disabled="isApproved"
                >
                  <el-option
                    v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_ASSET_TYPE)"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="预期年化收益" prop="expectedAnnualReturn">
                <el-input
                  v-model="formData.expectedAnnualReturn"
                  placeholder="请输入预期年化收益"
                  type="number"
                >
                  <template #append>%</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                label="基金时长/月"
                prop="duration"
                :required="formData.projectType === 2"
              >
                <el-input-number
                  v-model="formData.duration"
                  placeholder="请输入"
                  class="w-full"
                  :min="1"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 发行信息卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:coin" class="header-icon" />
              <span>发行信息</span>
            </div>
          </template>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="发行数量" prop="issueQuantity">
                <el-input-number
                  v-model="formData.issueQuantity"
                  placeholder="请输入发行数量"
                  class="w-full"
                  :min="1"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="起购量" prop="minimumPurchase">
                <el-input-number
                  v-model="formData.minimumPurchase"
                  placeholder="请输入起购量"
                  class="w-full"
                  :min="1"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="投资货币" prop="investmentCurrency">
                <el-select
                  v-model="formData.investmentCurrency"
                  placeholder="请选择投资货币"
                  class="w-full"
                  filterable
                >
                  <el-option
                    v-for="coin in fiatCoinList"
                    :key="coin.coinCode"
                    :label="`${coin.coinName} (${coin.coinCode})`"
                    :value="coin.coinCode"
                  >
                    <div class="coin-option">
                      <img v-if="coin.iconUrl" :src="coin.iconUrl" class="coin-icon" />
                      <span>{{ coin.coinName }} ({{ coin.coinCode }})</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发行单价" prop="issueUnitPrice">
                <div style="display: flex; align-items: center; width: 100%">
                  <el-input-number
                    v-model="formData.issueUnitPrice"
                    placeholder="请输入发行单价"
                    class="w-full"
                    style="flex: 1"
                    :precision="0"
                    :step="0"
                    :min="0"
                  />
                  <span
                    v-if="formData.investmentCurrency"
                    style="margin-left: 10px; min-width: 40px"
                    >{{ formData.investmentCurrency }}</span
                  >
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="收益货币" prop="earningCurrency">
                <el-select
                  v-model="formData.earningCurrency"
                  placeholder="请选择收益货币"
                  class="w-full"
                  filterable
                >
                  <el-option
                    v-for="coin in fiatCoinList"
                    :key="coin.coinCode"
                    :label="`${coin.coinName} (${coin.coinCode})`"
                    :value="coin.coinCode"
                  >
                    <div class="coin-option">
                      <img v-if="coin.iconUrl" :src="coin.iconUrl" class="coin-icon" />
                      <span>{{ coin.coinName }} ({{ coin.coinCode }})</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="isEdit">
              <el-form-item label="剩余数量">
                <el-input v-model="formData.remainingQuantity" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 项目介绍卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:picture" class="header-icon" />
              <span>项目介绍</span>
            </div>
          </template>

          <el-form-item label="项目介绍" prop="projectIntro">
            <Editor
              :project-detail="true"
              v-model="currentFormData.projectIntro"
              placeholder="请输入项目介绍（图文）"
            />
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="项目图片" prop="projectImageUrls">
                <UploadImgs v-model="formData.projectImageUrls" :limit="5" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目视频" prop="projectVideoUrl">
                <UploadFile
                  v-model="formData.projectVideoUrl"
                  :file-type="['mp4', 'avi', 'mov', 'wmv']"
                  :limit="1"
                  @before-upload="handleVideoBeforeUpload"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="项目资料" prop="projectFileUrls">
            <UploadFileDTO
              v-model="formData.projectFileUrls"
              :limit="30"
              :file-type="['pdf', 'mp4']"
            />
          </el-form-item>
        </el-card>

        <!-- 赎回规则卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:money" class="header-icon" />
              <span>赎回规则</span>
            </div>
          </template>

          <el-form-item label="赎回规则" prop="redemptionRules">
            <el-input
              v-model="currentFormData.redemptionRules"
              placeholder="请输入赎回规则"
              type="textarea"
              :rows="3"
            />
          </el-form-item>

          <el-form-item label="提前赎回手续费" prop="earlyRedemptionFeeJson">
            <div class="fee-config-container">
              <div v-for="(item, index) in feeConfigList" :key="index" class="fee-config-item">
                <el-input v-model="item.days" placeholder="天数" type="number" class="fee-input">
                  <template #append>天内</template>
                </el-input>
                <el-input v-model="item.ratio" placeholder="比例" type="number" class="fee-input">
                  <template #append>%</template>
                </el-input>
                <el-button type="danger" link icon="ep:delete" @click="removeFeeConfig(index)"
                  >删除</el-button
                >
              </div>
              <el-button type="primary" plain icon="ep:plus" @click="addFeeConfig"
                >添加配置</el-button
              >
            </div>
          </el-form-item>
        </el-card>

        <!-- 购买和分红须知卡片 -->
        <el-card class="form-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <Icon icon="ep:document" class="header-icon" />
              <span>须知说明</span>
            </div>
          </template>

          <el-form-item label="购买须知" prop="purchaseInstructions">
            <el-input
              v-model="currentFormData.purchaseInstructions"
              placeholder="请输入购买须知"
              type="textarea"
              :rows="5"
            />
          </el-form-item>

          <el-form-item label="分红须知" prop="dividendInstructions">
            <el-input
              v-model="currentFormData.dividendInstructions"
              placeholder="请输入分红须知"
              type="textarea"
              :rows="2"
            />
          </el-form-item>
        </el-card>
      </el-form>

      <!-- 底部操作按钮 -->
      <div class="form-actions">
        <el-button @click="handleBack" size="large">取消</el-button>
        <el-button
          v-if="!isEdit || formData.isRelease === 0"
          type="info"
          @click="submitForm(0, 0)"
          :loading="formLoading"
          size="large"
        >
          <Icon icon="ep:document" class="mr-5px" />
          存为草稿
        </el-button>
        <el-button type="primary" @click="submitForm(1, 1)" :loading="formLoading" size="large">
          <Icon icon="ep:check" class="mr-5px" />
          提交审核
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { InfoApi } from '@/api/project/projectinfo'
import { ChainApi, ChainVO } from '@/api/chain/chain'
import { SystemCoinApi, SystemCoinSimpleVO } from '@/api/project/systemcoin'
import * as FileApi from '@/api/infra/file'
import Editor from '@/components/Editor/src/Editor.vue'
import UploadImgs from '@/components/UploadFile/src/UploadImgs.vue'
import UploadFile from '@/components/UploadFile/src/UploadFile.vue'
import UploadFileDTO from '@/components/UploadFile/src/UploadFileDTO.vue'
import { useRouter, useRoute } from 'vue-router'

defineOptions({ name: 'ProjectCreate' })

const router = useRouter()
const route = useRoute()
const message = useMessage()
const { t } = useI18n()

// 是否已审核通过 (用于禁用关键字段)
const isApproved = computed(() => formData.value.auditStatus === 2)

// 编辑模式
const isEdit = computed(() => !!route.query.id)
const projectId = computed(() => (route.query.id ? Number(route.query.id) : undefined))

// 表单加载
const formLoading = ref(false)
const formRef = ref()

// 表单数据
const formData = ref({
  projectId: undefined as number | undefined,
  projectName: '',
  projectConfigType: 0, // 配置类型：0-挖矿型 1-基金型
  projectType: undefined as number | undefined,
  assetType: undefined as number | undefined,
  issueQuantity: undefined as number | undefined,
  issueUnitPrice: undefined as number | undefined,
  remainingQuantity: undefined as number | undefined,
  expectedAnnualReturn: undefined as string | undefined,
  minimumPurchase: 1,
  issueChainId: 1, // 默认链ID
  projectIntro: '',
  projectFileUrls: [] as any[],
  projectImageUrls: [] as string[],
  projectVideoUrl: '',
  cover: '', // 视频封面
  redemptionRules: '',
  earlyRedemptionFeeJson: '',
  duration: undefined as number | undefined,
  earningCurrency: undefined as string | undefined,
  investmentCurrency: undefined as string | undefined,
  purchaseInstructions: '', // 购买须知
  dividendInstructions: '', // 分红须知
  language: '', // 初始置空，由接口或新增加载时赋值
  projectJson: '', // 多语言JSON
  isRelease: 0, // 是否已发布过：0-未发布 1-已发布
  auditStatus: undefined as number | undefined // 审核状态
})

// 视频上传前处理 - 提取首帧
const handleVideoBeforeUpload = async (file: File) => {
  try {
    const coverUrl = await extractVideoFirstFrameFromFile(file)
    if (coverUrl) {
      formData.value.cover = coverUrl
      message.success('已自动生成视频封面')
    }
  } catch (error) {
    console.error('提取视频首帧失败:', error)
    // 不阻止视频上传,只是提取封面失败
  }
  return true // 继续上传视频
}

// 从File对象提取视频首帧
const extractVideoFirstFrameFromFile = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video')
    video.muted = true
    video.playsInline = true

    // 创建本地URL
    const videoUrl = URL.createObjectURL(file)
    video.src = videoUrl

    // 监听元数据加载完成
    video.addEventListener('loadedmetadata', () => {
      // 设置到第1秒或视频开始位置
      video.currentTime = Math.min(1, video.duration)
    })

    // 监听时间更新(帧已加载)
    video.addEventListener('seeked', () => {
      try {
        // 创建canvas
        const canvas = document.createElement('canvas')
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight

        const ctx = canvas.getContext('2d')
        if (!ctx) {
          URL.revokeObjectURL(videoUrl)
          reject(new Error('无法创建canvas context'))
          return
        }

        // 绘制视频帧到canvas
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height)

        // 转换为blob
        canvas.toBlob(
          async (blob) => {
            if (!blob) {
              URL.revokeObjectURL(videoUrl)
              reject(new Error('无法生成图片'))
              return
            }

            try {
              // 上传封面图片
              const coverFile = new File([blob], 'video-cover.jpg', { type: 'image/jpeg' })
              const uploadUrl = await uploadFile(coverFile)
              resolve(uploadUrl)
            } catch (error) {
              reject(error)
            } finally {
              // 清理资源
              URL.revokeObjectURL(videoUrl)
              video.src = ''
              video.load()
            }
          },
          'image/jpeg',
          0.8
        )
      } catch (error) {
        URL.revokeObjectURL(videoUrl)
        reject(error)
      }
    })

    video.addEventListener('error', () => {
      URL.revokeObjectURL(videoUrl)
      reject(new Error('视频加载失败: ' + (video.error?.message || '未知错误')))
    })

    // 开始加载视频
    video.load()
  })
}

// 上传文件
const uploadFile = async (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('file', file)
  const response: any = await FileApi.updateFile(formData)
  // request.upload 返回完整响应对象,需要提取 data 字段
  return response.data
}

// 表单验证规则
const formRules = reactive({
  projectName: [
    {
      validator: (_rule, _value, callback) => {
        // 遍历所有已添加的语言进行校验
        for (const lang of langList.value) {
          const langLabel = dictLangLabel(lang)
          let name = ''
          if (lang === formData.value.language) {
            name = formData.value.projectName
          } else {
            name = multiLangData.value[lang]?.projectName
          }

          if (!name || !name.trim()) {
            return callback(new Error(`语言(${langLabel})的项目名称不能为空`))
          }
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  projectType: [{ required: true, message: '项目类型不能为空', trigger: 'change' }],
  assetType: [{ required: true, message: '基金类型不能为空', trigger: 'change' }],
  issueQuantity: [{ required: true, message: '发行数量不能为空', trigger: 'blur' }],
  issueUnitPrice: [{ required: true, message: '发行单价不能为空', trigger: 'blur' }],
  expectedAnnualReturn: [{ required: true, message: '预期年化收益不能为空', trigger: 'blur' }],
  minimumPurchase: [{ required: true, message: '起购量不能为空', trigger: 'blur' }],

  projectImageUrls: [{ required: true, message: '项目图片不能为空', trigger: 'blur' }],
  investmentCurrency: [{ required: true, message: '投资货币不能为空', trigger: 'change' }],
  earningCurrency: [{ required: true, message: '收益货币不能为空', trigger: 'change' }],
  duration: [
    {
      validator: (_rule, value, callback) => {
        // 只要是封闭型项目，时长即为必填 (projectType: 1-开放型 2-封闭型)
        if (formData.value.projectType === 2 && !value) {
          callback(new Error('封闭型项目基金时长不能为空'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 发行链列表
const chainList = ref<ChainVO[]>([])

// 币种列表
const fiatCoinList = ref<SystemCoinSimpleVO[]>([])
const cryptoCoinList = ref<SystemCoinSimpleVO[]>([])

// 手续费配置
const feeConfigList = ref<{ days: number; ratio: number }[]>([])

// 多语言支持
const activeLang = ref('') // 当前编辑的语言
const langList = ref<string[]>([]) // 已添加的语言列表
const multiLangData = ref<Record<string, any>>({}) // 多语言数据存储

// 获取当前语言的数据对象，用于 v-model 绑定
const currentFormData = computed(() => {
  if (activeLang.value === formData.value.language) {
    return formData.value
  }
  return multiLangData.value[activeLang.value] || {}
})

// 获取语言标签
const dictLangLabel = (lang: string) => {
  const dict = getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE).find((d: any) => d.value === lang)
  return dict ? dict.label : lang
}

// 需要多语言的字段
const multiLangFields = [
  'projectName',
  'projectIntro',
  'redemptionRules',
  'purchaseInstructions',
  'dividendInstructions'
]

// 监听语言变化，并处理主语言切换时的数据转移
watch(
  () => formData.value.language,
  (newVal, oldVal) => {
    // 如果是修改了主语言（非初始化），且不在表单加载状态下
    if (oldVal && newVal !== oldVal && !formLoading.value) {
      // 1. 将原来主语言的数据暂存到 multiLangData
      if (!multiLangData.value[oldVal]) {
        multiLangData.value[oldVal] = {}
      }
      multiLangFields.forEach((field) => {
        multiLangData.value[oldVal][field] = formData.value[field] || ''
      })

      // 2. 将新的主语言数据从 multiLangData 恢复到 formData 中
      if (multiLangData.value[newVal]) {
        multiLangFields.forEach((field) => {
          formData.value[field] = multiLangData.value[newVal][field] || ''
        })
      } else {
        // 没有任何既存数据时，重置 formData 中的多语言字段
        multiLangFields.forEach((field) => {
          formData.value[field] = ''
        })
      }
    }

    // 保证新主语言在语言列表里并初始化 activeLang
    if (newVal) {
      if (!activeLang.value) {
        activeLang.value = newVal
      }
      if (!langList.value.includes(newVal)) {
        langList.value.push(newVal)
      }
    }
  },
  { immediate: true }
)

// 监听当前编辑的名称，实时触发校验，确保错误提示能及时消失
watch(
  () => currentFormData.value.projectName,
  () => {
    if (formRef.value) {
      formRef.value.validateField('projectName').catch(() => {})
    }
  }
)

// 配置类型与货币及类型的关联逻辑
watch(
  () => formData.value.projectConfigType,
  (val) => {
    if (val === 0) {
      formData.value.projectType = 2 // 挖矿型强制为封闭型
    } else if (val === 1) {
      // 基金型允许手动选择，默认给个开放型预览(可选)
      if (!formData.value.projectType) {
        formData.value.projectType = 1
      }
      // 基金型强制关联投资货币和收益货币
      if (formData.value.investmentCurrency) {
        formData.value.earningCurrency = formData.value.investmentCurrency
      }
    }
  },
  { immediate: true }
)

// 基金型模式下的货币强制同步
watch(
  () => formData.value.investmentCurrency,
  (val) => {
    if (formData.value.projectConfigType === 1 && val) {
      formData.value.earningCurrency = val
    }
  }
)

watch(
  () => formData.value.earningCurrency,
  (val) => {
    if (formData.value.projectConfigType === 1 && val) {
      formData.value.investmentCurrency = val
    }
  }
)

// 添加语言
const handleAddLang = (lang: string) => {
  if (!langList.value.includes(lang)) {
    langList.value.push(lang)
    // 初始化该语言的数据
    multiLangData.value[lang] = {}
    multiLangFields.forEach((field) => {
      multiLangData.value[lang][field] = ''
    })
  }
  activeLang.value = lang
}

// 移除语言
const handleRemoveLang = (lang: string) => {
  if (lang === formData.value.language) {
    message.warning('无法删除主语言')
    return
  }
  const index = langList.value.indexOf(lang)
  if (index > -1) {
    langList.value.splice(index, 1)
    delete multiLangData.value[lang]
    // 如果当前选中的是被删除的语言，切换回主语言
    if (activeLang.value === lang) {
      activeLang.value = formData.value.language!
    }
  }
}

// 为了方便，我们在 json 序列化时再转 snake_case，这里统一用 camelCase ?
// 用户要求 json 里是 snake_case: project_name
// 让我们统一一下：ui 绑定 camelCase, json存储 snake_case

const fieldMap: Record<string, string> = {
  projectName: 'project_name',
  projectIntro: 'project_intro',
  redemptionRules: 'redemption_rules',
  purchaseInstructions: 'purchase_instructions',
  dividendInstructions: 'dividend_instructions'
}

// 初始化数据
const initData = async () => {
  formLoading.value = true
  try {
    // 并行加载数据
    const [chains, fiatCoins, cryptoCoins] = await Promise.all([
      ChainApi.getActiveChainList(),
      SystemCoinApi.getFiatCoinList(),
      SystemCoinApi.getCryptoCoinList()
    ])

    chainList.value = chains
    fiatCoinList.value = fiatCoins
    cryptoCoinList.value = cryptoCoins

    // 如果是编辑模式，加载项目数据
    if (isEdit.value && projectId.value) {
      const data = await InfoApi.getInfo(projectId.value)

      // 处理图片URL
      if (data.projectImageUrls && typeof data.projectImageUrls === 'string') {
        data.projectImageUrls = data.projectImageUrls.split(',')
      }

      // 处理项目资料URL
      if (data.projectFileUrls) {
        try {
          const list = JSON.parse(data.projectFileUrls)
          data.projectFileUrls = Array.isArray(list) ? list : []
        } catch {
          data.projectFileUrls = []
        }
      }

      // 解析手续费配置
      if (data.earlyRedemptionFeeJson) {
        try {
          const json = JSON.parse(data.earlyRedemptionFeeJson)
          feeConfigList.value = Array.isArray(json)
            ? json.map((item: any) => ({ days: parseInt(item.day), ratio: parseFloat(item.value) }))
            : []
        } catch {
          feeConfigList.value = []
        }
      }

      // 解析多语言数据
      // projectJson 结构: { "en-US": { "project_name": "..." } }
      if (data.projectJson) {
        try {
          const json = JSON.parse(data.projectJson)
          // 将 snake_case 转回 camelCase 供前端使用 (如果需要)
          // 或者我们前端直接存储 snake_case 的数据在 multiLangData
          // 用户的 json 结构明确 key 是 snake_case
          // 为了简单，multiLangData 中的 value 对象 key 我们也存 snake_case ?
          // 不，template里绑定的是 v-model="formData.projectName"， camelCase
          // 所以 multiLangData 里也最好 camelCase，最后提交转 snake_case

          Object.keys(json).forEach((lang) => {
            if (lang !== data.language) {
              multiLangData.value[lang] = {}
              const langContent = json[lang]
              // 还原字段
              Object.keys(fieldMap).forEach((camelKey) => {
                const snakeKey = fieldMap[camelKey]
                if (langContent[snakeKey]) {
                  multiLangData.value[lang][camelKey] = langContent[snakeKey]
                }
              })
              if (!langList.value.includes(lang)) {
                langList.value.push(lang)
              }
            }
          })
        } catch (e) {
          console.error('解析多语言失败', e)
        }
      }

      // 确保主语言在列表
      if (data.language && !langList.value.includes(data.language)) {
        langList.value.push(data.language)
      }

      formData.value = data
      // 最后再赋值 activeLang，防止先赋值导致的 undefined 等问题
      activeLang.value = data.language || 'zh-Hant'
    } else {
      // 新增模式，默认主语言 zh-Hant
      formData.value.language = 'zh-Hant'
      activeLang.value = 'zh-Hant'
      langList.value = ['zh-Hant']
    }
  } finally {
    formLoading.value = false
  }
}

// 手续费配置操作
const addFeeConfig = () => {
  feeConfigList.value.push({ days: 0, ratio: 0 })
}

const removeFeeConfig = (index: number) => {
  feeConfigList.value.splice(index, 1)
}

// 返回列表
const handleBack = () => {
  router.push({ name: 'ProjectInfo', query: { t: Date.now().toString() } })
}

// 提交表单
const submitForm = async (auditStatus: number, projectStatus: number) => {
  if (!formRef.value) return
  // 校验表单 (Element Plus 的 validate 只会校验 model 绑定的字段，也就是 formData)
  // 如果当前 activeLang 不是 formData.language，可能校验不到必填项？
  // 实际上 formData 始终存的是“主语言”的数据，所以校验始终是针对主语言的。
  // 次要语言的必填性校验可能需要手动处理，或者暂不强制。

  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    const data = JSON.parse(JSON.stringify(formData.value))

    // 设置状态
    data.auditStatus = auditStatus
    data.projectStatus = projectStatus

    // 转换手续费配置
    if (feeConfigList.value.length > 0) {
      data.earlyRedemptionFeeJson = JSON.stringify(
        feeConfigList.value.map((item) => ({ day: String(item.days), value: String(item.ratio) }))
      )
    } else {
      data.earlyRedemptionFeeJson = ''
    }

    // 转换图片URL
    if (Array.isArray(data.projectImageUrls)) {
      data.projectImageUrls = data.projectImageUrls.join(',')
    }

    // 转换项目资料URL
    if (Array.isArray(data.projectFileUrls)) {
      data.projectFileUrls = JSON.stringify(data.projectFileUrls)
    }

    // 构建 projectJson
    // 格式: { "zh-CN": { "project_name": "..." }, "en-US": ... }
    const projectJsonObj: Record<string, any> = {}

    // 1. 添加主语言内容 (虽然字段已有，但用户要求 json 里也存一份详情？
    // "project_json用于存请外一种语言的内容" -> usually implies supplementary fields.
    // User request: "{ 'zh-CN': { ... } }" -> shows that json contains language keys.
    // "json里会存储其余语言的信息" -> sounds like primary is outside, others inside.
    // But the example shows "zh-CN" inside.
    // To be safe and complete, I will store ALL languages in projectJson, including primary.
    // Or strictly follow "others". But typically frontend wants a unified lookup.
    // Let's store OTHERS only as per "用于存请外一种语言的内容".
    // Wait, the example `{ "zh-CN": ... }` implies full structure.
    // Let's store ALL extra languages. And maybe include the primary one for completeness if redundant storage isn't an issue.
    // BUT the requirement says: "json里会存储其余语言的信息".
    // Let's stick to storing non-primary languages in `projectJson`.

    langList.value.forEach((lang) => {
      if (lang === data.language) return // 主语言字段直接在 data 根层级

      const langData: Record<string, any> = {}
      const source = multiLangData.value[lang] || {}

      // 转换 camelCase -> snake_case
      Object.keys(fieldMap).forEach((camelKey) => {
        const snakeKey = fieldMap[camelKey]
        if (source[camelKey]) {
          langData[snakeKey] = source[camelKey]
        }
      })

      if (Object.keys(langData).length > 0) {
        projectJsonObj[lang] = langData
      }
    })

    data.projectJson = JSON.stringify(projectJsonObj)

    if (isEdit.value) {
      await InfoApi.updateInfo(data)
      message.success(t('common.updateSuccess'))
    } else {
      // 新增时，剩余数量默认为发行数量
      data.remainingQuantity = data.issueQuantity
      await InfoApi.createInfo(data)
      message.success(t('common.createSuccess'))
    }

    handleBack()
  } finally {
    formLoading.value = false
  }
}

// 初始化
onMounted(() => {
  initData()
})
</script>

<style lang="scss" scoped>
.project-create-page {
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.page-header {
  background: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.form-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.form-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-card__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 16px 20px;
    border-bottom: none;
  }

  .card-header {
    display: flex;
    align-items: center;
    color: #fff;
    font-size: 16px;
    font-weight: 500;

    .header-icon {
      margin-right: 8px;
      font-size: 20px;
    }
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.coin-option {
  display: flex;
  align-items: center;

  .coin-icon {
    width: 20px;
    height: 20px;
    margin-right: 8px;
    border-radius: 50%;
  }
}

.fee-config-container {
  width: 100%;

  .fee-config-item {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    align-items: center;
  }

  .fee-input {
    width: 180px;
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.04);
  margin-top: 24px;
}

.w-full {
  width: 100%;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.mr-5px {
  margin-right: 5px;
}
.ml-5px {
  margin-left: 5px;
}

.lang-tabs {
  margin-top: 20px;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  .lang-header {
    font-size: 14px;
    font-weight: 500;
    color: #606266;
    margin-bottom: 10px;
  }

  .lang-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;

    .lang-item {
      padding: 6px 16px;
      background: #fff;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      color: #606266;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      &:hover {
        color: #409eff;
        border-color: #c6e2ff;
      }

      &.active {
        background: #409eff;
        color: #fff;
        border-color: #409eff;
      }

      .remove-icon {
        font-size: 12px;
        opacity: 0.7;
        transition: opacity 0.3s;

        &:hover {
          opacity: 1;
        }
      }
    }

    .add-lang-btn {
      padding: 6px 10px;
    }
  }
}
</style>
