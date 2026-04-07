import request from '@/config/axios'

// 用户登录日志 VO
export interface LoginLogVO {
  id: number // 日志ID
  userId: number // 登录用户ID
  loginType: number // 登录方式：1-密码登录 2-验证码登录
  loginTime: Date // 登录时间
  loginIp: string // 登录IP地址
  loginCity: string // 登录城市（如“中国 香港”）
  deviceInfo: string // 设备信息（可选：如手机型号/浏览器标识）
  loginStatus: number // 登录状态：1-成功 2-失败（失败时可记录原因）
  failReason: string // 登录失败原因（如“密码错误”，登录成功时为空）
}

// 用户登录日志 API
export const LoginLogApi = {
  // 查询用户登录日志分页
  getLoginLogPage: async (params: any) => {
    return await request.get({ url: `/user/login-log/page`, params })
  },

  // 查询用户登录日志详情
  getLoginLog: async (id: number) => {
    return await request.get({ url: `/user/login-log/get?id=` + id })
  },

  // 新增用户登录日志
  createLoginLog: async (data: LoginLogVO) => {
    return await request.post({ url: `/user/login-log/create`, data })
  },

  // 修改用户登录日志
  updateLoginLog: async (data: LoginLogVO) => {
    return await request.put({ url: `/user/login-log/update`, data })
  },

  // 删除用户登录日志
  deleteLoginLog: async (id: number) => {
    return await request.delete({ url: `/user/login-log/delete?id=` + id })
  },

  // 导出用户登录日志 Excel
  exportLoginLog: async (params) => {
    return await request.download({ url: `/user/login-log/export-excel`, params })
  }
}