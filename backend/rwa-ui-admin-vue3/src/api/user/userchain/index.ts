import request from '@/config/axios'

// 用户链地址表= VO
export interface ChainVO {
  id: number // ID
  userId: number // 用户ID
  chainId: number // 关联链ID（chain_manage.id）
  chainAddress: string // 用户在该链上的地址（如ETH地址：0x...）
  identityId: number // identity_id
  chainStatus: number // 链上状态
  isDefault: boolean // 是否默认地址：1-是 0-否（同一链下仅1个默认）
  addressRemark: string // 地址备注（如“常用钱包”）
}

// 用户链地址表= API
export const ChainApi = {
  // 查询用户链地址表=分页
  getChainPage: async (params: any) => {
    return await request.get({ url: `/user/chain/page`, params })
  },

  // 查询用户链地址表=详情
  getChain: async (id: number) => {
    return await request.get({ url: `/user/chain/get?id=` + id })
  },

  // 新增用户链地址表=
  createChain: async (data: ChainVO) => {
    return await request.post({ url: `/user/chain/create`, data })
  },

  // 修改用户链地址表=
  updateChain: async (data: ChainVO) => {
    return await request.put({ url: `/user/chain/update`, data })
  },

  // 删除用户链地址表=
  deleteChain: async (id: number) => {
    return await request.delete({ url: `/user/chain/delete?id=` + id })
  },

  // 导出用户链地址表= Excel
  exportChain: async (params) => {
    return await request.download({ url: `/user/chain/export-excel`, params })
  }
}