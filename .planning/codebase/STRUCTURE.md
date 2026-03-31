# Codebase Structure

**Analysis Date:** 2026-03-31

## Directory Layout

```
ragent/
├── .claude/                    # Claude AI 相关配置
├── .git/                       # Git 仓库
├── .idea/                      # IntelliJ IDEA 配置
├── .planning/                  # 规划和架构文档
├── assets/                     # 项目资源文件
├── bootstrap/                  # 业务逻辑层
├── docs/                       # 项目文档
├── framework/                  # 通用能力层
├── frontend/                   # 前端项目
├── infra-ai/                   # AI 基础设施层
├── mcp-server/                 # MCP 服务器
├── pg/                         # PostgreSQL 相关代码
├── resources/                  # 资源文件
├── scripts/                    # 脚本文件
├── CLAUDE.md                   # Claude AI 工作指南
├── LICENSE                     # 许可证文件
├── README.md                   # 项目介绍
├── lombok.config               # Lombok 配置
├── mvnw/mvnw.cmd              # Maven 包装器
└── pom.xml                     # Maven 父项目配置
```

## Directory Purposes

**bootstrap:**
- Purpose: 业务逻辑层，实现 RAG 核心功能和业务流程
- Contains: RAG 核心逻辑、文档入库 pipeline、会话管理、API 接口
- Key files:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/rag` - RAG 核心实现
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/ingestion` - 文档入库管理
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/controller` - API 接口

**framework:**
- Purpose: 通用能力层，提供与业务无关的通用功能
- Contains: 工具函数、异常处理、配置管理、通用服务
- Key files: `/Users/zhongyang/Desktop/个人工作/study/ragent/framework/src/main/java`

**infra-ai:**
- Purpose: AI 基础设施层，屏蔽不同模型供应商的差异
- Contains: 模型调用、向量处理、工具集成、MCP 相关功能
- Key files: `/Users/zhongyang/Desktop/个人工作/study/ragent/infra-ai/src/main/java`

**mcp-server:**
- Purpose: 处理 MCP（模型上下文协议）的工具调用
- Contains: MCP 工具注册、执行和管理
- Key files: `/Users/zhongyang/Desktop/个人工作/study/ragent/mcp-server/src/main/java`

**frontend:**
- Purpose: 前端项目，提供用户问答界面和管理后台
- Contains: React 组件、API 调用、状态管理、UI 界面
- Key files:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/App.tsx` - 应用入口组件
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/components` - 通用组件
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/pages` - 页面组件

**pg:**
- Purpose: PostgreSQL 相关代码
- Contains: 数据库操作、向量存储、数据访问
- Key files: `/Users/zhongyang/Desktop/个人工作/study/ragent/pg/src/main/java`

**resources:**
- Purpose: 资源文件目录
- Contains: 配置文件、SQL 脚本、静态资源
- Key files:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/resources/sql` - 数据库初始化脚本

**scripts:**
- Purpose: 脚本文件目录
- Contains: 用于部署、测试、维护的脚本
- Key files: Shell 脚本、Python 脚本等

## Key File Locations

**Entry Points:**
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/RagentCoreApplication.java` - 后端启动类
- `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/main.tsx` - 前端入口文件

**Configuration:**
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/resources/application.yml` - Spring Boot 配置
- `/Users/zhongyang/Desktop/个人工作/study/ragent/pom.xml` - Maven 项目配置
- `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/package.json` - 前端依赖配置

**Core Logic:**
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/rag` - RAG 核心逻辑
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/ingestion` - 文档入库 pipeline
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/vector` - 向量数据库操作

**Testing:**
- `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/test/java` - 后端单元测试
- `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/__tests__` - 前端测试

## Naming Conventions

**Files:**
- Java 文件: PascalCase（类名）+ .java（扩展名），如 `RagentCoreApplication.java`
- TypeScript/JavaScript 文件: camelCase（组件/函数名）+ .ts/.tsx/.js/.jsx（扩展名），如 `userService.ts`
- Configuration files: 小写字母 + 扩展名，如 `application.yml`、`package.json`

**Directories:**
- 后端模块: 小写字母，如 `bootstrap`、`framework`、`infra-ai`、`mcp-server`
- 源代码目录: `src/main/java`（生产代码）、`src/test/java`（测试代码）
- 前端目录: `src/components`（通用组件）、`src/pages`（页面组件）、`src/utils`（工具函数）

## Where to Add New Code

**New Feature:**
- 后端业务逻辑: `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/` 下新建对应模块
- 前端组件: `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/components`（通用组件）或 `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/pages`（页面组件）
- 测试代码: 对应模块的 `src/test/java` 或 `src/__tests__` 目录

**New Component/Module:**
- 后端模块: 在 `/Users/zhongyang/Desktop/个人工作/study/ragent/` 下新建 Maven 模块，继承父项目配置
- 前端模块: 在 `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/` 下新建子目录

**Utilities:**
- 后端: `/Users/zhongyang/Desktop/个人工作/study/ragent/framework/src/main/java/com/nageoffer/ai/ragent/framework/util`
- 前端: `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/utils`

## Special Directories

**resources:**
- Purpose: 包含项目所需的资源文件
- Generated: No
- Committed: Yes

**scripts:**
- Purpose: 包含项目部署、测试、维护相关的脚本
- Generated: No
- Committed: Yes

**frontend/node_modules:**
- Purpose: 前端依赖包目录
- Generated: Yes（通过 npm install 生成）
- Committed: No（已添加到 .gitignore）

**target:**
- Purpose: 后端编译输出目录
- Generated: Yes（通过 Maven 编译生成）
- Committed: No（已添加到 .gitignore）

---

*Structure analysis: 2026-03-31*