import request from '@/config/axios'

// 用户身份 VO
export interface UserIdentitiesVO {
  id: number // ID
  address: string // 用户地址
  contractAddress: string // 用户合约地址
  managementKey: string // 管理密钥地址
  blockchainAddressId: number // 关联的区块链地址ID
  salt: string // 盐值，用于加密或哈希计算
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  contractDeployed: boolean // 合约是否已部署，0-未部署，1-已部署
  claimKeySetup: boolean // 声明密钥是否已设置，0-未设置，1-已设置
  countryCode: number // 国家代码
  associatedTokenIds: string // 关联的代币ID，JSON格式
  pendingTokenIds: string // 待处理的代币ID，JSON格式
  status: string // 状态，如active-活跃
}

// 用户身份 API
export const UserIdentitiesApi = {
  // 查询用户身份分页
  getUserIdentitiesPage: async (params: any) => {
    return await request.get({ url: `/chain/user-identities/page`, params })
  },

  // 查询用户身份详情
  getUserIdentities: async (id: number) => {
    return await request.get({ url: `/chain/user-identities/get?id=` + id })
  },

  // 新增用户身份
  createUserIdentities: async (data: UserIdentitiesVO) => {
    return await request.post({ url: `/chain/user-identities/create`, data })
  },

  // 修改用户身份
  updateUserIdentities: async (data: UserIdentitiesVO) => {
    return await request.put({ url: `/chain/user-identities/update`, data })
  },

  // 删除用户身份
  deleteUserIdentities: async (id: number) => {
    return await request.delete({ url: `/chain/user-identities/delete?id=` + id })
  },

  // 导出用户身份 Excel
  exportUserIdentities: async (params) => {
    return await request.download({ url: `/chain/user-identities/export-excel`, params })
  }
}