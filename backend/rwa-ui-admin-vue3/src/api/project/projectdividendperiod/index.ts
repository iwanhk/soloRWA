import request from '@/config/axios'

// 分红周期 VO
export interface DividendPeriodVO {
  id: number // ID
  projectId: number // 项目id
  periodSeq: number // 分红期数
  unlockDate: Date // 解锁日期
}

// 分红周期 API
export const DividendPeriodApi = {
  // 查询分红周期分页
  getDividendPeriodPage: async (params: any) => {
    return await request.get({ url: `/project/dividend-period/page`, params })
  },

  // 查询分红周期详情
  getDividendPeriod: async (id: number) => {
    return await request.get({ url: `/project/dividend-period/get?id=` + id })
  },

  // 新增分红周期
  createDividendPeriod: async (data: DividendPeriodVO) => {
    return await request.post({ url: `/project/dividend-period/create`, data })
  },

  // 修改分红周期
  updateDividendPeriod: async (data: DividendPeriodVO) => {
    return await request.put({ url: `/project/dividend-period/update`, data })
  },

  // 删除分红周期
  deleteDividendPeriod: async (id: number) => {
    return await request.delete({ url: `/project/dividend-period/delete?id=` + id })
  },

  // 导出分红周期 Excel
  exportDividendPeriod: async (params) => {
    return await request.download({ url: `/project/dividend-period/export-excel`, params })
  }
}