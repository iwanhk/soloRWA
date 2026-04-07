import request from '@/config/axios'

// 地址身份关联 VO
export interface AddressIdentitiesVO {
  id: number // ID
  address: string // 区块链地址
  identityId: number // 身份ID，关联到对应的身份表
  type: string // 身份类型，如ClaimIssuer、User等
  contractAddress: string // 合约地址
}

// 地址身份关联 API
export const AddressIdentitiesApi = {
  // 查询地址身份关联分页
  getAddressIdentitiesPage: async (params: any) => {
    return await request.get({ url: `/chain/address-identities/page`, params })
  },

  // 查询地址身份关联详情
  getAddressIdentities: async (id: number) => {
    return await request.get({ url: `/chain/address-identities/get?id=` + id })
  },

  // 新增地址身份关联
  createAddressIdentities: async (data: AddressIdentitiesVO) => {
    return await request.post({ url: `/chain/address-identities/create`, data })
  },

  // 修改地址身份关联
  updateAddressIdentities: async (data: AddressIdentitiesVO) => {
    return await request.put({ url: `/chain/address-identities/update`, data })
  },

  // 删除地址身份关联
  deleteAddressIdentities: async (id: number) => {
    return await request.delete({ url: `/chain/address-identities/delete?id=` + id })
  },

  // 导出地址身份关联 Excel
  exportAddressIdentities: async (params) => {
    return await request.download({ url: `/chain/address-identities/export-excel`, params })
  }
}