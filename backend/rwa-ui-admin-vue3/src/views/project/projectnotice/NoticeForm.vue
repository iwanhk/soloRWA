<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
<!--      <el-form-item label="通告编号" >
        <el-input v-model="formData.noticeNo" disabled/>
      </el-form-item>-->
      <el-form-item label="项目" prop="projectId">
        <el-select v-model="formData.projectId" placeholder="请选择项目" filterable clearable>
          <el-option
            v-for="item in projectList"
            :key="item.projectId"
            :label="item.projectName"
            :value="item.projectId"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="主语言" prop="language">
        <el-select v-model="formData.language" placeholder="请选择主语言" :disabled="!!formData.id">
           <el-option
             v-for="dict in getStrDictOptions(DICT_TYPE.BIZ_LANGUAGE)"
             :key="dict.value"
             :label="dict.label"
             :value="dict.value"
           />
        </el-select>
      </el-form-item>

      <div class="lang-tabs" v-if="formData.language" style="margin-bottom: 20px; margin-left: 100px;">
        <div class="lang-list" style="display: flex; align-items: center; flex-wrap: wrap; gap: 10px;">
           <el-tag
             v-for="lang in langList" 
             :key="lang" 
             :effect="activeLang === lang ? 'dark' : 'plain'"
             @click="activeLang = lang"
             style="cursor: pointer; user-select: none;"
             :closable="lang !== formData.language"
             @close="handleRemoveLang(lang)"
           >
             {{ dictLangLabel(lang) }}
           </el-tag>
           
           <el-dropdown trigger="click" @command="handleAddLang">
             <el-button type="primary" link icon="ep:plus" class="add-lang-btn">添加语言</el-button>
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

      <el-form-item label="通告标题" prop="noticeTitle">
        <el-input v-model="currentFormData.noticeTitle" placeholder="请输入通告标题" />
      </el-form-item>

      <el-form-item label="通告内容" prop="noticeContent">
        <Editor v-model="currentFormData.noticeContent" height="150px" />
      </el-form-item>
      <el-form-item label="附件" prop="attachUrls">
        <UploadFileDTO v-model="formData.attachUrls" />
      </el-form-item>
<!--      <el-form-item label="发布人ID（关联sys_user.id）" prop="publishUserId">
        <el-input v-model="formData.publishUserId" placeholder="请输入发布人ID（关联sys_user.id）" />
      </el-form-item>
      <el-form-item label="发布人名称（冗余）" prop="publishUserName">
        <el-input v-model="formData.publishUserName" placeholder="请输入发布人名称（冗余）" />
      </el-form-item>-->
<!--      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker
          v-model="formData.publishTime"
          type="date"
          value-format="x"
          placeholder="选择发布时间"
        />
      </el-form-item>-->
<!--      <el-form-item label="开始展示时间（NULL表示立即展示）" prop="showStartTime">
        <el-date-picker
          v-model="formData.showStartTime"
          type="date"
          value-format="x"
          placeholder="选择开始展示时间（NULL表示立即展示）"
        />
      </el-form-item>
      <el-form-item label="结束展示时间（NULL表示永久展示）" prop="showEndTime">
        <el-date-picker
          v-model="formData.showEndTime"
          type="date"
          value-format="x"
          placeholder="选择结束展示时间（NULL表示永久展示）"
        />
      </el-form-item>-->
<!--      <el-form-item label="是否置顶：1-是 0-否（同一项目/全局仅1个置顶）" prop="isTop">
        <el-radio-group v-model="formData.isTop">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>-->
<!--      <el-form-item label="通告状态" prop="noticeStatus">
        <el-select v-model="formData.noticeStatus" placeholder="请选择通告状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_PROJECT_NOTICE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
