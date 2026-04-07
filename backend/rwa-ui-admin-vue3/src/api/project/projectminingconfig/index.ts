import request from '@/config/axios'

// 挖矿项目配置 VO
export interface MiningConfigVO {
  projectId: number // 项目ID(主键)
  coin: string // 币种
  poolAccessKey: string // 矿池AccessKey(加密)
  poolPrivateKey: string // 矿池私钥(加密)
  poolName: string // 矿池子账号
  computingPower: number // 总算力(T)
  powerConsumption: number // 单T功耗(W)
  electricityPrice: number // 电价(元/度)
  operationCost: number // 月运维成本
  operationCostType: number // 运维成本类型:0-固定值/月 1-按天
  teamShareRatio: number // 团队分成比例(%)
  thresholdMin: number // 最低收益阈值(日)
  thresholdMax: number // 最高收益阈值(日)
  alertEnabled: number // 阈值预警:0-关 1-开
}

// 挖矿项目配置 API
export const MiningConfigApi = {
  // 查询挖矿项目配置分页
  getMiningConfigPage: async (params: any) => {
    return await request.get({ url: `/project/mining-config/page`, params })
  },

  // 查询挖矿项目配置详情
  getMiningConfig: async (id: number) => {
    return await request.get({ url: `/project/mining-config/get?id=` + id })
  },

  // 新增挖矿项目配置
  createMiningConfig: async (data: MiningConfigVO) => {
    return await request.post({ url: `/project/mining-config/create`, data })
  },

  // 修改挖矿项目配置
  updateMiningConfig: async (data: MiningConfigVO) => {
    return await request.put({ url: `/project/mining-config/update`, data })
  },

  // 删除挖矿项目配置
  deleteMiningConfig: async (id: number) => {
    return await request.delete({ url: `/project/mining-config/delete?id=` + id })
  },

  // 导出挖矿项目配置 Excel
  exportMiningConfig: async (params) => {
    return await request.download({ url: `/project/mining-config/export-excel`, params })
  }
}