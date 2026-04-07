import request from '@/config/axios'

// 币种简单 VO
export interface SystemCoinSimpleVO {
  id: number
  coinCode: string
  coinName: string
  coinType: number
  symbol: string
  iconUrl: string
}

// 币种 API
export const SystemCoinApi = {
  // 获得币种下拉列表
  getCoinList: async (coinType?: number) => {
    return await request.get<SystemCoinSimpleVO[]>({
      url: `/project/system-coin/list`,
      params: { coinType }
    })
  },

  // 获取法币列表 (coinType=2)
  getFiatCoinList: async () => {
    return await request.get<SystemCoinSimpleVO[]>({
      url: `/project/system-coin/list`,
     // params: { coinType: 2 }
    })
  },

  // 获取数字货币列表 (coinType=1)
  getCryptoCoinList: async () => {
    return await request.get<SystemCoinSimpleVO[]>({
      url: `/project/system-coin/list`,
      params: { coinType: 1 }
    })
  }
}
