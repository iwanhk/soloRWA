import request from '@/config/axios'

// 用户基础信息 VO
export interface InfoVO {
  id: number // 用户ID
  mobile: string // 手机号
  password: string // 密码
  realName: string // 用户姓名
  idCard: string // 身份证号
  idCardExpire: string // 身份证有效期
  email: string // 邮箱
  phone: string // 联系电话
  auditStatus: number // 审核状态
  status: number // 状态：1启动 2禁用
  twoFactorAuthStatus?: number // 2FA验证状态：0-未开启 1-已开启 2-待验证 3-解绑中
  pendingBankApplyId?: number // 待审核的银行卡申请ID
  nickName?: string // 昵称
  avatar?: string // 头像
}


// 用户基础信息 API
export const InfoApi = {
  // 查询用户基础信息分页
  getInfoPage: async (params: any) => {
    return await request.get({ url: `/user/info/page`, params })
  },

  // 查询用户基础信息详情
  getInfo: async (id: number) => {
    return await request.get({ url: `/user/info/get?id=` + id })
  },

  // 新增用户基础信息
  createInfo: async (data: InfoVO) => {
    return await request.post({ url: `/user/info/create`, data })
  },

  // 修改用户基础信息
  updateInfo: async (data: InfoVO) => {
    return await request.put({ url: `/user/info/update`, data })
  },

  // 删除用户基础信息
  deleteInfo: async (id: number) => {
    return await request.delete({ url: `/user/info/delete?id=` + id })
  },

  // 导出用户基础信息 Excel
  exportInfo: async (params) => {
    return await request.download({ url: `/user/info/export-excel`, params })
  },

  // 导出用户购买信息 Excel
  exportUserPurchase: async (params) => {
    return await request.download({ url: `/user/info/export-purchase-excel`, params })
  },

  // 审核2FA解绑请求
  approve2FAUnbind: async (data) => {
    return await request.post({ url: `/user/info/approve-2fa-unbind`, data })
  }
}
