import request from '@/config/axios'

export interface TenantVO {
  id: number
  name: string
  contactName: string
  contactMobile: string
  status: number
  domain: string
  packageId: number
  username: string
  password: string
  expireTime: Date
  accountCount: number
  websites: string[]
  createTime: Date
  // 审核状态相关
  auditStatus?: number
  // 发行商信息字段（公司名和用户手机号从发行商信息自动填充，identityAuthStatus根据auditStatus自动设置）
  registerTime?: Date
  authIdentity?: string
  companyCreditCode?: string
  businessLicenseUrl?: string
  qualificationFileUrls?: string
  authorizationFileUrls?: string
  idCardName?: string
  idCardNo?: string
  idCardExpireTime?: Date
  idCardFrontUrl?: string
  idCardBackUrl?: string
  email?: string
  bankAccountName?: string
  bankAccount?: string
  bankName?: string
}

export interface TenantPageReqVO extends PageParam {
  name?: string
  contactName?: string
  contactMobile?: string
  status?: number
  createTime?: Date[]
}

export interface TenantExportReqVO {
  name?: string
  contactName?: string
  contactMobile?: string
  status?: number
  createTime?: Date[]
}

// 查询发行商列表
export const getTenantPage = (params: TenantPageReqVO) => {
  return request.get({ url: '/system/tenant/page', params })
}

// 查询发行商详情
export const getTenant = (id: number) => {
  return request.get({ url: '/system/tenant/get?id=' + id })
}

// 获取发行商精简信息列表
export const getTenantList = () => {
  return request.get({ url: '/system/tenant/simple-list' })
}

// 新增发行商
export const createTenant = (data: TenantVO) => {
  return request.post({ url: '/system/tenant/create', data })
}

// 新增发行商（带审核状态）
export const createTenantWithAudit = (data: TenantVO) => {
  return request.post({ url: '/system/tenant/create-with-audit', data })
}

// 修改发行商
export const updateTenant = (data: TenantVO) => {
  return request.put({ url: '/system/tenant/update', data })
}

// 删除发行商
export const deleteTenant = (id: number) => {
  return request.delete({ url: '/system/tenant/delete?id=' + id })
}

// 批量删除发行商
export const deleteTenantList = (ids: number[]) => {
  return request.delete({ url: '/system/tenant/delete-list', params: { ids: ids.join(',') } })
}

// 导出发行商
export const exportTenant = (params: TenantExportReqVO) => {
  return request.download({ url: '/system/tenant/export-excel', params })
}

// 获取待审核租户数量
export const getAuditCount = () => {
  return request.get<number>({ url: '/system/tenant/auditCount' })
}
