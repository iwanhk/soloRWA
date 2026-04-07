<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
      :disabled="formType === 'view'"
    >
      <el-form-item label="用户id" prop="userId">
        <el-input v-model="formData.userId"  />
      </el-form-item>
<!--      <el-form-item label="消息类型" prop="noticeType">
        <el-select v-model="formData.noticeType" placeholder="请选择消息类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_NOTICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>-->
      <el-form-item label="标题" prop="templateTitle">
        <el-input v-model="formData.templateTitle" />
      </el-form-item>

      <el-form-item label="内容" prop="templateContent">
        <el-input v-model="formData.templateContent" />
      </el-form-item>

<!--      <el-form-item label="订单id" prop="orderId">
        <el-input v-model="formData.orderId" placeholder="请输入订单id" />
      </el-form-item>-->

      <el-form-item label="是否已读" prop="readStatus">
        <el-radio-group v-model="formData.readStatus">
          <el-radio :label="true">已读</el-radio>
          <el-radio :label="false">未读</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="阅读时间" prop="readTime">
        <el-date-picker
          v-model="formData.readTime"
          type="datetime"
          value-format="x"

        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading" v-if="formType !== 'view'">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { NoticeMessageApi, NoticeMessageVO } from '@/api/user/noticemessage'

/** 用户消息 表单 */
defineOptions({ name: 'NoticeMessageForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；view - 查看
const formData = ref({
  id: undefined,
  userId: undefined,
  noticeType: undefined,
  templateId: undefined,
  templateCode: undefined,
  templateNickname: undefined,
  templateContent: undefined,
  templateType: undefined,
  templateParams: undefined,
  orderId: undefined,
  noticeUrl: undefined,
  readStatus: undefined,
  readTime: undefined,
  templateTitle: undefined
})
const formRules = reactive({

})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = type === 'view' ? '查看' : t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await NoticeMessageApi.getNoticeMessage(id)
    } finally {
      formLoading.value = false
    }
  }
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
    const data = formData.value as unknown as NoticeMessageVO
    if (formType.value === 'create') {
      await NoticeMessageApi.createNoticeMessage(data)
      message.success(t('common.createSuccess'))
    } else {
      await NoticeMessageApi.updateNoticeMessage(data)
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
    userId: undefined,
    noticeType: undefined,
    templateId: undefined,
    templateCode: undefined,
    templateNickname: undefined,
    templateContent: undefined,
    templateType: undefined,
    templateParams: undefined,
    orderId: undefined,
    noticeUrl: undefined,
    readStatus: undefined,
    readTime: undefined,
    templateTitle: undefined
  }
  formRef.value?.resetFields()
}
</script>
