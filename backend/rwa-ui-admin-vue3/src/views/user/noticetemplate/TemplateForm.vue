<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="模板编码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入模板编码" />
      </el-form-item>
      <el-form-item label="模板标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入模板标题，使用 ${key} 作为占位符" />
      </el-form-item>
      <el-form-item label="模板内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="4"
          placeholder="请输入模板内容，使用 ${key} 作为占位符"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_NOTICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="参数数组" prop="params">
        <el-input
          v-model="formData.params"
          placeholder="请输入参数数组，如：userName,auditTime"
        />
      </el-form-item>
<!--      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :label="0">禁用</el-radio>
          <el-radio :label="1">启用</el-radio>
        </el-radio-group>
      </el-form-item>-->
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { NoticeTemplateApi, NoticeTemplateVO } from '@/api/user/noticetemplate'
import {DICT_TYPE,getIntDictOptions} from "@/utils/dict";

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  name: '',
  code: '',
  nickname: '',
  content: '',
  type: undefined,
  params: '',
  status: 1,
  remark: '',
  title: ''
})
const formRules = reactive({
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '模板标题不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '模板内容不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'create' ? '添加消息模板' : '修改消息模板'
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await NoticeTemplateApi.getNoticeTemplate(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])

const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as unknown as NoticeTemplateVO
    if (formType.value === 'create') {
      await NoticeTemplateApi.createNoticeTemplate(data)
      message.success(t('common.createSuccess'))
    } else {
      await NoticeTemplateApi.updateNoticeTemplate(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    name: '',
    code: '',
    nickname: '',
    content: '',
    type: undefined,
    params: '',
    status: 1,
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
