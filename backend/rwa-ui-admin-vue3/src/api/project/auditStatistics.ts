import request from '@/config/axios'

export interface ProjectAuditStatisticsVO {
    projectOnlineAuditCount: number
    projectRunningAuditCount: number
}

export interface OrderAuditStatisticsVO {
    orderAuditCount: number
}

export interface BillAuditStatisticsVO {
    billAuditCount: number
}

// 获取项目审核统计
export const getProjectAuditStatistics = () => {
    return request.get<ProjectAuditStatisticsVO>({ url: '/project/audit-statistics' })
}

// 获取订单审核统计
export const getOrderAuditStatistics = () => {
    return request.get<OrderAuditStatisticsVO>({ url: '/project/order-audit-statistics' })
}

// 获取账单审核统计
export const getBillAuditStatistics = () => {
    return request.get<BillAuditStatisticsVO>({ url: '/project/bill-audit-statistics' })
}
