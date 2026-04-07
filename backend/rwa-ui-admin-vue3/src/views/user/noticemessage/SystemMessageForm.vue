<template>
  <Dialog title="发布系统消息" v-model="dialogVisible" width="600px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="消息标题" prop="templateTitle">
        <el-input v-model="formData.templateTitle" placeholder="请输入消息标题" />
      </el-form-item>

      <el-form-item label="消息内容" prop="templateContent">
        <el-input
          v-model="formData.templateContent"
          type="textarea"
          :rows="6"
          placeholder="请输入消息内容"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">发 布</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { NoticeMessageApi } from '@/api/user/noticemessage'

/** 发布系统消息 表单 */
defineOptions({ name: 'SystemMessageForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const formData = ref({
  noticeType: 1, // 系统消息类型
  templateTitle: '',
  templateContent: ''
})
const formRules = reactive({
  templateTitle: [{ required: true, message: '消息标题不能为空', trigger: 'blur' }],
  templateContent: [{ required: true, message: '消息内容不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    await NoticeMessageApi.createNoticeMessage(formData.value as any)
    message.success('系统消息发布成功')
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
    noticeType: 1,
    templateTitle: '',
    templateContent: ''
  }
  formRef.value?.resetFields()
}
</script>
