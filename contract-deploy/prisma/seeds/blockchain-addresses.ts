/**
 * BlockchainAddress Seed 数据和函数
 * 用户只需提供私钥，地址会自动从私钥推导
 */

import { PrismaClient } from '@prisma/client';
import { privateKeyToAccount } from 'web3-eth-accounts';

export const blockchainAddressesData = [
  {
    name: 'deployer',
    privateKey: '0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80',
    description: ''
  },
];

export async function seedBlockchainAddresses(prisma: PrismaClient) {
  console.log('📝 开始 seed BlockchainAddress...');

  if (blockchainAddressesData.length === 0) {
    console.log('⏭️  没有 BlockchainAddress 数据，跳过');
    return;
  }

  for (const data of blockchainAddressesData) {
    try {
      // 从私钥推导地址
      const account = privateKeyToAccount(data.privateKey);
      const address = account.address;

      const existing = await prisma.blockchainAddress.findUnique({
        where: { address }
      });

      if (existing) {
        console.log(`⏭️  BlockchainAddress ${data.name} 已存在，跳过`);
        continue;
      }

      await prisma.blockchainAddress.create({
        data: {
          name: data.name,
          address,
          privateKey: data.privateKey,
          description: data.description
        }
      });
      console.log(`✅ 创建 BlockchainAddress: ${data.name} (${address})`);
    } catch (error) {
      console.error(`❌ 创建 BlockchainAddress ${data.name} 失败:`, error);
    }
  }
}

