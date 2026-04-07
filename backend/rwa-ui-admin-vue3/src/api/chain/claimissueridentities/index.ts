import request from '@/config/axios'

// 声明发行者身份 VO
export interface ClaimIssuerIdentitiesVO {
  id: number // ID
  address: string // 发行者地址
  contractAddress: string // 发行者合约地址
  managementKey: string // 管理密钥地址
  blockchainAddressId: number // 关联的区块链地址ID
  salt: string // 盐值，用于加密或哈希计算
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  contractDeployed: boolean // 合约是否已部署，0-未部署，1-已部署
  claimKeySetup: boolean // 声明密钥是否已设置，0-未设置，1-已设置
  status: string // 状态，如active-活跃
}

// 声明发行者身份 API
export const ClaimIssuerIdentitiesApi = {
  // 查询声明发行者身份分页
  getClaimIssuerIdentitiesPage: async (params: any) => {
    return await request.get({ url: `/chain/claim-issuer-identities/page`, params })
  },

  // 查询声明发行者身份详情
  getClaimIssuerIdentities: async (id: number) => {
    return await request.get({ url: `/chain/claim-issuer-identities/get?id=` + id })
  },

  // 新增声明发行者身份
  createClaimIssuerIdentities: async (data: ClaimIssuerIdentitiesVO) => {
    return await request.post({ url: `/chain/claim-issuer-identities/create`, data })
  },

  // 修改声明发行者身份
  updateClaimIssuerIdentities: async (data: ClaimIssuerIdentitiesVO) => {
    return await request.put({ url: `/chain/claim-issuer-identities/update`, data })
  },

  // 删除声明发行者身份
  deleteClaimIssuerIdentities: async (id: number) => {
    return await request.delete({ url: `/chain/claim-issuer-identities/delete?id=` + id })
  },

  // 导出声明发行者身份 Excel
  exportClaimIssuerIdentities: async (params) => {
    return await request.download({ url: `/chain/claim-issuer-identities/export-excel`, params })
  }
}