<!--      <el-form-item label="阅读次数" prop="readCount">
        <el-input v-model="formData.readCount" placeholder="请输入阅读次数" />
      </el-form-item>
      <el-form-item label="是否弹窗展示：1-是 0-否（用户进入页面时弹窗）" prop="isPopup">
        <el-radio-group v-model="formData.isPopup">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注（仅运营可见，如“临时通告，3天后下架”）" prop="remark">
        <el-input v-model="formData.remark" placeholder="请输入备注（仅运营可见，如“临时通告，3天后下架”）" />
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm(2)" type="primary" :disabled="formLoading">发 布</el-button>
      <el-button @click="submitForm(1)" type="info" :disabled="formLoading">存为草稿</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, getStrDictOptions, DICT_TYPE } from '@/utils/dict'
import { NoticeApi, NoticeVO } from '@/api/project/projectnotice'
import { InfoApi, InfoSimpleVO } from '@/api/project/projectinfo'
import UploadFileDTO from '@/components/UploadFile/src/UploadFileDTO.vue'

/** 项目通告表（含全局通告） 表单 */
defineOptions({ name: 'NoticeForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const projectList = ref<InfoSimpleVO[]>([]) // 项目列表
const formData = ref({
  id: undefined,
  noticeNo: undefined,
  projectId: undefined,
  projectName: undefined,
  language: 'zh-Hant', // 默认主语言
  noticeJson: '', // 多语言JSON
  noticeTitle: undefined,
  noticeType: undefined,
  noticeContent: undefined,
  attachUrls: undefined,
  publishUserId: undefined,
  publishUserName: undefined,
  publishTime: undefined,
  showStartTime: undefined,
  showEndTime: undefined,
  isTop: undefined,
  noticeStatus: undefined,
  readCount: undefined,
  isPopup: undefined,
  remark: undefined
})
const formRules = reactive({
  //noticeNo: [{ required: true, message: '通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）不能为空', trigger: 'blur' }],
  projectId: [{ required: true, message: '项目不能为空', trigger: 'blur' }],
  //projectName: [{ required: true, message: '关联项目名称（冗余，0时为“全局通告”）不能为空', trigger: 'blur' }],
  language: [{ required: true, message: '主语言不能为空', trigger: 'change' }],
  noticeTitle: [{ required: true, message: '通告标题不能为空', trigger: 'blur' }],
  //noticeType: [{ required: true, message: '通告类型不能为空', trigger: 'change' }],
  noticeContent: [{ required: true, message: '通告内容不能为空', trigger: 'blur' }],
  //publishUserId: [{ required: true, message: '发布人ID（关联sys_user.id）不能为空', trigger: 'blur' }],
  //publishUserName: [{ required: true, message: '发布人名称（冗余）不能为空', trigger: 'blur' }],
  //publishTime: [{ required: true, message: '发布时间不能为空', trigger: 'blur' }],
  //isTop: [{ required: true, message: '是否置顶：1-是 0-否（同一项目/全局仅1个置顶）不能为空', trigger: 'blur' }],
  //noticeStatus: [{ required: true, message: '通告状态不能为空', trigger: 'change' }],
  //readCount: [{ required: true, message: '阅读次数不能为空', trigger: 'blur' }],
  //isPopup: [{ required: true, message: '是否弹窗展示：1-是 0-否（用户进入页面时弹窗）不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

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
  'noticeTitle',
  'noticeContent'
]

// 字段名映射 camelCase <-> snake_case
const fieldMap: Record<string, string> = {
  noticeTitle: 'notice_title',
  noticeContent: 'notice_content'
}

// 监听语言变化，初始化 activeLang
watch(() => formData.value.language, (val) => {
  if (val && !activeLang.value) {
    activeLang.value = val
    if (!langList.value.includes(val)) {
      langList.value.push(val)
    }
  }
}, { immediate: true })

// 添加语言
const handleAddLang = (lang: string) => {
  if (!langList.value.includes(lang)) {
    langList.value.push(lang)
    // 初始化该语言的数据
    multiLangData.value[lang] = {}
    multiLangFields.forEach(field => {
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

/** 打开弹窗 */
const open = async (type: string, id?: number, projectId?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  
  // 加载项目列表
  if (projectList.value.length === 0) {
    projectList.value = await InfoApi.getInfoSimple()
  }
  
  // 如果是新增且有projectId，设置默认值
  if (type === 'create' && projectId) {
    formData.value.projectId = projectId
  }

  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await NoticeApi.getNotice(id)
      formData.value = data
      
      // 处理附件
      if (formData.value.attachUrls) {
          try {
             const list = JSON.parse(formData.value.attachUrls)
             if(Array.isArray(list)){
                formData.value.attachUrls = list
             } else {
                 if (typeof formData.value.attachUrls === 'string') {
                    // @ts-ignore
                    formData.value.attachUrls = formData.value.attachUrls.split(',').map((url) => ({
                      name: url.substring(url.lastIndexOf('/') + 1),
                      url
                    }))
                }
             }
          } catch(e) {
               if (typeof formData.value.attachUrls === 'string') {
                    // @ts-ignore
                    formData.value.attachUrls = formData.value.attachUrls.split(',').map((url) => ({
                      name: url.substring(url.lastIndexOf('/') + 1),
                      url
                    }))
               }
          }
      } else {
          formData.value.attachUrls = []
      }

      // 处理多语言
      if (data.noticeJson) {
        try {
          const json = JSON.parse(data.noticeJson)
          Object.keys(json).forEach(lang => {
             if (lang !== data.language) {
               multiLangData.value[lang] = {}
               const langContent = json[lang]
               // 还原字段
               Object.keys(fieldMap).forEach(camelKey => {
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
          console.error("解析多语言失败", e)
        }
      }
      
      // 确保主语言在列表
      if (data.language && !langList.value.includes(data.language)) {
        langList.value.push(data.language)
      }
      activeLang.value = data.language || 'zh-Hant'
      
    } finally {
      formLoading.value = false
    }
  } else {
      // 新增模式
      formData.value.language = 'zh-Hant'
      activeLang.value = 'zh-Hant'
      langList.value = ['zh-Hant']
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async (status: number) => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = JSON.parse(JSON.stringify(formData.value)) as unknown as NoticeVO
    if (data.attachUrls) {
      // @ts-ignore
      data.attachUrls = JSON.stringify(data.attachUrls)
    }
    data.noticeStatus = status
    
    // 构建 noticeJson
    const noticeJsonObj: Record<string, any> = {}
    langList.value.forEach(lang => {
        if (lang === data.language) return // 主语言字段直接在 data 根层级
        
        const langData: Record<string, any> = {}
        const source = multiLangData.value[lang] || {}
        
        // 转换 camelCase -> snake_case
        Object.keys(fieldMap).forEach(camelKey => {
            const snakeKey = fieldMap[camelKey]
            if (source[camelKey]) {
                langData[snakeKey] = source[camelKey]
            }
        })
        
        if (Object.keys(langData).length > 0) {
            noticeJsonObj[lang] = langData
        }
    })
    data.noticeJson = JSON.stringify(noticeJsonObj)

    if (formType.value === 'create') {
      await NoticeApi.createNotice(data)
      message.success(t('common.createSuccess'))
    } else {
      await NoticeApi.updateNotice(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    noticeNo: undefined,
    projectId: undefined,
    projectName: undefined,
    language: 'zh-Hant',
    noticeJson: '',
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    attachUrls: undefined,
    publishUserId: undefined,
    publishUserName: undefined,
    publishTime: undefined,
    showStartTime: undefined,
    showEndTime: undefined,
    isTop: undefined,
    noticeStatus: undefined,
    readCount: undefined,
    isPopup: undefined,
    remark: undefined
  }
  activeLang.value = 'zh-Hant'
  langList.value = ['zh-Hant']
  multiLangData.value = {}
  formRef.value?.resetFields()
}
</script>
