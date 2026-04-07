import request from '@/config/axios'

export interface TenantPackageVO {
  id: number
  name: string
  status: number
  remark: string
  creator: string
  updater: string
  updateTime: string
  menuIds: number[]
  createTime: Date
}

// 查询发行商套餐列表
export const getTenantPackagePage = (params: PageParam) => {
  return request.get({ url: '/system/tenant-package/page', params })
}

// 获得发行商
export const getTenantPackage = (id: number) => {
  return request.get({ url: '/system/tenant-package/get?id=' + id })
}

// 新增发行商套餐
export const createTenantPackage = (data: TenantPackageVO) => {
  return request.post({ url: '/system/tenant-package/create', data })
}

// 修改发行商套餐
export const updateTenantPackage = (data: TenantPackageVO) => {
  return request.put({ url: '/system/tenant-package/update', data })
}

// 删除发行商套餐
export const deleteTenantPackage = (id: number) => {
  return request.delete({ url: '/system/tenant-package/delete?id=' + id })
}

// 批量删除发行商套餐
export const deleteTenantPackageList = (ids: number[]) => {
  return request.delete({ url: '/system/tenant-package/delete-list', params: { ids: ids.join(',') } })
}

// 获取发行商套餐精简信息列表
export const getTenantPackageList = () => {
  return request.get({ url: '/system/tenant-package/simple-list' })
}
