import request from '@/config/axios'

// 身份注册表存储 VO
export interface IdentityRegistryStoragesVO {
  id: number // ID
  address: string // 存储合约地址
  deployerAddress: string // 部署者地址
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  boundTokenCount: number // 绑定的代币数量
  status: string // 状态，如deployed-已部署，active-活跃
}

// 身份注册表存储 API
export const IdentityRegistryStoragesApi = {
  // 查询身份注册表存储分页
  getIdentityRegistryStoragesPage: async (params: any) => {
    return await request.get({ url: `/chain/identity-registry-storages/page`, params })
  },

  // 查询身份注册表存储详情
  getIdentityRegistryStorages: async (id: number) => {
    return await request.get({ url: `/chain/identity-registry-storages/get?id=` + id })
  },

  // 新增身份注册表存储
  createIdentityRegistryStorages: async (data: IdentityRegistryStoragesVO) => {
    return await request.post({ url: `/chain/identity-registry-storages/create`, data })
  },

  // 修改身份注册表存储
  updateIdentityRegistryStorages: async (data: IdentityRegistryStoragesVO) => {
    return await request.put({ url: `/chain/identity-registry-storages/update`, data })
  },

  // 删除身份注册表存储
  deleteIdentityRegistryStorages: async (id: number) => {
    return await request.delete({ url: `/chain/identity-registry-storages/delete?id=` + id })
  },

  // 导出身份注册表存储 Excel
  exportIdentityRegistryStorages: async (params) => {
    return await request.download({ url: `/chain/identity-registry-storages/export-excel`, params })
  }
}