# 合约部署后端服务 (Contract Deploy Backend)

ERC3643 智能合约部署和管理系统。该项目基于 MidwayJS 框架，提供完整的链上身份管理、Token 部署和声明管理功能。

## 🚀 快速开始

### 1. 安装依赖

使用 npm 安装项目依赖：

```bash
npm install
```

### 2. 环境配置

编辑 `.env` 文件，配置数据库连接和 RPC 地址：

```bash
# 数据库配置
DATABASE_URL="mysql://用户名:密码@localhost:3306/rwa_contract"

# RPC 地址配置
RPC_URL="http://localhost:8545"  # 或其他链的 RPC 地址
```

### 3. 配置部署者私钥

编辑 `contract-deploy/prisma/seeds/blockchain-addresses.ts` 文件，修改 `deployer` 的私钥为实际的部署者私钥：

````typescript
export const blockchainAddressesData = [
  {
    name: 'deployer',
    privateKey: '0x你的正式部署者私钥',
    description: ''
  },
];
````

### 4. 数据库迁移

运行数据库迁移命令，将数据库 schema 应用到数据库：

```bash
npm run db:migrate:deploy
```

该命令会执行所有待处理的迁移。详细信息请查看 [DATABASE_GUIDE.md](./DATABASE_GUIDE.md)。

### 5. 启动服务（可选：修改端口）

#### 修改端口（可选）

编辑 `contract-deploy/src/config/config.default.ts` 来修改服务端口：

````typescript
export default {
  keys: '1765946061725_3444',
  koa: {
    port: 17123,  // 修改此端口号
  }
} as MidwayConfig;
````

#### 启动服务

**开发环境：**
```bash
npm run dev
# 访问 http://localhost:7001/
```

**生产环境：**
```bash
npm start
# 服务将在配置的端口启动，默认 17123
```

## 📋 后端 API 接口文档

所有 API 请求示例详见 `backend.http` 文件。该文件可在 VS Code 使用 REST Client 扩展运行。

### 基础配置

在 `backend.http` 中配置 API 主机地址：

```http
@host = http://localhost:17123/api
```

### 钱包管理接口

#### 添加钱包
```http
POST {{host}}/wallet
Content-Type: application/json

{
  "name": "钱包名称",
  "privateKey": "0x私钥",
  "description": "可选描述"
}
```

#### 获取钱包列表
```http
GET {{host}}/wallet
```

### 系统初始化接口

#### 获取初始化状态
```http
GET {{host}}/trex/status
```

返回系统是否已初始化以及各个初始化步骤的状态。

#### 初始化系统
```http
POST {{host}}/trex/initialize
```

初始化系统，部署所需的合约基础设施（需在部署 Token 前调用）。支持断点续传，如果中断可以继续执行。

### ClaimTopic 管理接口

#### 创建 ClaimTopic
```http
POST {{host}}/claim-topics
Content-Type: application/json

{
  "name": "平台验证",
  "value": "PLATFORM_VERIFIED"
}
```

说明：`value` 的 keccak256 哈希会自动生成为 `topic`。

#### 获取所有 ClaimTopic
```http
GET {{host}}/claim-topics
```

### ClaimIssuer 管理接口

#### 添加 ClaimIssuer
```http
POST {{host}}/identity/claim-issuer
Content-Type: application/json

{
  "address": "0x钱包地址"
}
```

说明：该地址将被设置为声明签发者，可以为用户签发声明。

#### 为用户签发 Claim
```http
POST {{host}}/identity/:id/issue-claim
Content-Type: application/json

{
  "userId": 1,
  "topic": "0x67de13167a807bb19655bccb2de80675f62130ccbf7146f75dbd7d79b9f3018c",
  "data": "0x签名数据",
  "uri": "https://did.example.com/verify-claim",
  "scheme": 1
}
```

参数说明：
- `userId`: 接收声明的用户 ID
- `topic`: Claim 主题的 keccak256 哈希
- `data`: Claim 签名数据（可选）
- `uri`: Claim 验证 URI（可选）
- `scheme`: 签名方案，1 = ECDSA_SIGNATURE（可选，默认为 1）

### 身份管理接口

#### 添加用户身份
```http
POST {{host}}/identity/user
Content-Type: application/json

{
  "address": "0x用户钱包地址",
  "token_id": 1,
  "countryCode": 156
}
```

参数说明：
- `address`: 用户钱包地址（必需）
- `token_id`: 关联的 Token ID（可选）
- `countryCode`: ISO 3166-1 数字国家代码，例如 156（中国）

#### 关联新钱包
```http
POST {{host}}/identity/link-wallet
Content-Type: application/json

{
  "new_wallet_address": "0x新钱包地址",
  "old_wallet_address": "0x旧钱包地址"
}
```

说明：将用户的新钱包与现有身份关联，允许用户转换钱包。

### Token 部署接口

#### 部署 ERC3643 Token
```http
POST {{host}}/trex/deploy-token
Content-Type: application/json

{
  "name": "Token 名称",
  "symbol": "代币符号",
  "ownerAddress": "0x所有者地址",
  "tokenAgents": ["0x代理地址"],
  "salt": "随机盐值",
  "claimTopics": ["0xTopic1", "0xTopic2"],
  "issuers": ["0x签发者地址"],
  "issuerClaims": [["0xTopic1"]]
}
```

参数说明：
- `name`: Token 名称（必需）
- `symbol`: Token 符号，如 "TEST"（必需）
- `ownerAddress`: Token 所有者地址（必需）
- `salt`: 合约地址生成用的盐值（必需，建议使用唯一值）
- `decimals`: Token 小数位数（可选，默认 18）
- `tokenAgents`: Token 代理地址数组（可选）
- `claimTopics`: Claim 主题数组（可选）
- `issuers`: 声明签发者地址数组（可选）
- `issuerClaims`: 签发者可签发的 Claim 主题映射（可选）

#### 发行 Token
```http
POST {{host}}/token/:id/mint
Content-Type: application/json

{
  "toAddress": "0x接收者地址",
  "amount": "1000000000000000000"
}
```

说明：向指定地址铸造 Token。`amount` 需要考虑小数位数（默认 18 位）。

#### 获取 Token 列表
```http
GET {{host}}/token
```

### npm 脚本

- `npm install` - 安装依赖
- `npm run dev` - 开发模式运行（带热重载）
- `npm run build` - 构建项目
- `npm start` - 生产模式运行
- `npm run db:migrate:dev` - 开发环境数据库迁移（创建迁移文件）
- `npm run db:migrate:deploy` - 生产环境数据库迁移（应用迁移）
- `npm run db:push` - 快速同步 Schema 到数据库（开发用）
- `npm run db:reset` - 重置数据库（开发用，删除所有数据）
- `npm run db:seed` - 执行 Seed 数据初始化
- `npm run test` - 运行单元测试
- `npm run lint` - 检查代码风格
- `npm run lint:fix` - 自动修复代码风格问题

## 📚 更多信息

详细的数据库操作指南请查看 [DATABASE_GUIDE.md](./DATABASE_GUIDE.md)

参考文档：[MidwayJS][midway]

[midway]: https://midwayjs.org
