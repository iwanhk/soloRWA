import request from '@/config/axios'

// 概览统计
export interface StatsOverviewVO {
    projectCount: number
    orderCount: number
    totalAmount: number
}

// 订单趋势
export interface OrderTrendVO {
    date: string
    orderCount: number
    amount: number
}

// 订单状态分布
export interface OrderStatusDistVO {
    name: string
    status: number
    count: number
}

// 热门项目
export interface TopProjectVO {
    projectId: number
    name: string
    orderCount: number
    amount: number
}

// 最新订单
export interface RecentOrderVO {
    id: number
    orderNo: string
    projectName: string
    amount: number
    status: number
    createTime: string
}

// 获取概览统计
export const getOverview = () => {
    return request.get<StatsOverviewVO>({ url: '/project/statistics/overview' })
}

// 获取订单趋势
export const getOrderTrend = () => {
    return request.get<OrderTrendVO[]>({ url: '/project/statistics/order-trend' })
}

// 获取订单状态分布
export const getOrderStatusDist = () => {
    return request.get<OrderStatusDistVO[]>({ url: '/project/statistics/order-status-dist' })
}

// 获取热门项目排行
export const getTopProjects = (limit?: number) => {
    return request.get<TopProjectVO[]>({ url: '/project/statistics/top-projects', params: { limit } })
}

// 获取最新订单
export const getRecentOrders = (limit?: number) => {
    return request.get<RecentOrderVO[]>({ url: '/project/statistics/recent-orders', params: { limit } })
}
