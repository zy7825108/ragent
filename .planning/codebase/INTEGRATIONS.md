# External Integrations

**Analysis Date:** 2026-03-31

## APIs & External Services

**AI 模型服务:**
- 百炼（阿里云）- 大语言模型服务，用于对话和文本生成
- SiliconFlow - AI 模型服务
- Ollama - 本地大语言模型运行框架
- vLLM - 高性能 LLM 推理框架（后续扩展）
  - SDK/Client: 自定义 HTTP 客户端 (OkHttp)
  - Auth: API 密钥通过环境变量配置

## Data Storage

**Databases:**
- MySQL - 关系型数据库，存储用户、知识库、会话等结构化数据
  - Connection: `spring.datasource` 配置
  - Client: MyBatis Plus ORM

- PostgreSQL (pgvector) - 关系型数据库，支持向量存储和相似度搜索
  - Connection: `spring.datasource` 配置
  - Client: MyBatis Plus ORM
  - Vector extension: pgvector 0.1.6

- H2 - 内存数据库，用于开发和测试
  - Connection: 内存模式
  - Client: Spring Boot JDBC

**向量数据库:**
- Milvus 2.6 - 分布式向量数据库，用于文档嵌入向量的存储和检索
  - Connection: `milvus` 配置 (host, port, token)
  - Client: Milvus Java SDK 2.6.6

**File Storage:**
- S3 兼容存储 (RustFS) - 对象存储服务，用于存储原始文档和解析后的内容
  - Connection: AWS S3 SDK 配置 (endpoint, access key, secret key)
  - Client: AWS S3 SDK 2.40.2

**Caching:**
- Redis 6.0+ - 分布式缓存，用于会话管理、限流和数据缓存
  - Connection: `spring.redis` 配置
  - Client: Redisson 4.0.0

## Authentication & Identity

**Auth Provider:**
- Sa-Token - 开源 Java 权限认证框架
  - Implementation: 基于 Token 的认证方式
  - 支持: 登录认证、权限验证、会话管理

## Monitoring & Observability

**Error Tracking:**
- SLF4J + Logback - 日志框架
- 日志配置: `logback-spring.xml`
- 链路追踪: 基于 AOP 的自定义 Trace 系统 (@RagTraceNode 注解)

**Logs:**
- 日志级别: DEBUG, INFO, WARN, ERROR
- 输出位置: 控制台和文件
- 文件路径: 配置在 logback 配置文件中

## CI/CD & Deployment

**Hosting:**
- 后端: Docker 容器化部署
- 前端: 静态文件部署 (Nginx 或 CDN)

**CI Pipeline:**
- GitHub Actions (配置文件未在代码库中明确看到)

## Environment Configuration

**Required env vars:**
- 数据库连接: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- Milvus 连接: `MILVUS_HOST`, `MILVUS_PORT`, `MILVUS_TOKEN`
- Redis 连接: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- 对象存储: `S3_ENDPOINT`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_BUCKET`
- 消息队列: `ROCKETMQ_NAME_SERVER`, `ROCKETMQ_PRODUCER_GROUP`
- AI 模型 API 密钥: 各模型供应商的 API 密钥

**Secrets location:**
- 开发环境: `.env` 文件 (前端), `application.yml` (后端)
- 生产环境: 环境变量或配置中心

## Webhooks & Callbacks

**Incoming:**
- 文档入库 Webhook: 接收文档上传和处理请求
- MCP 工具调用: 处理工具调用请求

**Outgoing:**
- 模型 API 调用: 向各 AI 模型服务发送请求
- 消息队列发送: 向 RocketMQ 发送异步任务消息

---

*Integration audit: 2026-03-31*
