<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="币种" prop="coin">
        <el-input v-model="formData.coin" placeholder="请输入币种" />
      </el-form-item>
      <el-form-item label="矿池AccessKey(加密)" prop="poolAccessKey">
        <el-input v-model="formData.poolAccessKey" placeholder="请输入矿池AccessKey(加密)" />
      </el-form-item>
      <el-form-item label="矿池私钥(加密)" prop="poolPrivateKey">
        <el-input v-model="formData.poolPrivateKey" placeholder="请输入矿池私钥(加密)" />
      </el-form-item>
      <el-form-item label="矿池子账号" prop="poolName">
        <el-input v-model="formData.poolName" placeholder="请输入矿池子账号" />
      </el-form-item>
      <el-form-item label="总算力(T)" prop="computingPower">
        <el-input v-model="formData.computingPower" placeholder="请输入总算力(T)" />
      </el-form-item>
      <el-form-item label="单T功耗(W)" prop="powerConsumption">
        <el-input v-model="formData.powerConsumption" placeholder="请输入单T功耗(W)" />
      </el-form-item>
      <el-form-item label="电价(元/度)" prop="electricityPrice">
        <el-input v-model="formData.electricityPrice" placeholder="请输入电价(元/度)" />
      </el-form-item>
      <el-form-item label="月运维成本" prop="operationCost">
        <el-input v-model="formData.operationCost" placeholder="请输入月运维成本" />
      </el-form-item>
      <el-form-item label="运维成本类型:0-固定值/月 1-按天" prop="operationCostType">
        <el-select v-model="formData.operationCostType" placeholder="请选择运维成本类型:0-固定值/月 1-按天">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="团队分成比例(%)" prop="teamShareRatio">
        <el-input v-model="formData.teamShareRatio" placeholder="请输入团队分成比例(%)" />
      </el-form-item>
<!--      <el-form-item label="最低收益阈值(日)" prop="thresholdMin">
        <el-input v-model="formData.thresholdMin" placeholder="请输入最低收益阈值(日)" />
      </el-form-item>
      <el-form-item label="最高收益阈值(日)" prop="thresholdMax">
        <el-input v-model="formData.thresholdMax" placeholder="请输入最高收益阈值(日)" />
      </el-form-item>
      <el-form-item label="阈值预警:0-关 1-开" prop="alertEnabled">
        <el-input v-model="formData.alertEnabled" placeholder="请输入阈值预警:0-关 1-开" />
      </el-form-item>-->
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { MiningConfigApi, MiningConfigVO } from '@/api/project/projectminingconfig'

/** 挖矿项目配置 表单 */
defineOptions({ name: 'MiningConfigForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  projectId: undefined,
  coin: undefined,
  poolAccessKey: undefined,
  poolPrivateKey: undefined,
  poolName: undefined,
  computingPower: undefined,
  powerConsumption: undefined,
  electricityPrice: undefined,
  operationCost: undefined,
  operationCostType: undefined,
  teamShareRatio: undefined,
  thresholdMin: undefined,
  thresholdMax: undefined,
  alertEnabled: undefined
})
const formRules = reactive({
  coin: [{ required: true, message: '币种不能为空', trigger: 'blur' }],
  operationCostType: [{ required: true, message: '运维成本类型:0-固定值/月 1-按天不能为空', trigger: 'change' }],
  teamShareRatio: [{ required: true, message: '团队分成比例(%)不能为空', trigger: 'blur' }],
  alertEnabled: [{ required: true, message: '阈值预警:0-关 1-开不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await MiningConfigApi.getMiningConfig(id)
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
    const data = formData.value as unknown as MiningConfigVO
    if (formType.value === 'create') {
      await MiningConfigApi.createMiningConfig(data)
      message.success(t('common.createSuccess'))
    } else {
      await MiningConfigApi.updateMiningConfig(data)
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
    projectId: undefined,
    coin: undefined,
    poolAccessKey: undefined,
    poolPrivateKey: undefined,
    poolName: undefined,
    computingPower: undefined,
    powerConsumption: undefined,
    electricityPrice: undefined,
    operationCost: undefined,
    operationCostType: undefined,
    teamShareRatio: undefined,
    thresholdMin: undefined,
    thresholdMax: undefined,
    alertEnabled: undefined
  }
  formRef.value?.resetFields()
}
</script>
