import request from '@/config/axios'

// 提交审核请求 VO
export interface SubmitAuditVO {
    companyCreditCode: string
    businessLicenseUrl: string
    qualificationFileUrls?: string
    authorizationFileUrls?: string
    authIdentity: string
    idCardName: string
    idCardNo: string
    idCardExpireTime: Date
    idCardFrontUrl: string
    idCardBackUrl: string
    email?: string
    bankAccountName: string
    bankAccount: string
    bankName: string
}

// 审核请求 VO
export interface AuditVO {
    tenantId: number
    auditResult: number // 2-通过 3-拒绝
    auditRemark?: string
}

// 审核状态响应 VO
export interface AuditStatusVO {
    auditStatus: number // 0-待提交 1-待审核 2-审核通过 3-审核拒绝
    auditRemark?: string
    hasPublisherInfo: boolean
}

// 提交审核
export const submitAudit = (data: SubmitAuditVO) => {
    return request.post({ url: '/system/tenant/submit-audit', data })
}

// 审核租户
export const auditTenant = (data: AuditVO) => {
    return request.post({ url: '/system/tenant/audit', data })
}

// 获取当前租户审核状态
export const getAuditStatus = () => {
    return request.get<AuditStatusVO>({ url: '/system/tenant/audit-status' })
}

// 获取当前租户的发行商信息（表单回显）
export const getPublisherInfo = () => {
    return request.get<SubmitAuditVO>({ url: '/system/tenant/publisher-info' })
}

// 获取指定租户的发行商信息（管理员审核用）
export const getPublisherInfoByTenantId = (tenantId: number) => {
    return request.get<SubmitAuditVO>({ url: `/system/tenant/publisher-info/${tenantId}` })
}


