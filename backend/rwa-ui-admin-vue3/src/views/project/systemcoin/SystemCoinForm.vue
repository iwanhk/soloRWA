<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="币种标识" prop="coinCode">
        <el-input v-model="formData.coinCode" placeholder="请输入币种标识" />
      </el-form-item>
      <el-form-item label="币种名称" prop="coinName">
        <el-input v-model="formData.coinName" placeholder="请输入币种名称" />
      </el-form-item>
      <el-form-item label="英文名称" prop="coinNameEn">
        <el-input v-model="formData.coinNameEn" placeholder="请输入英文名称" />
      </el-form-item>
      <el-form-item label="币种类型" prop="coinType">
        <el-select v-model="formData.coinType" placeholder="请选择币种类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BIZ_COIN_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="显示符号" prop="symbol">
        <el-input v-model="formData.symbol" placeholder="请输入显示符号" />
      </el-form-item>
      <el-form-item label="币安交易对符号(如BTCUSDT)" prop="binanceSymbol">
        <el-input v-model="formData.binanceSymbol" placeholder="请输入币安交易对符号(如BTCUSDT)" />
      </el-form-item>
      <el-form-item label="矿池币种符号(如btc)" prop="poolSymbol">
        <el-input v-model="formData.poolSymbol" placeholder="请输入矿池币种符号(如btc)" />
      </el-form-item>
      <el-form-item label="币种图标" prop="iconUrl">
        <el-input v-model="formData.iconUrl" placeholder="请输入币种图标" />
      </el-form-item>
      <el-form-item label="小数精度" prop="decimals">
        <el-input v-model="formData.decimals" placeholder="请输入小数精度" />
      </el-form-item>
      <el-form-item label="排序(越小越前)" prop="sort">
        <el-input v-model="formData.sort" placeholder="请输入排序(越小越前)" />
      </el-form-item>
      <el-form-item label="状态:0-禁用 1-启用" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio value="1">请选择字典生成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { SystemCoinApi, SystemCoinVO } from '@/api/project/systemcoin'

/** 币种管理 表单 */
defineOptions({ name: 'SystemCoinForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  coinCode: undefined,
  coinName: undefined,
  coinNameEn: undefined,
  coinType: undefined,
  symbol: undefined,
  binanceSymbol: undefined,
  poolSymbol: undefined,
  iconUrl: undefined,
  decimals: undefined,
  sort: undefined,
  status: undefined,
  remark: undefined
})
const formRules = reactive({
  coinCode: [{ required: true, message: '币种标识不能为空', trigger: 'blur' }],
  coinName: [{ required: true, message: '币种名称不能为空', trigger: 'blur' }],
  coinType: [{ required: true, message: '币种类型不能为空', trigger: 'change' }],
  decimals: [{ required: true, message: '小数精度不能为空', trigger: 'blur' }],
  sort: [{ required: true, message: '排序(越小越前)不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态:0-禁用 1-启用不能为空', trigger: 'blur' }]
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
      formData.value = await SystemCoinApi.getSystemCoin(id)
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
    const data = formData.value as unknown as SystemCoinVO
    if (formType.value === 'create') {
      await SystemCoinApi.createSystemCoin(data)
      message.success(t('common.createSuccess'))
    } else {
      await SystemCoinApi.updateSystemCoin(data)
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
    coinCode: undefined,
    coinName: undefined,
    coinNameEn: undefined,
    coinType: undefined,
    symbol: undefined,
    binanceSymbol: undefined,
    poolSymbol: undefined,
    iconUrl: undefined,
    decimals: undefined,
    sort: undefined,
    status: undefined,
    remark: undefined
  }
  formRef.value?.resetFields()
}
</script>