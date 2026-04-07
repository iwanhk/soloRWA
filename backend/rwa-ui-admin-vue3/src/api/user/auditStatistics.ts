import request from '@/config/axios'

export interface UserAuditStatisticsVO {
    userAuditCount: number
    bankCardAuditCount: number
}

// 获取用户审核统计
export const getUserAuditStatistics = () => {
    return request.get<UserAuditStatisticsVO>({ url: '/user/audit-statistics' })
}
