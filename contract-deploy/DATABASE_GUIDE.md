# 数据库操作指南

## 📋 常用命令

### 开发环境 - 修改 Schema 后

```bash
# 创建新的迁移并应用到数据库
pnpm run db:migrate:dev

# 输入迁移名称，例如：add_user_table
```

这个命令会：
1. 生成迁移文件
2. 创建数据库（如果不存在）
3. 应用迁移到数据库
4. 生成 Prisma Client

### 生产环境 - 应用迁移

```bash
# 应用所有待处理的迁移
pnpm run db:migrate:deploy
```

**注意**：生产环境需要提前创建数据库

### 快速同步 Schema（不创建迁移）

```bash
# 直接同步 schema 到数据库（开发用）
pnpm run db:push
```

**警告**：这会直接修改数据库，不会生成迁移文件，不推荐在生产环境使用

### 重置数据库（开发用）

```bash
# 删除所有数据并重新运行所有迁移
pnpm run db:reset
```

**警告**：这会删除所有数据，仅在开发环境使用

## 🔄 工作流程

### 场景 1：修改数据库 Schema

```bash
# 1. 修改 prisma/schema.prisma
# 2. 运行迁移
pnpm run db:migrate:dev

# 3. 输入迁移名称（例如：add_status_field）
# 4. 完成！数据库已更新
```

### 场景 2：部署到生产环境

```bash
# 1. 在本地开发环境创建迁移
pnpm run db:migrate:dev

# 2. 提交迁移文件到 Git
git add prisma/migrations/
git commit -m "Add new migration"

# 3. 在生产环境应用迁移
pnpm run db:migrate:deploy
```

### 场景 3：运行测试

```bash
# 首次运行
pnpm run test:setup    # 创建测试数据库
pnpm test              # 运行测试

# 后续运行
pnpm test              # 直接运行
```

## 📁 迁移文件结构

```
prisma/
├── schema.prisma                    # 数据模型定义
└── migrations/
    ├── 20251217044843_init/
    │   └── migration.sql            # 初始迁移
    ├── 20251217050000_add_field/
    │   └── migration.sql            # 新增字段迁移
    └── migration_lock.toml          # 迁移锁文件
```

## ⚠️ 注意事项

### 不要手动修改迁移文件
- 迁移文件由 Prisma 自动生成
- 手动修改可能导致数据库不一致

### 迁移文件应该提交到 Git
```bash
git add prisma/migrations/
git commit -m "Add database migration"
```

### 生产环境数据库需要提前创建
```bash
# 在生产环境运行迁移前，需要先创建数据库
mysql -u root -p"password" -e "CREATE DATABASE rwa_contract;"
```

## 🔍 查看迁移历史

```bash
# 查看所有迁移
ls prisma/migrations/

# 查看特定迁移的 SQL
cat prisma/migrations/20251217044843_init/migration.sql
```

## 🆘 常见问题

### Q: 修改了 Schema 但忘记运行迁移怎么办？
A: 运行 `pnpm run db:migrate:dev` 创建迁移，然后应用到数据库

### Q: 迁移失败了怎么办？
A: 
1. 检查错误信息
2. 修复 schema.prisma
3. 重新运行 `pnpm run db:migrate:dev`

### Q: 如何回滚迁移？
A: Prisma 不支持自动回滚，需要手动：
1. 修改 schema.prisma 回到之前的状态
2. 创建新的迁移来撤销更改
3. 运行新迁移

### Q: 测试数据库和正式数据库如何分离？
A: 已配置在 `.env` 和 `.env.test` 中
- 正式：`rwa_contract`
- 测试：`rwa_contract_test`

## 📊 命令速查表

| 命令 | 用途 | 环境 |
|------|------|------|
| `db:migrate:dev` | 创建迁移并应用 | 开发 |
| `db:migrate:deploy` | 应用迁移 | 生产 |
| `db:push` | 快速同步 Schema | 开发 |
| `db:reset` | 重置数据库 | 开发 |
| `test:setup` | 创建测试数据库 | 测试 |
| `test` | 运行测试 | 测试 |

