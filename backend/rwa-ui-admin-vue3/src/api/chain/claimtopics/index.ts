import request from '@/config/axios'

// 声明主题 VO
export interface ClaimTopicsVO {
  id: number // ID
  name: string // 主题名称
  value: string // 主题值
  topic: string // 主题哈希
}

// 声明主题 API
export const ClaimTopicsApi = {
  // 查询声明主题分页
  getClaimTopicsPage: async (params: any) => {
    return await request.get({ url: `/chain/claim-topics/page`, params })
  },

  // 查询声明主题详情
  getClaimTopics: async (id: number) => {
    return await request.get({ url: `/chain/claim-topics/get?id=` + id })
  },

  // 新增声明主题
  createClaimTopics: async (data: ClaimTopicsVO) => {
    return await request.post({ url: `/chain/claim-topics/create`, data })
  },

  // 修改声明主题
  updateClaimTopics: async (data: ClaimTopicsVO) => {
    return await request.put({ url: `/chain/claim-topics/update`, data })
  },

  // 删除声明主题
  deleteClaimTopics: async (id: number) => {
    return await request.delete({ url: `/chain/claim-topics/delete?id=` + id })
  },

  // 导出声明主题 Excel
  exportClaimTopics: async (params) => {
    return await request.download({ url: `/chain/claim-topics/export-excel`, params })
  }
}