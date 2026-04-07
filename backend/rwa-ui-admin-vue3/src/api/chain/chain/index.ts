import request from '@/config/axios'

export interface ChainVO {
  id: number
  name: string
  chainId: number
  rpcUrl: string
  browserUrl: string
  status: number
  sort: number
}

export const ChainApi = {
  // 查询分页
  getChainPage: async (params: any) => {
    return await request.get({ url: `/chain/chain/page`, params })
  },
  // 查询详情
  getChain: async (id: number) => {
    return await request.get({ url: `/chain/chain/get?id=` + id })
  },
  // 新增
  createChain: async (data: ChainVO) => {
    return await request.post({ url: `/chain/chain/create`, data })
  },
  // 修改
  updateChain: async (data: ChainVO) => {
    return await request.put({ url: `/chain/chain/update`, data })
  },
  // 删除
  deleteChain: async (id: number) => {
    return await request.delete({ url: `/chain/chain/delete?id=` + id })
  },
  // 获得开启的区块链列表
  getActiveChainList: async () => {
    return await request.get({ url: `/chain/chain/list-active` })
  }
}
