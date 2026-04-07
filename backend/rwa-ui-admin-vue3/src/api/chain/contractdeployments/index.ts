import request from '@/config/axios'

// 合约部署 VO
export interface ContractDeploymentsVO {
  id: number // ID
  contractType: string // 合约类型
  deploymentAddress: string // 部署地址
  deployerAddress: string // 部署者地址
  deploymentInfo: string // 部署信息，JSON格式
  transactionHash: string // 交易哈希
  blockNumber: number // 区块号
  status: string // 状态，如deployed-已部署
}

// 合约部署 API
export const ContractDeploymentsApi = {
  // 查询合约部署分页
  getContractDeploymentsPage: async (params: any) => {
    return await request.get({ url: `/chain/contract-deployments/page`, params })
  },

  // 查询合约部署详情
  getContractDeployments: async (id: number) => {
    return await request.get({ url: `/chain/contract-deployments/get?id=` + id })
  },

  // 新增合约部署
  createContractDeployments: async (data: ContractDeploymentsVO) => {
    return await request.post({ url: `/chain/contract-deployments/create`, data })
  },

  // 修改合约部署
  updateContractDeployments: async (data: ContractDeploymentsVO) => {
    return await request.put({ url: `/chain/contract-deployments/update`, data })
  },

  // 删除合约部署
  deleteContractDeployments: async (id: number) => {
    return await request.delete({ url: `/chain/contract-deployments/delete?id=` + id })
  },

  // 导出合约部署 Excel
  exportContractDeployments: async (params) => {
    return await request.download({ url: `/chain/contract-deployments/export-excel`, params })
  }
}