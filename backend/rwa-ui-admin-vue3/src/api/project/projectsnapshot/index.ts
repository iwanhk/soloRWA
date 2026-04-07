import request from '@/config/axios'

export interface SnapshotVO {
    id: number
    projectId: number
    snapshotVersion: number
    snapshotStatus: number // 0=待审核 1=已通过 2=已拒绝
    operatorId?: number
    operatorName?: string
    auditRemark?: string
    projectData: string
    createTime: Date
}

export interface SnapshotPageReqVO extends PageParam {
    projectId?: number
    snapshotStatus?: number
}

// 获取项目最新快照
export const getLatestSnapshot = (projectId: number) => {
    return request.get<SnapshotVO>({ url: `/project/snapshot/latest?projectId=${projectId}` })
}

// 获取快照详情
export const getSnapshot = (id: number) => {
    return request.get<SnapshotVO>({ url: `/project/snapshot/get?id=${id}` })
}

// 分页查询项目快照
export const getSnapshotPage = (params: SnapshotPageReqVO) => {
    return request.get<PageResult<SnapshotVO>>({ url: '/project/snapshot/page', params })
}
