const path = require('path');
const dotenv = require('dotenv');

// Load test environment variables
dotenv.config({ path: path.resolve(__dirname, '.env.test') });

module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'node',
  testPathIgnorePatterns: ['<rootDir>/test/fixtures'],
  coveragePathIgnorePatterns: ['<rootDir>/test/'],
};