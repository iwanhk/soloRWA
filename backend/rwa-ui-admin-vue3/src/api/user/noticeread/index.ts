import request from '@/config/axios'

// 系统消息已读记录 VO
export interface NoticeReadVO {
  id: number // 记录ID
  noticeId: number // 关联消息主表的系统消息ID
  userId: number // 已读用户ID
  readStatus: number // 阅读状态：0-未读 1-已读
  readTime: Date // 阅读时间
}

// 系统消息已读记录 API
export const NoticeReadApi = {
  // 查询系统消息已读记录分页
  getNoticeReadPage: async (params: any) => {
    return await request.get({ url: `/user/notice-read/page`, params })
  },

  // 查询系统消息已读记录详情
  getNoticeRead: async (id: number) => {
    return await request.get({ url: `/user/notice-read/get?id=` + id })
  },

  // 新增系统消息已读记录
  createNoticeRead: async (data: NoticeReadVO) => {
    return await request.post({ url: `/user/notice-read/create`, data })
  },

  // 修改系统消息已读记录
  updateNoticeRead: async (data: NoticeReadVO) => {
    return await request.put({ url: `/user/notice-read/update`, data })
  },

  // 删除系统消息已读记录
  deleteNoticeRead: async (id: number) => {
    return await request.delete({ url: `/user/notice-read/delete?id=` + id })
  },

  // 导出系统消息已读记录 Excel
  exportNoticeRead: async (params) => {
    return await request.download({ url: `/user/notice-read/export-excel`, params })
  }
}