## Context

Ragent 项目当前默认使用 Milvus 作为向量数据库，但 Milvus 的安装和配置对快速体验项目带来了一定的门槛。为了降低用户的上手难度，让项目可以在无外部依赖的情况下快速启动和体验核心功能，需要添加内存向量数据库实现作为默认选项。

项目已有的向量存储和检索架构如下：
- `VectorStoreService` 接口定义了向量数据的增删改查操作
- `RetrieverService` 接口定义了向量检索操作
- 已有 Milvus 和 PGVector 两种实现
- 使用 `@ConditionalOnProperty` 注解根据配置选择实现

## Goals / Non-Goals

**Goals:**
- 新增内存向量数据库实现，替换 Milvus 作为默认选项
- 确保选择内存实现时，项目可以快速启动并保留基本功能
- 提供配置选项，允许用户选择使用 Milvus、PGVector 或内存实现
- 内存实现应支持基本的向量存储和检索功能

**Non-Goals:**
- 内存实现不考虑数据持久化（重启后数据会丢失）
- 不实现复杂的向量索引优化（如 IVF_FLAT、HNSW 等）
- 不提供内存容量管理或数据过期机制

## Decisions

### 1. 架构设计

**决定**: 遵循项目现有的架构模式，创建两个新的服务实现类

- `MemoryVectorStoreService`: 实现 `VectorStoreService` 接口，负责向量数据的增删改查
- `MemoryRetrieverService`: 实现 `RetrieverService` 接口，负责向量检索

**理由**: 这样可以保持与现有代码的一致性，易于维护和扩展

### 2. 数据存储结构

**决定**: 使用 Java 内置的 `ConcurrentHashMap` 作为内存存储容器

```java
// 存储结构设计
private final Map<String, Map<String, VectorChunkWithEmbedding>> collectionMap = new ConcurrentHashMap<>();
```

- 第一级 key: collectionName（知识库名称）
- 第二级 key: chunkId（文档分块唯一标识）
- Value: 包含向量和元数据的对象

**理由**:
- ConcurrentHashMap 提供了线程安全的操作
- 结构简单，易于理解和实现
- 支持快速的插入、查询和删除操作

### 3. 相似度计算

**决定**: 支持余弦相似度和 L2 距离计算

**理由**:
- 余弦相似度和 L2 距离都是文本相似度计算的常用方法
- 与 Milvus 支持的度量类型保持一致
- 满足不同场景的需求

### 4. 配置选项

**决定**: 修改配置类，新增 `rag.vector.type` 配置项，默认为 `memory`，同时支持配置相似度度量类型

```yaml
rag:
  vector:
    type: memory  # 可选值: memory, milvus, pg
  default:
    metric-type: COSINE  # 可选值: COSINE, L2, IP

**理由**:
- 遵循项目现有的配置模式
- 提供清晰的配置选项
- 默认使用内存实现，降低体验门槛

### 5. 条件注解

**决定**: 使用 `@ConditionalOnProperty` 注解根据配置选择实现

```java
@ConditionalOnProperty(name = "rag.vector.type", havingValue = "memory")
```

**理由**: 与项目现有代码保持一致，易于维护

## Risks / Trade-offs

### 风险 1: 数据持久化

**问题**: 内存实现不支持数据持久化，重启后数据会丢失

**缓解**: 明确说明内存实现的用途是快速体验，生产环境应使用 Milvus 或 PGVector

### 风险 2: 内存容量限制

**问题**: 大量数据入库可能导致内存不足

**缓解**:
- 内存实现主要用于快速体验，数据量不大
- 可以考虑添加内存使用监控和警告

### 风险 3: 检索性能

**问题**: 内存实现使用线性扫描，检索性能不如 Milvus 的索引优化

**缓解**:
- 对于小数据集，线性扫描性能足够
- 内存实现主要用于快速体验，数据量不大

## Migration Plan

1. **引入变更**: 新增内存实现类，不修改现有代码
2. **默认配置**: 将默认向量存储类型改为 memory
3. **文档更新**: 更新 README.md 和配置说明文档
4. **测试**: 确保内存实现的功能正常

## Open Questions

1. **是否需要支持其他相似度度量方法**（如 L2 距离、内积）？
   - 目前只支持余弦相似度，满足基本需求
2. **是否需要添加内存使用监控**？
   - 可以考虑在后续版本添加
3. **是否需要支持数据导出功能**？
   - 内存实现主要用于快速体验，导出功能需求不大
