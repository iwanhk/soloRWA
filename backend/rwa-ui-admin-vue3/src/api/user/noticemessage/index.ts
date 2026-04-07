import request from '@/config/axios'

// 用户消息 VO
export interface NoticeMessageVO {
  id: number // 用户ID
  userId: number // 用户id
  noticeType: number // 消息类型
  templateId: number // 模版编号
  templateCode: string // 模板编码
  templateNickname: string // 模版发送人名称
  templateContent: string // 模版内容
  templateType: number // 模版类型
  templateParams: string // 模版参数
  orderId: number // 订单id
  noticeUrl: string // 消息地址
  readStatus: boolean // 是否已读
  readTime: Date // 阅读时间
}

// 用户消息 API
export const NoticeMessageApi = {
  // 查询用户消息分页
  getNoticeMessagePage: async (params: any) => {
    return await request.get({ url: `/user/notice-message/page`, params })
  },

  // 查询用户消息详情
  getNoticeMessage: async (id: number) => {
    return await request.get({ url: `/user/notice-message/get?id=` + id })
  },

  // 新增用户消息
  createNoticeMessage: async (data: NoticeMessageVO) => {
    return await request.post({ url: `/user/notice-message/create`, data })
  },

  // 修改用户消息
  updateNoticeMessage: async (data: NoticeMessageVO) => {
    return await request.put({ url: `/user/notice-message/update`, data })
  },

  // 删除用户消息
  deleteNoticeMessage: async (id: number) => {
    return await request.delete({ url: `/user/notice-message/delete?id=` + id })
  },

  // 导出用户消息 Excel
  exportNoticeMessage: async (params) => {
    return await request.download({ url: `/user/notice-message/export-excel`, params })
  }
}