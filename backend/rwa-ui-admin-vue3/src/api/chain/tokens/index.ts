import request from '@/config/axios'

// 代币 VO
export interface TokensVO {
  id: number // ID
  name: string // 代币名称
  symbol: string // 代币符号
  decimals: number // 小数位数
  address: string // 代币合约地址
  ownerAddress: string // 所有者地址
  deployerAddress: string // 部署者地址
  identityRegistryStorageId: number // 身份注册表存储ID
  identityRegistryAddress: string // 身份注册表地址
  claimTopicsRegistryAddress: string // 声明主题注册表地址
  trustedIssuersRegistryAddress: string // 可信发行者注册表地址
  modularComplianceAddress: string // 模块化合规合约地址
  tokenOnchainIdAddress: string // 代币链上ID地址
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  status: string // 状态，如pending-待处理，deployed-已部署
  salt: string // 盐值，用于加密或哈希计算
  tokenAgents: string // 代币代理，JSON格式
  claimTopics: string // 声明主题，JSON格式
  issuers: string // 发行者，JSON格式
  issuerClaims: string // 发行者声明，JSON格式
  deploymentInfo: string // 部署信息，JSON格式
  errorMessage: string // 错误信息，若有
}

// 代币 API
export const TokensApi = {
  // 查询代币分页
  getTokensPage: async (params: any) => {
    return await request.get({ url: `/chain/tokens/page`, params })
  },

  // 查询代币详情
  getTokens: async (id: number) => {
    return await request.get({ url: `/chain/tokens/get?id=` + id })
  },

  // 新增代币
  createTokens: async (data: TokensVO) => {
    return await request.post({ url: `/chain/tokens/create`, data })
  },

  // 修改代币
  updateTokens: async (data: TokensVO) => {
    return await request.put({ url: `/chain/tokens/update`, data })
  },

  // 删除代币
  deleteTokens: async (id: number) => {
    return await request.delete({ url: `/chain/tokens/delete?id=` + id })
  },

  // 导出代币 Excel
  exportTokens: async (params) => {
    return await request.download({ url: `/chain/tokens/export-excel`, params })
  }
}