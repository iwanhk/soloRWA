import request from '@/config/axios'

// 系统初始化 VO
export interface SystemInitializationVO {
  id: number // ID
  step: string // 初始化步骤名称
  status: string // 状态，如pending-待处理，completed-已完成
  contractAddress: string // 相关合约地址
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  errorMessage: string // 错误信息，若有
  metadata: string // 元数据，JSON格式
}

// 系统初始化 API
export const SystemInitializationApi = {
  // 查询系统初始化分页
  getSystemInitializationPage: async (params: any) => {
    return await request.get({ url: `/chain/system-initialization/page`, params })
  },

  // 查询系统初始化详情
  getSystemInitialization: async (id: number) => {
    return await request.get({ url: `/chain/system-initialization/get?id=` + id })
  },

  // 新增系统初始化
  createSystemInitialization: async (data: SystemInitializationVO) => {
    return await request.post({ url: `/chain/system-initialization/create`, data })
  },

  // 修改系统初始化
  updateSystemInitialization: async (data: SystemInitializationVO) => {
    return await request.put({ url: `/chain/system-initialization/update`, data })
  },

  // 删除系统初始化
  deleteSystemInitialization: async (id: number) => {
    return await request.delete({ url: `/chain/system-initialization/delete?id=` + id })
  },

  // 导出系统初始化 Excel
  exportSystemInitialization: async (params) => {
    return await request.download({ url: `/chain/system-initialization/export-excel`, params })
  }
}