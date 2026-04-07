import request from '@/config/axios'

// 系统协议表 VO
export interface AgreementVO {
  id: number // 协议ID
  agreementType: number // 协议类型
  agreementTitle: string // 协议标题
  agreementContent: string // 协议内容
  version: string // 协议版本号
  isCurrent: boolean // 是否当前生效版本：1-是 0-否（同一类型仅1个生效版本）
  effectiveTime: Date // 生效时间
  expireTime: Date // 过期时间
}

export interface AgreementSimpleRespVO {
  id: number
  agreementTitle: string
  agreementType: number
}

// 系统协议表 API
export const AgreementApi = {
  // 获得系统协议表简单列表
  getAgreementSimple: async () => {
    return await request.get({ url: `/user/agreement/simple` })
  },

  // 查询系统协议表分页
  getAgreementPage: async (params: any) => {
    return await request.get({ url: `/user/agreement/page`, params })
  },

  // 查询系统协议表详情
  getAgreement: async (id: number) => {
    return await request.get({ url: `/user/agreement/get?id=` + id })
  },

  // 新增系统协议表
  createAgreement: async (data: AgreementVO) => {
    return await request.post({ url: `/user/agreement/create`, data })
  },

  // 修改系统协议表
  updateAgreement: async (data: AgreementVO) => {
    return await request.put({ url: `/user/agreement/update`, data })
  },

  // 删除系统协议表
  deleteAgreement: async (id: number) => {
    return await request.delete({ url: `/user/agreement/delete?id=` + id })
  },

  // 导出系统协议表 Excel
  exportAgreement: async (params) => {
    return await request.download({ url: `/user/agreement/export-excel`, params })
  }
}