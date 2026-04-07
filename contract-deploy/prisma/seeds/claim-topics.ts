/**
 * ClaimTopic Seed 数据和函数
 * 用户提供 name 和 value，topic 会自动通过 keccak256 计算
 */

import { PrismaClient } from '@prisma/client';
import { keccak256 } from 'web3-utils';

export const claimTopicsData = [
  {
    name: '平台认证',
    value: 'PLATFORM_VERIFIED'
  },
];

export async function seedClaimTopics(prisma: PrismaClient) {
  console.log('📝 开始 seed ClaimTopic...');

  if (claimTopicsData.length === 0) {
    console.log('⏭️  没有 ClaimTopic 数据，跳过');
    return;
  }

  for (const data of claimTopicsData) {
    try {
      const topic = keccak256(data.value);

      const existing = await prisma.claimTopic.findUnique({
        where: { value: data.value }
      });

      if (existing) {
        console.log(`⏭️  ClaimTopic ${data.name} 已存在，跳过`);
        continue;
      }

      await prisma.claimTopic.create({
        data: {
          name: data.name,
          value: data.value,
          topic
        }
      });
      console.log(`✅ 创建 ClaimTopic: ${data.name} (topic: ${topic})`);
    } catch (error) {
      console.error(`❌ 创建 ClaimTopic ${data.name} 失败:`, error);
    }
  }
}

