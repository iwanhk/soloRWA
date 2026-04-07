import request from '@/config/axios'

// 订单每日收益统计 VO
export interface OrderDailyIncomeVO {
  id: number // 记录ID
  orderId: number // 订单id
  orderNo: string // 订单号
  userId: number // 用户ID
  projectId: number // 项目ID
  incomeDate: Date // 收益日期
  projectRevenueId: number // 收益id
  holdQuantity: number // 当日持有份额
  issueTime: Date // 收益发放时间
  dailyIncome: number // 当日收益
  incomeRate: number // 当日收益率
  cumulativeIncome: number // 累计收益
  status: number // 收益状态
}

// 订单每日收益统计 API
export const OrderDailyIncomeApi = {
  // 查询订单每日收益统计分页
  getOrderDailyIncomePage: async (params: any) => {
    return await request.get({ url: `/project/order-daily-income/page`, params })
  },

  // 查询订单每日收益统计详情
  getOrderDailyIncome: async (id: number) => {
    return await request.get({ url: `/project/order-daily-income/get?id=` + id })
  },

  // 新增订单每日收益统计
  createOrderDailyIncome: async (data: OrderDailyIncomeVO) => {
    return await request.post({ url: `/project/order-daily-income/create`, data })
  },

  // 修改订单每日收益统计
  updateOrderDailyIncome: async (data: OrderDailyIncomeVO) => {
    return await request.put({ url: `/project/order-daily-income/update`, data })
  },

  // 删除订单每日收益统计
  deleteOrderDailyIncome: async (id: number) => {
    return await request.delete({ url: `/project/order-daily-income/delete?id=` + id })
  },

  // 导出订单每日收益统计 Excel
  exportOrderDailyIncome: async (params) => {
    return await request.download({ url: `/project/order-daily-income/export-excel`, params })
  },

  // 发放收益
  distributeIncome: async (incomeDate: string) => {
    return await request.post({ url: `/project/order-daily-income/distribute-income`, data: { incomeDate } })
  }
}