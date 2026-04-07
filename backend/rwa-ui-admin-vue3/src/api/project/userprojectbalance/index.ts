import request from '@/config/axios'

// 用户项目余额表（本金/收益汇总） VO
export interface UserProjectBalanceVO {
  id: number // 记录ID
  userId: number // 用户ID
  projectId: number // 项目ID（关联project_core.id）
  principalAmount: number // 本金金额(U)（=申购份额×项目单价）
  holdQuantity: number // 当前持有份额(份)（赎回后扣减）
  totalIncome: number // 累计总收益(U)（含未提取）
  withdrawnDividend: number // 已提取分红(U)
  unwithdrawnDividend: number // 未提取分红(U)（=总收益-已提取）
  totalRedemptionAmount: number // 累计赎回本金(U)（赎回时累加）
  lastIncomeCalcTime: Date // 最后一次收益计算时间
  lastWithdrawTime: Date // 最后一次分红提取时间
  balanceStatus: number // 余额状态：1-正常 2-已赎回 3-冻结
}

// 用户项目余额表（本金/收益汇总） API
export const UserProjectBalanceApi = {
  // 查询用户项目余额表（本金/收益汇总）分页
  getUserProjectBalancePage: async (params: any) => {
    return await request.get({ url: `/project/user-project-balance/page`, params })
  },

  // 查询用户项目余额表（本金/收益汇总）详情
  getUserProjectBalance: async (id: number) => {
    return await request.get({ url: `/project/user-project-balance/get?id=` + id })
  },

  // 新增用户项目余额表（本金/收益汇总）
  createUserProjectBalance: async (data: UserProjectBalanceVO) => {
    return await request.post({ url: `/project/user-project-balance/create`, data })
  },

  // 修改用户项目余额表（本金/收益汇总）
  updateUserProjectBalance: async (data: UserProjectBalanceVO) => {
    return await request.put({ url: `/project/user-project-balance/update`, data })
  },

  // 删除用户项目余额表（本金/收益汇总）
  deleteUserProjectBalance: async (id: number) => {
    return await request.delete({ url: `/project/user-project-balance/delete?id=` + id })
  },

  // 导出用户项目余额表（本金/收益汇总） Excel
  exportUserProjectBalance: async (params) => {
    return await request.download({ url: `/project/user-project-balance/export-excel`, params })
  }
}
