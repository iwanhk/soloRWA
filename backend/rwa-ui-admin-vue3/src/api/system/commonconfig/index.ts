import request from '@/config/axios'

// 参数配置 VO
export interface CommonConfigVO {
  id: number // 参数主键
  name: string // 参数名称
  configKey: string // 参数键名
  value: string // 参数键值
  isApp: boolean // 前端可用
  remark: string // 备注
}

// 参数配置 API
export const CommonConfigApi = {
  // 查询参数配置分页
  getCommonConfigPage: async (params: any) => {
    return await request.get({ url: `/system/common-config/page`, params })
  },

  // 查询参数配置详情
  getCommonConfig: async (id: number) => {
    return await request.get({ url: `/system/common-config/get?id=` + id })
  },

  // 新增参数配置
  createCommonConfig: async (data: CommonConfigVO) => {
    return await request.post({ url: `/system/common-config/create`, data })
  },

  // 修改参数配置
  updateCommonConfig: async (data: CommonConfigVO) => {
    return await request.put({ url: `/system/common-config/update`, data })
  },

  // 删除参数配置
  deleteCommonConfig: async (id: number) => {
    return await request.delete({ url: `/system/common-config/delete?id=` + id })
  },

  // 导出参数配置 Excel
  exportCommonConfig: async (params) => {
    return await request.download({ url: `/system/common-config/export-excel`, params })
  }
}