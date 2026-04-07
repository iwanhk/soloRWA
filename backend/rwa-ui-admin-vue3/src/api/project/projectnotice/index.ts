import request from '@/config/axios'

// 项目通告表（含全局通告） VO
export interface NoticeVO {
  id: number // 通告ID
  noticeNo: string // 通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）
  projectId: number // 关联项目ID（0表示全局通告，关联project_core.id）
  projectName: string // 关联项目名称（冗余，0时为“全局通告”）
  noticeTitle: string // 通告标题
  noticeType: number // 通告类型
  noticeContent: string // 通告内容
  attachUrls: string // 附件URL（多个用逗号分隔，支持PDF/Word/图片等，如“url1,url2”）
  publishUserId: number // 发布人ID（关联sys_user.id）
  publishUserName: string // 发布人名称（冗余）
  publishTime: Date // 发布时间
  showStartTime: Date // 开始展示时间（NULL表示立即展示）
  showEndTime: Date // 结束展示时间（NULL表示永久展示）
  isTop: boolean // 是否置顶：1-是 0-否（同一项目/全局仅1个置顶）
  noticeStatus: number // 通告状态：1-草稿 2-已发布 3-已下架
  readCount: number // 阅读次数
  isPopup: boolean // 是否弹窗展示：1-是 0-否（用户进入页面时弹窗）
  remark: string // 备注（仅运营可见，如“临时通告，3天后下架”）
}

// 项目通告表（含全局通告） API
export const NoticeApi = {
  // 查询项目通告表（含全局通告）分页
  getNoticePage: async (params: any) => {
    return await request.get({ url: `/project/notice/page`, params })
  },

  // 查询项目通告表（含全局通告）详情
  getNotice: async (id: number) => {
    return await request.get({ url: `/project/notice/get?id=` + id })
  },

  // 新增项目通告表（含全局通告）
  createNotice: async (data: NoticeVO) => {
    return await request.post({ url: `/project/notice/create`, data })
  },

  // 修改项目通告表（含全局通告）
  updateNotice: async (data: NoticeVO) => {
    return await request.put({ url: `/project/notice/update`, data })
  },

  // 删除项目通告表（含全局通告）
  deleteNotice: async (id: number) => {
    return await request.delete({ url: `/project/notice/delete?id=` + id })
  },

  // 导出项目通告表（含全局通告） Excel
  exportNotice: async (params) => {
    return await request.download({ url: `/project/notice/export-excel`, params })
  }
}