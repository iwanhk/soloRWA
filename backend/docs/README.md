# RWA Bamboo Cloud 文档集（入口）

本文档用于作为本仓库的“文档集入口”，提供项目总览、模块索引、启动方式、配置与部署入口的统一导航。

## 1. 项目概览

本仓库是一个以 Spring Cloud Alibaba 为基础的后端微服务 + Vue3 管理后台的工程集合：

- 后端：Maven 多模块聚合工程（网关 + 多个业务服务），统一版本管理与自研 Starter 扩展
- 前端：`rwa-ui-admin-vue3`（Vue3 + Vite + Element Plus）
- 说明：根目录的 [README.md](../README.md) 主要来自上游“芋道”体系说明，其中会提到本仓库未包含的模块（例如支付/工作流/报表等）。以本文档的“实际模块列表”为准。

## 2. 技术栈（按当前仓库实际内容）

- Java：JDK 8（根 [pom.xml](../pom.xml) 的 `java.version`）
- Spring Boot：2.7.18（根 [pom.xml](../pom.xml)）
- Spring Cloud：2021.x（版本由 [rwa-dependencies](../rwa-dependencies/pom.xml) 管理）
- 注册/配置中心：Nacos（各服务 `spring.config.import` 支持本地 + Nacos）
- 网关：Spring Cloud Gateway（[rwa-gateway](../rwa-gateway)）
- 数据访问：MyBatis-Plus（各服务 `mybatis-plus` 配置）
- 前端：Vue 3 + Vite + TypeScript（[rwa-ui-admin-vue3](../rwa-ui-admin-vue3)）
- 容器化：各服务提供 Dockerfile，仓库提供 compose 示例（[script/docker/docker-compose.yml](../script/docker/docker-compose.yml)）

## 3. 仓库结构

后端（Maven modules 以根 [pom.xml](../pom.xml) 为准）：

- `rwa-dependencies`：依赖版本 BOM
- `rwa-framework`：自研 Spring Boot Starter 与公共组件
- `rwa-gateway`：网关服务（统一入口、聚合文档、路由转发）
- `rwa-module-system`：系统域（api + biz）
- `rwa-module-infra`：基础设施域（api + biz）
- `rwa-module-user`：用户域（api + biz）
- `rwa-module-project`：项目域（api + biz）
- `rwa-module-chain`：链业务域（api + biz）

前端：

- `rwa-ui-admin-vue3`：管理后台（Vue3）

SQL：

- `sql/`：数据库脚本与工具（参考 [sql/tools/README.md](../sql/tools/README.md)）

## 4. 模块说明（后端）

后端模块通常采用 `api` + `biz` 的拆分方式：

- `*-api`：对外暴露的 DTO/VO、Feign API、公共常量与契约
- `*-biz`：具体业务实现（Spring Boot 应用、Controller/Service/DAO 等）

建议阅读入口（启动类）：

- 网关：`rwa-gateway` 的 `GatewayServerApplication`
- system：`rwa-module-system-biz` 的 `SystemServerApplication`
- infra：`rwa-module-infra-biz` 的 `InfraServerApplication`
- user：`rwa-module-user-biz` 的 `UserServerApplication`
- project：`rwa-module-project-biz` 的 `ProjectServerApplication`
- chain：`rwa-module-chain-biz` 的 `ChainServerApplication`（如存在）

## 5. 端口与路由

### 5.1 端口规划（默认）

以各服务 `application.yaml` 为准，常见默认端口如下：

- gateway：48080（[rwa-gateway/application.yaml](../rwa-gateway/src/main/resources/application.yaml)）
- system：48081（[system/application.yaml](../rwa-module-system/rwa-module-system-biz/src/main/resources/application.yaml)）
- infra：48082
- user：48083
- project：48084
- chain：48085

### 5.2 网关路由

网关通过 `Path` 前缀将请求转发到对应服务，常见格式：

- `/admin-api/{domain}/**`：管理端接口
- `/app-api/{domain}/**`：用户端接口

路由定义见 [rwa-gateway/application.yaml](../rwa-gateway/src/main/resources/application.yaml)。

注意：网关配置文件里还包含上游体系的部分模块路由/聚合文档项（例如 `member/bpm/pay/mp/...`），如果当前仓库未包含对应服务，需要按实际情况精简或补齐服务。

## 6. 本地开发（建议流程）

### 6.1 后端（Maven）

1) 安装 JDK 8 与 Maven

2) 根目录构建：

```bash
mvn -B package -Dmaven.test.skip=true
```

3) 按服务启动（推荐 IDE 直接运行各 `*ServerApplication` 启动类）

4) 配置方式（两种来源同时支持）：

- 本地配置：`application-{profile}.yaml`
- Nacos 配置：`{spring.application.name}-{profile}.yaml`

各服务通过 `spring.config.import` 同时加载两者（以各服务 `application.yaml` 为准）。

### 6.2 前端（Vue3）

前端位于 [rwa-ui-admin-vue3](../rwa-ui-admin-vue3)，详细说明见其 [README.md](../rwa-ui-admin-vue3/README.md)。

常见脚本（以 package.json 为准）：

- `pnpm dev`：本地开发
- `pnpm build:*`：按环境构建

### 6.3 数据库与脚本

SQL 工具与测试数据库 compose 见 [sql/tools/README.md](../sql/tools/README.md)。

## 7. 配置与安全要点

- 多环境：各服务默认 `spring.profiles.active=local`，并存在 `application-dev.yaml` / `application-local.yaml` 等文件
- 机密信息：请不要将数据库密码、Nacos 密钥、第三方 API Key 等写入仓库；建议使用环境变量或配置中心注入
- 日志：默认写入 `${user.home}/logs/`（各服务 `logging.file.name`）

## 8. 部署入口（Docker）

- Dockerfile：
  - 网关：[rwa-gateway/Dockerfile](../rwa-gateway/Dockerfile)
  - system-biz：[rwa-module-system-biz/Dockerfile](../rwa-module-system/rwa-module-system-biz/Dockerfile)
  - infra-biz：[rwa-module-infra-biz/Dockerfile](../rwa-module-infra/rwa-module-infra-biz/Dockerfile)
- Compose 示例：[script/docker/docker-compose.yml](../script/docker/docker-compose.yml)

注意：compose 示例中可能包含当前仓库不存在的镜像条目，需要按实际模块裁剪。
