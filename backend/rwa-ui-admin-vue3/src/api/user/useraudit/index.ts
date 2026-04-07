import request from '@/config/axios'

// 用户投资者认证审核 VO
export interface AuditVO {
  id: number // 审核记录ID
  userId: number // 关联用户ID
  realName: string // 用户姓名（提交审核时的姓名）
  idCard: string // 证件号（脱敏存储）
  idCardExpire: string // 证件号有效期
  idCardFrontUrl: string // 证件号人像面图片地址
  idCardBackUrl: string // 证件号国徽面图片地址
  investmentQualificationUrl: string // 投资资质图片地址
  bankFlowUrl: string // 银行流水单图片地址
  residenceProofUrl: string // 住址证明图片地址
  bankCardId: number // 关联用户银行卡ID（user_bank_card.id）
  email: string // 邮箱
  contactPhone: string // 联系电话
  auditStatus: number // 审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回
  submitVersion: number // 提交版本（用户第N次提交认证）
  auditRemark: string // 审核备注（驳回原因）
  isLatest: boolean // 是否为最新提交记录（1-是 0-否）
}

// 用户投资者认证审核 API
export const AuditApi = {
  // 查询用户投资者认证审核分页
  getAuditPage: async (params: any) => {
    return await request.get({ url: `/user/audit/page`, params })
  },

  // 查询用户投资者认证审核详情
  getAudit: async (id: number) => {
    return await request.get({ url: `/user/audit/get?id=` + id })
  },

  // 新增用户投资者认证审核
  createAudit: async (data: AuditVO) => {
    return await request.post({ url: `/user/audit/create`, data })
  },

  // 修改用户投资者认证审核
  updateAudit: async (data: AuditVO) => {
    return await request.put({ url: `/user/audit/update`, data })
  },

  // 删除用户投资者认证审核
  deleteAudit: async (id: number) => {
    return await request.delete({ url: `/user/audit/delete?id=` + id })
  },

  // 导出用户投资者认证审核 Excel
  exportAudit: async (params) => {
    return await request.download({ url: `/user/audit/export-excel`, params })
  },

  // 审核用户认证
  reviewAudit: async (data: { id: number; auditStatus: number; auditRemark?: string }) => {
    return await request.put({ url: `/user/audit/review`, data })
  }
}