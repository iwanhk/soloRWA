import request from '@/config/axios'

// 用户项目余额记录 VO
export interface OrderBalanceLogVO {
  id: number // 记录ID
  userId: number // 用户ID
  projectId: number // 项目ID
  orderId: number // 订单ID
  amount: number // 操作金额
  afterAmount: number // 操作后金额
  type: number // 类型
}

// 用户项目余额记录 API
export const OrderBalanceLogApi = {
  // 查询用户项目余额记录分页
  getOrderBalanceLogPage: async (params: any) => {
    return await request.get({ url: `/project/order-balance-log/page`, params })
  },

  // 查询用户项目余额记录详情
  getOrderBalanceLog: async (id: number) => {
    return await request.get({ url: `/project/order-balance-log/get?id=` + id })
  },

  // 新增用户项目余额记录
  createOrderBalanceLog: async (data: OrderBalanceLogVO) => {
    return await request.post({ url: `/project/order-balance-log/create`, data })
  },

  // 修改用户项目余额记录
  updateOrderBalanceLog: async (data: OrderBalanceLogVO) => {
    return await request.put({ url: `/project/order-balance-log/update`, data })
  },

  // 删除用户项目余额记录
  deleteOrderBalanceLog: async (id: number) => {
    return await request.delete({ url: `/project/order-balance-log/delete?id=` + id })
  },

  // 导出用户项目余额记录 Excel
  exportOrderBalanceLog: async (params) => {
    return await request.download({ url: `/project/order-balance-log/export-excel`, params })
  }
}