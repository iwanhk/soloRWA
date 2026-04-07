import request from '@/config/axios'

// 项目核心表（基础+状态） VO
export interface InfoVO {
  projectId: number // 项目ID
  projectName: string // 项目名称
  projectType: number // 项目类
  assetType: number // 资产类型
  publisherUserId: number // 发行商
  publisherCompanyName: string // 发行商
  issueQuantity: number // 发行数量(份)
  issueUnitPrice: number // 发行单价(U)
  remainingQuantity: number // 剩余数量(份)【高频更新】
  expectedAnnualReturn: number // 预期年化收益
  minimumPurchase: number // 起购量
  issueChainId: number // 发行链ID（关联chain_manage）
  subscriptionStartTime: Date // 认购期-开始
  subscriptionEndTime: Date // 认购期-结束
  lockStartTime: Date // 锁定期-开始
  lockEndTime: Date // 锁定期-结束
  projectStatus: number // 状态：1-未开售 2-出售中 3-已售罄 4-盈利中
  sellStatus: number // 出售状态：0-下架 1-上架
  projectIntro: string // 项目介绍（图文）
  projectFileUrls: string // 项目资料URL
  subscriptionContractIds: string // 认购合同ID
  dividendContractIds: string // 分红合同ID
  maturityRedemptionContractIds: string // 到期赎回合同ID
  earlyRedemptionContractIds: string // 提前赎回合同ID
  earlyRedemptionFeeJson: string // 提前赎回手续费配置
  projectImageUrls: string // 项目图片URL
  projectVideoUrl: string // 项目视频URL
  projectConfigType?: number // 项目配置类型：0-挖矿型 1-基金型
  // 新增配置字段
  computingPower?: number // 总算力
  electricity?: number // 电费
  coin?: string // 币种
  peopleCost?: number // 人力成本
  costType?: number // 计算方式 0固定成本 1收益百分比
  poolAccessKey?: string // 矿池AccessKey
  hasPoolPrivateKey?: boolean // 是否已配置私钥
  poolName?: string // 结算账户
  powerConsumption?: number // 单T功耗
}

// 项目配置 VO
export interface ProjectConfigVO {
  projectId: number
  powerConsumption?: number // 单T功耗(W)
  electricityPrice?: number // 电费(元/度)
  operationCost?: number // 月运维成本
  operationCostType?: number // 运维成本类型:0-固定值/月 1-按天
  teamShareRatio?: number // 团队分成比例(%)
  thresholdMin?: number // 最低收益阈值(日)
  thresholdMax?: number // 最高收益阈值(日)
  alertEnabled?: number // 阈值预警:0-关 1-开
  poolAccessKey?: string // 矿池AccessKey
  poolPrivateKey?: string // 矿池私钥
  poolName?: string // 结算账户
  hasPoolPrivateKey?: boolean // 是否已配置私钥
}

// 项目精简信息 VO
export interface InfoSimpleVO {
  projectId: number // 项目ID
  projectName: string // 项目名称
}

// 项目核心表（基础+状态） API
export const InfoApi = {
  // 查询项目核心表（基础+状态）分页
  getInfoPage: async (params: any) => {
    return await request.get({ url: `/project/info/page`, params })
  },

  // 获得项目简易下拉
  getInfoSimple: async () => {
    return await request.get({ url: `/project/info/pageSimple` })
  },

  // 查询项目核心表（基础+状态）详情
  getInfo: async (projectId: number) => {
    return await request.get({ url: `/project/info/get?projectId=` + projectId })
  },

  // 新增项目核心表（基础+状态）
  createInfo: async (data: InfoVO) => {
    return await request.post({ url: `/project/info/create`, data })
  },

  // 修改项目核心表（基础+状态）
  updateInfo: async (data: InfoVO) => {
    return await request.put({ url: `/project/info/update`, data })
  },

  // 删除项目核心表（基础+状态）
  deleteInfo: async (projectId: number) => {
    return await request.delete({ url: `/project/info/delete?projectId=` + projectId })
  },

  // 导出项目核心表（基础+状态） Excel
  exportInfo: async (params) => {
    return await request.download({ url: `/project/info/export-excel`, params })
  },

  // 导出项目汇总（按项目分sheet） Excel
  exportProjectSummary: async (params) => {
    return await request.download({ url: `/project/info/export-summary-excel`, params })
  },

  // 审核项目
  auditProject: async (data: { projectId: number; approved: boolean; auditRemark?: string }) => {
    return await request.put({ url: `/project/info/audit`, data })
  },

  // 提交项目审核
  submitAudit: async (data: { projectId: number }) => {
    return await request.put({ url: `/project/info/submitAudit`, data })
  },

  // 更新上下架状态
  updateSellStatus: async (data: { projectId: number; sellStatus: number }) => {
    return await request.put({ url: `/project/info/sell-status`, data })
  },

  // 获取项目配置
  getConfig: async (projectId: number) => {
    return await request.get({ url: `/project/info/config?projectId=` + projectId })
  },

  // 更新项目配置
  updateConfig: async (data: ProjectConfigVO) => {
    return await request.put({ url: `/project/info/config`, data })
  },

  // 提交运行审核
  submitRunAudit: async (data: { projectId: number }) => {
    return await request.put({ url: `/project/info/submitRunAudit`, data })
  },

  // 审核运行
  auditRun: async (data: { projectId: number; approved: boolean; auditRemark?: string }) => {
    return await request.put({ url: `/project/info/auditRun`, data })
  },

  // 结束运行
  endRun: async (projectId: number) => {
    return await request.put({ url: `/project/info/endRun?projectId=${projectId}` })
  }
}
