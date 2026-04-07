import request from '@/config/axios'

// 用户项目余额表 VO
export interface OrderBalanceVO {
  id: number // 记录ID
  userId: number // 用户ID
  projectId: number // 项目ID
  orderId: number // 订单ID
  principalAmount: number // 本金金额(U)
  holdAmount: number // 持有金额
  buyQuantity: number // 购买份额
  holdQuantity: number // 当前持有份额(份)（赎回后扣减）
  totalIncome: number // 累计总收益(U)（含未提取）
  withdrawnDividend: number // 已提取分红(U)
  freezeDividend: string // 冻结的分红(U)
  unwithdrawnDividend: number // 未提取分红(U)（=总收益-已提取）
  totalRedemptionAmount: number // 累计赎回本金(U)（赎回时累加）
  lastIncomeCalcTime: Date // 最后一次收益计算时间
  lastWithdrawTime: Date // 最后一次分红提取时间
}

// 用户项目余额表 API
export const OrderBalanceApi = {
  // 查询用户项目余额表分页
  getOrderBalancePage: async (params: any) => {
    return await request.get({ url: `/project/order-balance/page`, params })
  },

  // 查询用户项目余额表详情
  getOrderBalance: async (id: number) => {
    return await request.get({ url: `/project/order-balance/get?id=` + id })
  },

  // 新增用户项目余额表
  createOrderBalance: async (data: OrderBalanceVO) => {
    return await request.post({ url: `/project/order-balance/create`, data })
  },

  // 修改用户项目余额表
  updateOrderBalance: async (data: OrderBalanceVO) => {
    return await request.put({ url: `/project/order-balance/update`, data })
  },

  // 删除用户项目余额表
  deleteOrderBalance: async (id: number) => {
    return await request.delete({ url: `/project/order-balance/delete?id=` + id })
  },

  // 导出用户项目余额表 Excel
  exportOrderBalance: async (params) => {
    return await request.download({ url: `/project/order-balance/export-excel`, params })
  }
}
