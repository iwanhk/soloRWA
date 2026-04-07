<template>
  <div v-if="!disabled" class="upload-file">
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="uploadUrl"
      :auto-upload="autoUpload"
      :before-upload="beforeUpload"
      :drag="drag"
      :http-request="httpRequest"
      :limit="props.limit"
      :disabled="disabled || isUploading"
      :multiple="props.limit > 1"
      :on-error="excelUploadError"
      :on-exceed="handleExceed"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="handleFileSuccess"
      :show-file-list="true"
      class="upload-file-uploader"
      name="file"
    >
      <el-button type="primary" :disabled="disabled || isUploading">
        <Icon icon="ep:upload-filled" />
        选取文件
      </el-button>
      <template v-if="isShowTip" #tip>
        <div style="font-size: 12px">
          大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
        </div>
        <div style="font-size: 12px">
          格式为 <b style="color: #f56c6c">{{ fileType.join('/') }}</b> 的文件
        </div>
      </template>
      <template #file="row">
        <div class="flex flex-col w-full">
          <div class="flex items-center justify-between w-full">
            <div class="flex items-center">
              <Icon icon="ep:document" class="mr-5px" />
              <span>{{ row.file.name }}</span>
            </div>
            <div class="flex items-center">
              <div v-if="row.file.status !== 'uploading'" class="ml-10px">
                <el-link
                  :href="row.file.url"
                  :underline="false"
                  download
                  target="_blank"
                  type="primary"
                >
                  下载
                </el-link>
              </div>
              <div v-if="!disabled" class="ml-10px">
                <el-button link type="danger" @click="handleRemove(row.file)"> 删除</el-button>
              </div>
            </div>
          </div>
          <el-progress 
            v-if="row.file.status === 'uploading'" 
            :percentage="Math.round(row.file.percentage || 0)" 
            :stroke-width="2"
            class="mt-5px"
          />
        </div>
      </template>
    </el-upload>
  </div>

  <!-- 上传操作禁用时 -->
  <div v-if="disabled" class="upload-file">
    <div v-for="(file, index) in fileList" :key="index" class="flex items-center file-list-item">
      <span>{{ file.name }}</span>
      <div class="ml-10px">
        <el-link :href="file.url" :underline="false" download target="_blank" type="primary">
          下载
        </el-link>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { propTypes } from '@/utils/propTypes'
import type { UploadProps, UploadRawFile, UploadUserFile } from 'element-plus'
import { isString } from '@/utils/is'
import { useUploadDTO } from '@/components/UploadFile/src/useUploadDTO'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'

defineOptions({ name: 'UploadFileDTO' })

const message = useMessage() // 消息弹窗
const emit = defineEmits(['update:modelValue'])

const props = defineProps({
  modelValue: propTypes.oneOfType<string | any[]>([String, Array]).isRequired,
  fileType: propTypes.array.def(['doc', 'xls', 'ppt', 'txt', 'pdf', 'zip', 'rar', 'mp4']), // 文件类型
  fileSize: propTypes.number.def(50), // 大小限制(MB)
  limit: propTypes.number.def(30), // 数量限制
  autoUpload: propTypes.bool.def(true), // 自动上传
  drag: propTypes.bool.def(false), // 拖拽上传
  isShowTip: propTypes.bool.def(true), // 是否显示提示
  disabled: propTypes.bool.def(false), // 是否禁用上传组件
  directory: propTypes.string.def(undefined) // 上传目录
})

// ========== 上传相关 ==========
const uploadList = ref<UploadUserFile[]>([])
const fileList = ref<UploadUserFile[]>([])
const uploadNumber = ref<number>(0)

const isUploading = computed(() => uploadNumber.value > 0)

const { uploadUrl, httpRequest } = useUploadDTO(props.directory)

// 文件上传之前判断
const beforeUpload: UploadProps['beforeUpload'] = (file: UploadRawFile) => {
  if (fileList.value.length >= props.limit) {
    message.error(`上传文件数量不能超过${props.limit}个!`)
    return false
  }
  let fileExtension = ''
  if (file.name.lastIndexOf('.') > -1) {
    fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1)
  }
  const isImg = props.fileType.some((type: string) => {
    if (file.type.indexOf(type) > -1) return true
    return !!(fileExtension && fileExtension.indexOf(type) > -1)
  })
  const isLimit = file.size < props.fileSize * 1024 * 1024
  if (!isImg) {
    message.error(`文件格式不正确, 请上传${props.fileType.join('/')}格式!`)
    return false
  }
  if (!isLimit) {
    message.error(`上传文件大小不能超过${props.fileSize}MB!`)
    return false
  }
  message.success('正在上传文件，请稍候...')
  uploadNumber.value++
  return true
}

