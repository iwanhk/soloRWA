import request from '@/config/axios'

// 项目收益 VO
export interface RevenueVO {
  id: number // ID
  revenueDate: Date // 收益日期
  revenue: number // 总收益
  projectId: number // 产品id
  projectName: string // 产品名称
  electricityCost?: number // 电力成本
  peopleCost?: number // 人力成本
  availableRevenue?: number // 可发放收益
  isSend: number // 是否发放收益
  isSync: number // 是否同步收益
  remark: string // 备注
  coinRevenue?: number // 币种收益
  projectRevenue?: number // 项目收益
  exchangeRate?: number // 汇率
  coin?: string // 币种
}

// 成本计算结果 VO
export interface CalculateCostsVO {
  electricityCost: number // 电力成本
  peopleCost: number // 人力成本
  availableRevenue: number // 可发放收益
  projectRevenue?: number // 项目方收益
}

// 矿池收益计算结果 VO
export interface PoolRevenueCalcVO {
  coinRevenue: number
  exchangeRate: number
  revenue: number
  coin: string
}

// 项目收益 API
export const RevenueApi = {
  // 查询项目收益分页
  getRevenuePage: async (params: any) => {
    return await request.get({ url: `/project/revenue/page`, params })
  },

  // 查询项目收益详情
  getRevenue: async (id: number) => {
    return await request.get({ url: `/project/revenue/get?id=` + id })
  },

  // 新增项目收益
  createRevenue: async (data: RevenueVO) => {
    return await request.post({ url: `/project/revenue/create`, data })
  },

  // 修改项目收益
  updateRevenue: async (data: RevenueVO) => {
    return await request.put({ url: `/project/revenue/update`, data })
  },

  // 删除项目收益
  deleteRevenue: async (id: number) => {
    return await request.delete({ url: `/project/revenue/delete?id=` + id })
  },

  // 导出项目收益 Excel
  exportRevenue: async (params) => {
    return await request.download({ url: `/project/revenue/export-excel`, params })
  },

  // 自动计算成本
  calculateCosts: async (projectId: number, revenue: number, revenueDate: string): Promise<CalculateCostsVO> => {
    return await request.get({ url: `/project/revenue/calculate-costs`, params: { projectId, revenue, revenueDate } })
  },

  // 获取矿池收益并计算
  calculatePoolRevenue: async (projectId: number, revenueDate: string): Promise<PoolRevenueCalcVO> => {
    return await request.get({
      url: `/project/revenue/calculate-pool-revenue`,
      params: { projectId, revenueDate }
    })
  },

  // 修改可发放收益
  updateAvailableRevenue: async (data: RevenueVO) => {
    return await request.put({ url: `/project/revenue/update-available-revenue`, data })
  },

  // 手动模拟生成单日收益
  generateManualRevenue: async (params: { projectId: number; revenueDate: string; revenue: number }) => {
    return await request.post({ url: `/project/revenue/test/generate-manual`, params })
  },

  // 获取币种汇率
  getCoinUsdRate: async (coinCode?: string): Promise<number> => {
    return await request.get({ url: `/project/revenue/get-coin-usd-rate`, params: { coinCode } })
  }
}
