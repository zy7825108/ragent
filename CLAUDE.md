```
# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
```

## 项目概述

Ragent 是一个企业级 RAG（检索增强生成）智能体平台，基于 Java 17 + Spring Boot 3 + React 18 构建。它覆盖了 RAG 系统从文档入库到智能问答全链路的完整工程实现，包括多路检索、意图识别、问题重写、会话记忆、模型容错、MCP 工具调用、链路追踪等核心能力。

## 代码架构

### 后端模块架构

项目采用 Maven 多模块架构，分为四个核心模块：

1. **bootstrap** - 业务逻辑层，专注于具体业务实现
2. **framework** - 通用能力层，提供与业务无关的通用功能
3. **infra-ai** - AI 基础设施层，屏蔽不同模型供应商的差异
4. **mcp-server** - MCP（Model Context Protocol）服务器，处理工具调用

### 前端架构

前端使用 React 18 + TypeScript + Vite 构建，分为用户问答界面和管理后台两部分：
- 用户界面：提供自然语言问答、深度思考模式、示例问题等功能
- 管理后台：提供知识库管理、意图树编辑、入库监控、链路追踪、系统设置等功能

## 开发命令

### 后端开发

```bash
# 编译项目
./mvnw compile

# 运行项目
./mvnw spring-boot:run -pl bootstrap

# 运行单元测试
./mvnw test

# 运行特定模块的测试
./mvnw test -pl framework

# 格式化代码（使用 Spotless 插件）
./mvnw spotless:apply
```

### 前端开发

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 运行测试
npm run test

# 格式化代码
npm run format
```

## 核心技术栈

| 层面       | 技术选型                                                     |
| ---------- | ------------------------------------------------------------ |
| 后端框架   | Java 17、Spring Boot 3.5.7、MyBatis Plus                     |
| 前端框架   | React 18、Vite、TypeScript                                   |
| 关系数据库 | MySQL                                                       |
| 向量数据库 | Milvus 2.6                                                   |
| 缓存/限流  | Redis + Redisson                                             |
| 对象存储   | S3 兼容存储（RustFS）                                        |
| 消息队列   | RocketMQ 5.x                                                 |
| 文档解析   | Apache Tika 3.2                                              |
| 模型供应商 | 百炼（阿里云）、SiliconFlow、Ollama（本地）、vLLM（后续扩展） |
| 认证鉴权   | Sa-Token                                                     |

## 关键设计模式

Ragent 应用了多种经典设计模式解决实际工程问题：

| 设计模式   | 应用场景                                      | 解决的问题                               |
| ---------- | --------------------------------------------- | ---------------------------------------- |
| 策略模式   | SearchChannel、PostProcessor、MCPToolExecutor | 检索通道、后处理器、MCP 工具可插拔替换   |
| 工厂模式   | IntentTreeFactory、StreamCallbackFactory      | 复杂对象的创建逻辑集中管理               |
| 注册表模式 | MCPToolRegistry、IntentNodeRegistry           | 组件自动发现与注册，新增工具零配置       |
| 模板方法   | IngestionNode 基类                            | 入库节点统一执行流程，子类只关注核心逻辑 |
| 装饰器模式 | ProbeBufferingCallback                        | 在不修改原有回调的前提下增加首包探测能力 |
| 责任链模式 | 后处理器链、模型降级链                        | 多个处理步骤按顺序串联，灵活组合         |
| 观察者模式 | StreamCallback                                | 流式事件的异步通知                       |
| AOP        | @RagTraceNode、@ChatRateLimit                 | 链路追踪和限流逻辑与业务代码解耦         |

## 主要功能模块

### 1. 多路检索引擎
- 意图定向检索 + 全局向量检索并行执行
- 结果去重、重排序后处理
- 兼顾精准度与召回率

### 2. 意图识别与引导
- 树形多级意图分类（领域→类目→话题）
- 置信度不足时主动引导澄清

### 3. 问题重写与拆分
- 多轮对话自动补全上下文
- 复杂问题拆分为子问题分别检索

### 4. 会话记忆管理
- 保留近 N 轮对话
- 超限自动摘要压缩，控制 Token 成本

### 5. 模型路由与容错
- 多模型优先级调度、首包探测、健康检查
- 自动降级机制，单模型故障不影响服务

### 6. MCP 工具集成
- 意图非知识检索时自动提参调用业务工具
- 检索与工具调用无缝融合

### 7. 文档入库 ETL
- 节点编排 Pipeline，支持条件执行和输出链式传递
- 从抓取、解析、增强、分块、向量化到写入 Milvus
- 每个任务和节点都有独立的执行日志

### 8. 全链路追踪
- 重写、意图、检索、生成每个环节均有 Trace 记录
- 支持问题定位与调优

## 生产级特性

- **限流**：支持全局并发限制和用户级限流
- **熔断**：模型健康检查 + 失败计数，自动熔断不可用的模型
- **可观测性**：基于 AOP 的全链路 Trace
- **流式输出**：SSE 实时推送，首包探测机制保证模型切换时用户无感知
- **会话管理**：记忆压缩、摘要持久化、TTL 过期
- **认证鉴权**：基于 Sa-Token 的用户认证体系

## 开发注意事项

1. 代码格式化：使用 Spotless 插件自动格式化 Java 代码，前端使用 Prettier 格式化
2. 日志系统：使用 SLF4J + Logback，日志配置在 `resources/logback-spring.xml`
3. 配置文件：Spring Boot 配置文件在各模块的 `src/main/resources` 目录下
4. 数据库：使用 MySQL，SQL 初始化脚本在 `resources/sql` 目录下
5. 向量数据库：使用 Milvus，需要提前安装并配置连接信息

## 语言规范

### 通用语言要求

- **所有交流、思考、任务规划必须使用简体中文**
- **代码注释、文档、接口描述必须使用简体中文**
- **日志信息、错误提示、操作日志必须使用简体中文**
- **数据库字段注释、表注释必须使用简体中文**

### 代码注释规范

```java
// 单行注释：使用简体中文，清晰说明代码用途
/*
 * 多行注释：使用简体中文
 * 详细描述代码块的功能和实现逻辑
 */
/**
 * Javadoc注释：使用简体中文
 * @param 参数名  参数描述（简体中文）
 * @return 返回值描述（简体中文）
 * @throws 异常类型 异常描述（简体中文）
 */
```

### 文档规范

- 所有程序设计文档（.md文件）必须使用简体中文
- 数据库设计文档必须使用简体中文
- API接口文档（Swagger）必须使用简体中文

### 操作日志规范

- 操作日志的字段描述必须使用简体中文
- 变更前后的值转换必须使用简体中文描述
- 操作类型、操作结果必须使用简体中文