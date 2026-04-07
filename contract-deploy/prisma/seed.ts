import { PrismaClient } from '@prisma/client';
import { executeAllSeeds } from './seeds';

const prisma = new PrismaClient();

async function main() {
  console.log('🌱 开始数据库 seed...');

  await executeAllSeeds(prisma);

  console.log('✅ 数据库 seed 完成！');
}

main()
  .then(async () => {
    await prisma.$disconnect();
  })
  .catch(async (e) => {
    console.error('❌ Seed 失败:', e);
    await prisma.$disconnect();
    process.exit(1);
  });

