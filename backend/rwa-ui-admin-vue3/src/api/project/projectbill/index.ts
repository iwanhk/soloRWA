import request from '@/config/axios'

// 项目账单管理表 VO
export interface BillVO {
  id: number // 账单ID
  billNo: string // 流水号（唯一，如：BILL202512160001）
  billType: number // 账单类型：1-分红 2-到期赎回 3-提前赎回
  applyTime: Date // 申请时间
  userId: number // 申请用户ID（关联用户表）
  projectId: number // 项目ID（关联project_core.id）
  projectName: string // 所属项目名称（冗余）
  payee: string // 收款方（用户/企业名称）
  bankAccount: string // 收款账户（银行卡号/链地址）
  bankName: string // 开户行（如“中国工商银行XX支行”）
  billAmount: number // 账单金额（元）
  actualAmount: number // 实际到账金额（元）
  auditStatus: number // 审核状态：1-待审核 2-审核通过 3-审核不通过
  auditUserId: number // 审核人ID
  auditUserName: string // 审核人名称
  auditTime: Date // 审核时间
  auditRemark: string // 审核备注（审核不通过原因）
  payVoucherUrl: string // 支付凭证URL（审核通过后上传）
  payTime: Date // 支付时间（凭证上传时记录）
}

// 项目账单管理表 API
export const BillApi = {
  // 查询项目账单管理表分页
  getBillPage: async (params: any) => {
    return await request.get({ url: `/project/bill/page`, params })
  },

  // 查询项目账单管理表详情
  getBill: async (id: number) => {
    return await request.get({ url: `/project/bill/get?id=` + id })
  },

  // 新增项目账单管理表
  createBill: async (data: BillVO) => {
    return await request.post({ url: `/project/bill/create`, data })
  },

  // 修改项目账单管理表
  updateBill: async (data: BillVO) => {
    return await request.put({ url: `/project/bill/update`, data })
  },

  // 删除项目账单管理表
  deleteBill: async (id: number) => {
    return await request.delete({ url: `/project/bill/delete?id=` + id })
  },

  // 导出项目账单管理表 Excel
  exportBill: async (params) => {
    return await request.download({ url: `/project/bill/export-excel`, params })
  },

  // 审核账单
  auditBill: async (data: any) => {
    return await request.put({ url: `/project/bill/audit`, data })
  },

  // 上传支付凭证
  uploadVoucher: async (data: { id: number; payVoucherUrl: string; payTime?: number }) => {
    return await request.put({ url: `/project/bill/upload-voucher`, data })
  }
}