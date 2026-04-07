import request from '@/config/axios'

// 项目认购订单 VO
export interface OrderVO {
  id: number // 订单ID
  orderNo: string // 订单号
  applyDate: Date // 申请日期
  userId: number // 用户ID
  orderStatus: number // 订单状态：0-待支付 1-审核中 2-审核通过 3-审核未通过 4-已取消
  projectId: number // 项目ID
  projectName: string // 项目名称（冗余）
  subscribeQuantity: number // 申购份额(份)
  price: number // 单价
  totalAmount: number // 金额(U)（=申购份额×项目发行单价）
  lastQuantity: string // 剩余份额
  lastAmount: string // 剩余金额
  confirmPurchaseTime: Date // 确认时间
  payType: string // 支付方式（默认银行转账）
  chainAddress: string // 链地址
  contractNo: string // 合同号
  payVoucherUrl: string // 支付凭证图片URL
  auditTime: Date // 审核时间（未审核为空）
  auditUserId: number // 审核人ID（关联用户表）
  auditUserName: string // 审核人名称
  auditRemark: string // 审核备注（如审核未通过原因）
  cancelTime: Date // 取消时间（已取消订单填充）
  cancelReason: string // 取消原因（如超时未支付）
  expireTime: Date // 订单过期时间（待支付订单超时时间，如创建后24小时）
}

// 项目认购订单 API
export const OrderApi = {
  // 查询项目认购订单分页
  getOrderPage: async (params: any) => {
    return await request.get({ url: `/project/order/page`, params })
  },

  // 查询项目认购订单详情
  getOrder: async (id: number) => {
    return await request.get({ url: `/project/order/get?id=` + id })
  },

  // 新增项目认购订单
  createOrder: async (data: OrderVO) => {
    return await request.post({ url: `/project/order/create`, data })
  },

  // 修改项目认购订单
  updateOrder: async (data: OrderVO) => {
    return await request.put({ url: `/project/order/update`, data })
  },

  // 删除项目认购订单
  deleteOrder: async (id: number) => {
    return await request.delete({ url: `/project/order/delete?id=` + id })
  },

  // 审核项目认购订单
  auditOrder: async (data: any) => {
    return await request.put({ url: `/project/order/audit`, data })
  },

  // 导出项目认购订单 Excel
  exportOrder: async (params) => {
    return await request.download({ url: `/project/order/export-excel`, params })
  }
}
