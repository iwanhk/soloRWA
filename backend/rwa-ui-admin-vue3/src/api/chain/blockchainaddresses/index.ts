import request from '@/config/axios'

// 区块链地址 VO
export interface BlockchainAddressesVO {
  id: number // ID
  name: string // 地址名称
  address: string // 区块链地址
  privateKey: string // 地址私钥
  description: string // 地址描述
}

// 区块链地址 API
export const BlockchainAddressesApi = {
  // 查询区块链地址分页
  getBlockchainAddressesPage: async (params: any) => {
    return await request.get({ url: `/chain/blockchain-addresses/page`, params })
  },

  // 查询区块链地址详情
  getBlockchainAddresses: async (id: number) => {
    return await request.get({ url: `/chain/blockchain-addresses/get?id=` + id })
  },

  // 新增区块链地址
  createBlockchainAddresses: async (data: BlockchainAddressesVO) => {
    return await request.post({ url: `/chain/blockchain-addresses/create`, data })
  },

  // 修改区块链地址
  updateBlockchainAddresses: async (data: BlockchainAddressesVO) => {
    return await request.put({ url: `/chain/blockchain-addresses/update`, data })
  },

  // 删除区块链地址
  deleteBlockchainAddresses: async (id: number) => {
    return await request.delete({ url: `/chain/blockchain-addresses/delete?id=` + id })
  },

  // 导出区块链地址 Excel
  exportBlockchainAddresses: async (params) => {
    return await request.download({ url: `/chain/blockchain-addresses/export-excel`, params })
  }
}