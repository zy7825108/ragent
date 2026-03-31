# Ragent 内存向量库支持

## What This Is

Ragent 是一个企业级 RAG（检索增强生成）智能体平台，基于 Java 17 + Spring Boot 3 + React 18 构建。本次项目旨在为 Ragent 添加内存向量库支持，使得项目能够在不依赖 Milvus 向量数据库的情况下快速启动和体验核心功能。

## Core Value

**无需依赖外部向量数据库即可快速体验 Ragent 的完整功能** — 让开发者能够在本地环境快速启动项目，进行开发、测试和演示，降低入门门槛。

## Requirements

### Validated

<!-- Shipped and confirmed valuable. -->

(None yet — ship to validate)

### Active

<!-- Current scope. Building toward these. -->

- [ ] 分析 Milvus 在项目中的使用场景和功能
- [ ] 设计内存向量库实现方案，替换 Milvus 的核心功能
- [ ] 提供配置选项，允许用户选择使用内存向量库或 Milvus
- [ ] 默认配置为内存向量库，确保项目快速启动
- [ ] 保留项目的基本功能（文档入库、智能问答、多路检索等）
- [ ] 实现内存向量库的核心操作：插入、查询、删除
- [ ] 确保与现有代码架构的兼容性（策略模式、工厂模式等）

### Out of Scope

<!-- Explicit boundaries. Includes reasoning to prevent re-adding. -->

- 内存向量库的持久化存储 - 本次不实现数据持久化，重启后数据会丢失
- 内存向量库的分布式支持 - 本次仅支持单机内存存储
- Milvus 功能的完全复刻 - 仅实现核心的向量检索功能

## Context

### 技术背景

当前 Ragent 项目严重依赖 Milvus 向量数据库作为文档检索的核心组件。这给项目的快速启动和体验带来了以下挑战：

1. **部署复杂度高**：需要单独部署 Milvus 服务
2. **资源消耗大**：Milvus 服务需要较多的内存和 CPU 资源
3. **启动时间长**：Milvus 服务启动需要较长时间
4. **学习成本高**：开发者需要了解 Milvus 的配置和使用

### 解决方案价值

内存向量库支持将带来以下优势：

1. **零依赖启动**：无需安装和配置外部向量数据库
2. **快速启动**：项目启动时间从几分钟缩短到几秒钟
3. **轻量级体验**：适合开发、测试和演示场景
4. **降低门槛**：新开发者可以快速上手项目

## Constraints

- **技术选型**：内存向量库使用纯 Java 实现，不引入额外的第三方依赖
- **性能考虑**：内存向量库主要用于开发和测试场景，对大规模数据的性能不做要求
- **兼容性**：必须与现有代码架构兼容，使用策略模式实现可插拔替换
- **默认配置**：项目默认使用内存向量库，确保开箱即用

## Key Decisions

<!-- Decisions that constrain future work. Add throughout project lifecycle. -->

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| 使用纯 Java 实现内存向量库 | 避免引入额外依赖，确保轻量级 | — Pending |
| 采用策略模式实现可插拔 | 与项目现有架构保持一致 | — Pending |
| 默认使用内存向量库 | 降低入门门槛，快速体验 | — Pending |
| 内存存储不持久化 | 简化实现，专注于快速体验 | — Pending |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd:transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd:complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-03-31 after initialization*
