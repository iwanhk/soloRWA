import request from '@/config/axios'

// 项目运营统计表 VO
export interface OperationVO {
  projectId: number // 关联project_core.id
  investorCount: number // 参与投资人数
  dividendApplyCount: number // 申请分红人数
  dividendApplyAmount: number // 申请分红金额(U)
  earlyRedemptionPeople: number // 提前赎回人数
  earlyRedemptionAmount: number // 提前赎回金额(U)
  earlyRedemptionCount: number // 提前赎回份额
  maturityRedemptionCount: number // 到期赎回人数
  maturityRedemptionAmount: number // 到期赎回金额(U)
  totalInvestorIncome: number // 投资人总收益(U)
  totalInvestorYield: number // 投资人总收益率(%)
  projectIncome: number // 项目方收益(U)
}

// 项目运营统计表 API
export const OperationApi = {
  // 查询项目运营统计表分页
  getOperationPage: async (params: any) => {
    return await request.get({ url: `/project/operation/page`, params })
  },

  // 查询项目运营统计表详情
  getOperation: async (id: number) => {
    return await request.get({ url: `/project/operation/get?id=` + id })
  },

  // 新增项目运营统计表
  createOperation: async (data: OperationVO) => {
    return await request.post({ url: `/project/operation/create`, data })
  },

  // 修改项目运营统计表
  updateOperation: async (data: OperationVO) => {
    return await request.put({ url: `/project/operation/update`, data })
  },

  // 删除项目运营统计表
  deleteOperation: async (id: number) => {
    return await request.delete({ url: `/project/operation/delete?id=` + id })
  },

  // 导出项目运营统计表 Excel
  exportOperation: async (params) => {
    return await request.download({ url: `/project/operation/export-excel`, params })
  }
}
