import request from '@/config/axios'

// 用户银行卡信息 VO
export interface BankVO {
  id: number // 银行卡记录ID
  userId: number // 关联用户ID
  bankAccountName: string // 银行卡开户名（需与实名一致）
  bankAccount: string // 银行卡号
  bankName: string // 开户行
  bankBranch: string // 开户行支行
  isDefault: boolean // 是否默认银行卡
  auditStatus: number // 审核状态
  auditRemark: string // 审核备注
}

// 用户银行卡信息 API
export const BankApi = {
  // 查询用户银行卡信息分页
  getBankPage: async (params: any) => {
    return await request.get({ url: `/user/bank/page`, params })
  },

  // 查询用户银行卡信息详情
  getBank: async (id: number) => {
    return await request.get({ url: `/user/bank/get?id=` + id })
  },

  // 新增用户银行卡信息
  createBank: async (data: BankVO) => {
    return await request.post({ url: `/user/bank/create`, data })
  },

  // 修改用户银行卡信息
  updateBank: async (data: BankVO) => {
    return await request.put({ url: `/user/bank/update`, data })
  },

  // 删除用户银行卡信息
  deleteBank: async (id: number) => {
    return await request.delete({ url: `/user/bank/delete?id=` + id })
  },

  // 导出用户银行卡信息 Excel
  exportBank: async (params) => {
    return await request.download({ url: `/user/bank/export-excel`, params })
  },

  // 审核用户银行卡
  auditBank: async (data: { id: number; auditStatus: number; auditRemark?: string }) => {
    return await request.put({ url: `/user/bank/audit`, data })
  }
}