// 文件上传成功
const handleFileSuccess: UploadProps['onSuccess'] = (res: any): void => {
  message.success('上传成功')
  const data = res.data // {id, name, url}
  // 删除自身
  const index = fileList.value.findIndex((item) => (item.response as any)?.data === res.data)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
  
  uploadList.value.push({ name: data.name, url: data.url, ...data }) // 保存完整对象
  
  if (uploadList.value.length == uploadNumber.value) {
    // 过滤掉 el-upload 自动添加的原始文件（它们没有 url 或者 url 不是我们需要的那种）
    // 或者直接把 uploadList 的内容赋给 fileList (append)
    
    // 这里采取一种简单的策略：
    // 因为 el-upload 在 onSuccess 时可能还没把 file 放到 fileList 里 (或者 put 了一个 raw file)
    // 我们手动管理 modelValue，所以只需 update modelValue
    
    // 但是 fileList 用于显示，需要包含
    // 简单起见，我们将后端返回的 data 追加到 fileList
    // 但是要注意去重，因为 el-upload 可能会有本地预览的 file
    
    // 更好的做法：忽略 el-upload 的自动列表管理，完全由 modelValue 控制？
    // 不，利用 modelValue 同步 fileList
    
    // 将新上传的文件加入 fileList
    // fileList.value.push(...uploadList.value) // 这可能会导致重复，如果 el-upload 已经加了
    
    // 让我们清洗一下 fileList：移除没有 url 的（正在上传的），换成 uploadList 中的
    
    // 触发 update
    fileList.value = fileList.value.filter(f => f.url && f.url.startsWith('http'))
    fileList.value.push(...uploadList.value)
    
    uploadList.value = []
    uploadNumber.value = 0
    emitUpdateModelValue()
  }
}

// 文件数超出提示
const handleExceed: UploadProps['onExceed'] = (): void => {
  message.error(`上传文件数量不能超过${props.limit}个!`)
}

// 上传错误提示
const excelUploadError: UploadProps['onError'] = (): void => {
  message.error('导入数据失败，请您重新上传！')
  uploadNumber.value = Math.max(0, uploadNumber.value - 1)
}

// 删除上传文件
const handleRemove = (file: UploadFile) => {
  const index = fileList.value.findIndex((f) => f.url === file.url)
  if (index > -1) {
    fileList.value.splice(index, 1)
    emitUpdateModelValue()
  }
}

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  console.log(uploadFile)
}

// 监听模型绑定值变动
watch(
  () => props.modelValue,
  (val: string | any[]) => {
    if (!val) {
      fileList.value = []
      return
    }

    fileList.value = []
    // 情况1：JSON 字符串
    if (isString(val)) {
      try {
        const list = JSON.parse(val)
        if (Array.isArray(list)) {
            fileList.value.push(...list)
        }
      } catch (e) {
          // 兼容旧数据（纯URL字符串）
          fileList.value.push(
              ...val.split(',').map((url) => ({ name: url.substring(url.lastIndexOf('/') + 1), url }))
          )
      }
      return
    }
    // 情况2：数组 (直接对象数组)
    if (Array.isArray(val)) {
       fileList.value.push(...val)
    }
  },
  { immediate: true, deep: true }
)

// 发送更新
const emitUpdateModelValue = () => {
  // 我们总是发射对象数组，由父组件决定是否 stringify
  // 或者为了兼容 ProjectInfo projectFileUrls 是 string，我们在这里 stringify?
  // 用户说 "最后传个后端包含名称和url和id的json"
  // 如果父组件绑定的是数组，我们 emit 数组。如果父组件绑定的是 string，我们 emit string (JSON).
  
   const list = fileList.value.map((file) => ({
      id: (file as any).id, // 可能有 id
      name: file.name,
      url: file.url
   }))
   
   emit('update:modelValue', list)
}
</script>
<style lang="scss" scoped>
.upload-file-uploader {
  margin-bottom: 5px;
}

:deep(.upload-file-list .el-upload-list__item) {
  position: relative;
  margin-bottom: 10px;
  line-height: 2;
  border: 1px solid #e4e7ed;
}

:deep(.el-upload-list__item-file-name) {
  max-width: 250px;
}

:deep(.upload-file-list .ele-upload-list__item-content) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}

:deep(.ele-upload-list__item-content-action .el-link) {
  margin-right: 10px;
}

.file-list-item {
  border: 1px dashed var(--el-border-color-darker);
  border-radius: 8px;
}
</style>
