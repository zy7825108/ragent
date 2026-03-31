# Architecture

**Analysis Date:** 2026-03-31

## Pattern Overview

**Overall:** 企业级 RAG（检索增强生成）智能体平台架构，采用分层模块化设计

**Key Characteristics:**
- 后端采用 Maven 多模块架构，分为业务逻辑层、通用能力层、AI 基础设施层和 MCP 服务器
- 前端采用 React 18 + TypeScript + Vite 构建，分为用户问答界面和管理后台
- 核心功能覆盖 RAG 全链路：文档入库、检索、意图识别、问答生成
- 应用多种经典设计模式：策略模式、工厂模式、注册表模式、模板方法等
- 生产级特性：限流、熔断、可观测性、流式输出、会话管理

## Layers

**Bootstrap（业务逻辑层）:**
- Purpose: 专注于具体业务实现，包括 RAG 核心流程、文档入库、会话管理等
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap`
- Contains: RAG 核心逻辑、文档入库 pipeline、会话管理、API 接口
- Depends on: framework、infra-ai、mcp-server
- Used by: 前端用户界面和管理后台

**Framework（通用能力层）:**
- Purpose: 提供与业务无关的通用功能
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/framework`
- Contains: 工具函数、异常处理、配置管理、通用服务
- Depends on: 无（独立模块）
- Used by: bootstrap、infra-ai、mcp-server

**Infra-AI（AI 基础设施层）:**
- Purpose: 屏蔽不同模型供应商的差异，提供统一的 AI 服务接口
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/infra-ai`
- Contains: 模型调用、向量处理、工具集成、MCP 相关功能
- Depends on: framework
- Used by: bootstrap、mcp-server

**MCP-Server（MCP 服务器）:**
- Purpose: 处理模型上下文协议（Model Context Protocol）的工具调用
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/mcp-server`
- Contains: MCP 工具注册、执行和管理
- Depends on: framework、infra-ai
- Used by: bootstrap

**Frontend（前端层）:**
- Purpose: 提供用户问答界面和管理后台
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend`
- Contains: React 组件、API 调用、状态管理、UI 界面
- Depends on: 后端 API
- Used by: 终端用户和管理员

## Data Flow

**RAG 问答流程:**
1. 用户通过前端界面输入问题
2. 后端接收请求，进行问题重写和意图识别
3. 基于意图进行多路检索（向量检索 + 意图定向检索）
4. 检索结果去重、重排序后处理
5. 生成 prompt 并调用模型
6. 模型返回结果，前端流式展示

**文档入库流程:**
1. 创建入库任务和 pipeline
2. 执行入库节点（抓取 → 解析 → 增强 → 分块 → 向量化 → 写入）
3. 每个节点有独立执行日志和错误处理
4. 任务完成后更新知识库状态

**State Management:**
- 后端：Spring Boot 状态管理、会话管理
- 前端：React 状态管理（可能使用 Context API 或 Redux）

## Key Abstractions

**RAG 核心抽象:**
- Purpose: 封装 RAG 系统的核心概念和流程
- Examples:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/rag` - RAG 核心逻辑
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/ingestion` - 文档入库 pipeline

**意图识别抽象:**
- Purpose: 处理用户意图分类和识别
- Examples:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/test/java/com/nageoffer/ai/ragent/rag/Intent` - 意图识别测试

**向量处理抽象:**
- Purpose: 处理向量数据的存储和检索
- Examples:
  - `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/vector` - 向量数据库操作

## Entry Points

**后端入口:**
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/RagentCoreApplication.java`
- Triggers: Spring Boot 启动
- Responsibilities: 初始化 Spring 容器、配置组件扫描、启动应用服务

**API 入口:**
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/bootstrap/src/main/java/com/nageoffer/ai/ragent/controller`
- Triggers: HTTP 请求
- Responsibilities: 处理用户请求、返回响应数据

**前端入口:**
- Location: `/Users/zhongyang/Desktop/个人工作/study/ragent/frontend/src/main.tsx`
- Triggers: 浏览器访问
- Responsibilities: 渲染应用界面、处理用户交互

## Error Handling

**Strategy:** 分层错误处理 + 全局异常捕获

**Patterns:**
- 自定义异常类继承 RuntimeException
- @RestControllerAdvice 全局异常处理
- 详细的错误信息和错误码
- 操作日志记录

## Cross-Cutting Concerns

**Logging:** SLF4J + Logback，日志配置在各模块的 `src/main/resources/logback-spring.xml`

**Validation:** 使用 Spring Validation 进行参数校验

**Authentication:** Sa-Token 认证体系，支持用户级权限控制

**Observability:** 基于 AOP 的全链路 Trace，使用 @RagTraceNode 注解

**Rate Limiting:** 基于 Sa-Token 和 Redisson 的限流机制，支持全局和用户级限流

---

*Architecture analysis: 2026-03-31*