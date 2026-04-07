/**
 * Seed 统一导出文件
 * 在这里手动指定 seed 执行顺序
 */

import { PrismaClient } from '@prisma/client';
import { seedBlockchainAddresses } from './blockchain-addresses';
import { seedClaimTopics } from './claim-topics';

// 定义 seed 执行顺序（数组中的顺序就是执行顺序）
export const seedFunctions = [
  seedBlockchainAddresses,
  seedClaimTopics
];

// 执行所有 seed
export async function executeAllSeeds(prisma: PrismaClient) {
  for (const seedFn of seedFunctions) {
    await seedFn(prisma);
  }
}

