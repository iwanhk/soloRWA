# Solo RWA Frontend

这是一个基于 UniApp + Vue 3 + TypeScript 的跨平台应用项目。

## 项目说明

### 技术栈
- **框架**: UniApp (跨平台开发框架)
- **UI 框架**: Vue 3 + TypeScript
- **构建工具**: Vite 5
- **状态管理**: Pinia
- **HTTP 客户端**: luch-request
- **国际化**: Vue-i18n
- **Web3**: ethers + Web3Modal
- **其他**: PDF 预览 (pdfjs-dist)、电话号码验证等

### 支持平台
- H5 (Web)
- App (Android/iOS)
- 小程序 (微信、支付宝、百度、抖音等)
- 快应用

## 环境要求

### 必需工具
- **HBuilderX**: 用于运行和编译 UniApp 项目
  - 下载地址: https://www.dcloud.io/hbuilderx.html
  - 需要安装 `uni-app` 扩展插件

- **Node.js**: >= 16.0.0 (用于管理依赖)
- **pnpm**: 包管理工具 (推荐使用)

## 快速开始

### 1. 安装依赖
```bash
cd frontend
pnpm install
```

### 2. 开发模式 (H5)
```bash
pnpm run dev:h5
```
- 应用将运行在 `http://localhost:8700`
- 支持热模块替换 (HMR)

### 3. 生产构建 (H5)
```bash
pnpm run build:h5
```

### 4. 其他平台开发/构建
```bash
# 开发模式
pnpm run dev:mp-weixin    # 微信小程序
pnpm run dev:mp-alipay    # 支付宝小程序
pnpm run dev:mp-baidu     # 百度小程序
# 更多平台查看 package.json

# 生产构建
pnpm run build:mp-weixin
pnpm run build:mp-alipay
# ... 等等
```

## 重要配置说明

### manifest.json
位置: `frontend/src/manifest.json`

**首次运行注意**: 如果是新环境或首次编译，需要手动生成 `appid`：
- 打开 HBuilderX
- 右键项目 → 发行 → 选择目标平台
- HBuilderX 会自动生成唯一的 `appid`
- 或访问 [DCloud AppID 申请页面](https://ask.dcloud.net.cn/article/35985)

### .env 环境变量
位置: `frontend/.env`

#### 关键环境变量说明：

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `VITE_PROXY_PREFIX` | 开发时 API 代理前缀 | `/api` |
| `VITE_PROXY_TARGET` | 开发时 API 代理目标地址 | `http://localhost:3000` |
| `VITE_BASE_URL` | **应用基础 URL**（重要） | `http://rwa.solo.com.hk` |
| `VITE_IMAGE_BASE_URL` | 图片资源路径前缀 | `/images` |
| `VITE_IMAGE_PROXY_TARGET` | 图片资源代理目标 | (待配置) |
| `VITE_PORT` | 开发服务器端口 | `8700` |
| `VITE_REGION` | 地域配置 | `foreign` |
| `VITE_ENV_SIGNATURE` | 环境签名（可选） | (待配置) |
| `VITE_REOWN_PROJECT_ID` | Web3Modal 项目 ID | (已配置) |

## 部署指南

### 开发环境部署
```bash
# 在 HBuilderX 中运行
pnpm run dev:h5
```

### 生产环境部署

#### 1. 修改 .env 配置
确保配置正确的生产环境参数：
```env
VITE_BASE_URL=https://your-domain.com
VITE_PROXY_TARGET=https://api.your-domain.com
VITE_IMAGE_PROXY_TARGET=https://images.your-domain.com
```

#### 2. **关键：配置反向代理**
在实际部署时，需要在服务器上配置反向代理，将以下路径映射到后端服务：

**Nginx 配置示例**:
```nginx
# API 代理
location /api/ {
    proxy_pass http://backend-server:3000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}

# 图片资源代理
location /images/ {
    proxy_pass http://image-server:8080/;
    proxy_set_header Host $host;
}
```

**Apache 配置示例**:
```apache
ProxyPass /api/ http://backend-server:3000/
ProxyPassReverse /api/ http://backend-server:3000/

ProxyPass /images/ http://image-server:8080/
ProxyPassReverse /images/ http://image-server:8080/
```

#### 3. 构建和发布
```bash
# 构建 H5 版本
pnpm run build:h5

# 将 dist/h5 目录部署到服务器
# 使用 Nginx、Apache 或其他 Web 服务器提供静态文件服务
```

## 常见问题

### Q: 如何在 HBuilderX 中打开项目？
A: 
1. 在 HBuilderX 中点击 文件 → 打开文件夹
2. 选择 `frontend` 目录
3. HBuilderX 会自动识别为 UniApp 项目

### Q: 如何修改应用名称和版本号？
A: 编辑 `frontend/src/manifest.json`
- `name`: 应用名称
- `versionName`: 版本号 (如 "1.0.0")
- `versionCode`: 版本代码 (整数，用于发行)

### Q: 开发时如何连接后端服务？
A: 修改 `.env` 中的 `VITE_PROXY_TARGET` 和 `VITE_IMAGE_PROXY_TARGET`，Vite 会自动代理请求。

### Q: 生产部署后接口返回 CORS 错误？
A: 确保服务器正确配置了反向代理，`VITE_PROXY_PREFIX` 和 `VITE_IMAGE_BASE_URL` 必须代理到实际的后端服务。

### Q: 如何使用国际化功能？
A: 
```bash
pnpm run i18n:add       # 添加新的国际化键
pnpm run i18n:remove    # 删除国际化键
pnpm run i18n:list      # 列出所有国际化键
```

## 文件结构

```
frontend/
├── src/
│   ├── api/              # API 接口定义
│   ├── components/       # 可复用组件
│   ├── composable/       # Vue 3 Composition API 功能
│   ├── pages/            # 页面组件
│   ├── store/            # Pinia 状态管理
│   ├── styles/           # 全局样式
│   ├── types/            # TypeScript 类型定义
│   ├── i18n/             # 国际化配置
│   ├── locale/           # 国际化文本
│   ├── manifest.json     # UniApp 应用配置（重要）
│   ├── main.ts           # 应用入口
│   └── App.vue           # 根组件
├── .env                  # 环境变量（重要）
├── vite.config.ts        # Vite 构建配置
├── package.json          # 项目依赖
└── README.md            # 本文件
```

## 脚本命令

### 开发命令
- `pnpm run dev:h5` - H5 开发模式

### 构建命令
- `pnpm run build:h5` - 构建 H5 版本

### 其他命令
- `pnpm run type-check` - TypeScript 类型检查
- `pnpm run i18n` - 国际化文本更新

## 资源链接

- [UniApp 官方文档](https://uniapp.dcloud.io/)
- [Vue 3 文档](https://vuejs.org/)
- [HBuilderX 使用指南](https://hbuilderx.dcloud.io/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
