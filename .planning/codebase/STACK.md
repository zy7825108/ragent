# Technology Stack

**Analysis Date:** 2026-03-31

## Languages

**Primary:**
- Java 17 - 后端核心业务逻辑、框架、AI 基础设施
- TypeScript 5.5.4 - 前端开发（React 18 应用）

**Secondary:**
- JavaScript - 前端配置和脚本
- SQL - 数据库查询和初始化

## Runtime

**Environment:**
- JVM 17+ - 后端运行环境
- Node.js - 前端开发和构建

**Package Manager:**
- Maven 3.8.x - 后端依赖管理
- npm 10.x - 前端依赖管理
- Lockfile: present (package-lock.json, pom.xml with dependencyManagement)

## Frameworks

**Core:**
- Spring Boot 3.5.7 - 后端核心框架，提供自动配置、Web 支持、数据访问等
- React 18.3.1 - 前端 UI 框架
- Vite 5.4.3 - 前端构建工具

**Testing:**
- Spring Boot Test - 后端单元和集成测试
- Mockito 5.20.0 - 后端 mocking 框架
- 前端测试: Not explicitly configured in package.json

**Build/Dev:**
- Spotless 2.22.1 - Java 代码格式化
- Prettier 3.3.3 - 前端代码格式化
- ESLint 8.57.0 - 前端代码检查

## Key Dependencies

**Critical:**
- Milvus SDK 2.6.6 - 向量数据库客户端，用于文档向量化存储和检索
- Apache Tika 3.2.3 - 文档解析库，支持 PDF/DOC/DOCX/Markdown 等格式
- MyBatis Plus 3.5.14 - ORM 框架，简化数据库操作
- Sa-Token 1.43.0 - 认证鉴权框架
- Redisson 4.0.0 - Redis 客户端和分布式锁
- RocketMQ 2.3.5 - 消息队列，用于异步文档处理
- AWS S3 SDK 2.40.2 - 对象存储客户端

**Infrastructure:**
- Hutool 5.8.37 - Java 工具库
- Guava - Google 核心库
- OkHttp 4.12.0 - HTTP 客户端
- Transmittable Thread Local 2.14.5 - 线程本地变量传递

## Configuration

**Environment:**
- Spring Boot 配置文件: `application.yml` (各模块 src/main/resources 目录)
- 前端环境变量: `.env` 文件

**Build:**
- 后端: Maven pom.xml (根目录和各模块)
- 前端: Vite 配置 (vite.config.ts)

## Platform Requirements

**Development:**
- JDK 17+
- Node.js 16+
- Maven 3.8+
- Docker (可选，用于 Milvus/MySQL/Redis 等服务)

**Production:**
- 部署目标: Java 应用服务器 (Tomcat 或内置 Spring Boot 服务器)
- 数据库: MySQL 8.0+ 或 PostgreSQL (支持 pgvector 扩展)
- 向量数据库: Milvus 2.6+
- 缓存: Redis 6.0+
- 对象存储: S3 兼容存储

---

*Stack analysis: 2026-03-31*
