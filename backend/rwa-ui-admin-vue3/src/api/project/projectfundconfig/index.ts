import request from '@/config/axios'

// 基金项目配置 VO
export interface FundConfigVO {
  projectId: number // 项目ID(主键)
  operationCost: number // 月运维成本
  operationCostType: number // 运维成本类型:0-固定值/月 1-按天
  teamShareRatio: number // 团队分成比例(%)
  thresholdMin: number // 最低收益阈值(日)
  thresholdMax: number // 最高收益阈值(日)
  alertEnabled: number // 阈值预警:0-关 1-开
  fundJson: string // 基金公司配置
}

// 基金项目配置 API
export const FundConfigApi = {
  // 查询基金项目配置分页
  getFundConfigPage: async (params: any) => {
    return await request.get({ url: `/project/fund-config/page`, params })
  },

  // 查询基金项目配置详情
  getFundConfig: async (id: number) => {
    return await request.get({ url: `/project/fund-config/get?id=` + id })
  },

  // 新增基金项目配置
  createFundConfig: async (data: FundConfigVO) => {
    return await request.post({ url: `/project/fund-config/create`, data })
  },

  // 修改基金项目配置
  updateFundConfig: async (data: FundConfigVO) => {
    return await request.put({ url: `/project/fund-config/update`, data })
  },

  // 删除基金项目配置
  deleteFundConfig: async (id: number) => {
    return await request.delete({ url: `/project/fund-config/delete?id=` + id })
  },

  // 导出基金项目配置 Excel
  exportFundConfig: async (params) => {
    return await request.download({ url: `/project/fund-config/export-excel`, params })
  }
}