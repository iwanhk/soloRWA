import request from '@/config/axios'

// 消息模板 VO
export interface NoticeTemplateVO {
  id: number // 主键
  name: string // 模板名称
  code: string // 模板编码
  nickname: string // 发送人名称
  content: string // 模板内容
  type: number // 类型
  params: string // 参数数组
  status: number // 状态
  remark: string // 备注
  createTime: Date // 创建时间
}

// 消息模板 API
export const NoticeTemplateApi = {
  // 查询消息模板分页
  getNoticeTemplatePage: async (params: any) => {
    return await request.get({ url: `/user/notice-template/page`, params })
  },

  // 查询消息模板详情
  getNoticeTemplate: async (id: number) => {
    return await request.get({ url: `/user/notice-template/get?id=` + id })
  },

  // 新增消息模板
  createNoticeTemplate: async (data: NoticeTemplateVO) => {
    return await request.post({ url: `/user/notice-template/create`, data })
  },

  // 修改消息模板
  updateNoticeTemplate: async (data: NoticeTemplateVO) => {
    return await request.put({ url: `/user/notice-template/update`, data })
  },

  // 删除消息模板
  deleteNoticeTemplate: async (id: number) => {
    return await request.delete({ url: `/user/notice-template/delete?id=` + id })
  },

  // 发送测试消息
  sendMessage: async (data: any) => {
    return await request.post({ url: `/user/notice-template/send-message`, data })
  }
}
