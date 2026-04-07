import request from '@/config/axios'

// 发行商 VO
export interface PublisherInfoVO {
  id: number // 发行商ID （sys_user.id，发行商对应的用户账号）
  userPhone: string // 用户手机号（冗余sys_user.phonenumber）
  registerTime: Date // 注册时间（冗余sys_user.create_time）
  identityAuthStatus: number // 身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败
  authIdentity: string // 认证身份（如“企业法人”“经办人”）
  companyName: string // 公司名称（企业全称，与营业执照一致）
  companyCreditCode: string // 公司统一社会信用代码（唯一，18位）
  businessLicenseUrl: string // 营业执照URL（图片/文件）
  qualificationFileUrls: string // 资质文件URL（多个用,分隔）
  authorizationFileUrls: string // 授权文件URL（多个用,分隔）
  idCardName: string // 身份证姓名
  idCardNo: string // 身份证号
  idCardExpireTime: Date // 身份证有效期
  idCardFrontUrl: string // 身份证正面URL
  idCardBackUrl: string // 身份证背面URL
  email: string // 邮箱
  bankAccountName: string // 开户名（与公司名称/法人姓名一致）
  bankAccount: string // 银行账户（卡号）
  bankName: string // 开户行
}

// 发行商 API
export const PublisherInfoApi = {
  // 查询发行商分页
  getPublisherInfoPage: async (params: any) => {
    return await request.get({ url: `/system/publisher-info/page`, params })
  },

  // 查询发行商详情
  getPublisherInfo: async (id: number) => {
    return await request.get({ url: `/system/publisher-info/get?id=` + id })
  },

  // 新增发行商
  createPublisherInfo: async (data: PublisherInfoVO) => {
    return await request.post({ url: `/system/publisher-info/create`, data })
  },

  // 修改发行商
  updatePublisherInfo: async (data: PublisherInfoVO) => {
    return await request.put({ url: `/system/publisher-info/update`, data })
  },

  // 删除发行商
  deletePublisherInfo: async (id: number) => {
    return await request.delete({ url: `/system/publisher-info/delete?id=` + id })
  },

  // 导出发行商 Excel
  exportPublisherInfo: async (params) => {
    return await request.download({ url: `/system/publisher-info/export-excel`, params })
  }
}