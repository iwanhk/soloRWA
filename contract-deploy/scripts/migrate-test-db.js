const { execSync } = require('child_process');
const path = require('path');

// Set test database URL
process.env.DATABASE_URL = 'mysql://root:root@localhost:3306/rwa_contract_test';

try {
  console.log('Running Prisma migrations for test database...');
  execSync('npx prisma migrate deploy', {
    cwd: path.resolve(__dirname, '..'),
    stdio: 'inherit',
  });
  console.log('Test database migrations completed successfully');
} catch (error) {
  console.error('Error running migrations:', error.message);
  process.exit(1);
}

