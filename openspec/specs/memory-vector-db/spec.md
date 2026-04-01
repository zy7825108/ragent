# 内存向量数据库 (Memory Vector Database)

## 用途

提供无外部依赖的内存向量存储和检索功能，使项目能够快速启动和体验核心功能。

## 设计决策

### 架构设计

遵循项目现有的架构模式，创建两个新的服务实现类：

- `MemoryVectorStoreService`: 实现 `VectorStoreService` 接口，负责向量数据的增删改查
- `MemoryRetrieverService`: 实现 `RetrieverService` 接口，负责向量检索

**理由**: 这样可以保持与现有代码的一致性，易于维护和扩展。

### 数据存储结构

使用 Java 内置的 `ConcurrentHashMap` 作为内存存储容器：

```java
private final Map<String, Map<String, VectorChunkWithEmbedding>> collectionMap = new ConcurrentHashMap<>();
```

- 第一级 key: collectionName（知识库名称）
- 第二级 key: chunkId（文档分块唯一标识）
- Value: 包含向量和元数据的对象

**理由**:
- ConcurrentHashMap 提供了线程安全的操作
- 结构简单，易于理解和实现
- 支持快速的插入、查询和删除操作

### 相似度计算

支持余弦相似度和 L2 距离计算：

**理由**:
- 余弦相似度和 L2 距离都是文本相似度计算的常用方法
- 与 Milvus 支持的度量类型保持一致
- 满足不同场景的需求

### 配置选项

修改配置类，新增 `rag.vector.type` 配置项，默认为 `memory`，同时支持配置相似度度量类型：

```yaml
rag:
  vector:
    type: memory  # 可选值: memory, milvus, pg
  default:
    metric-type: COSINE  # 可选值: COSINE, L2, IP
```

**理由**:
- 遵循项目现有的配置模式
- 提供清晰的配置选项
- 默认使用内存实现，降低体验门槛

### 条件注解

使用 `@ConditionalOnProperty` 注解根据配置选择实现：

```java
@ConditionalOnProperty(name = "rag.vector.type", havingValue = "memory", matchIfMissing = true)
```

**理由**: 与项目现有代码保持一致，易于维护。

## 风险与权衡

### 风险 1: 数据持久化

**问题**: 内存实现不支持数据持久化，重启后数据会丢失。

**缓解**: 明确说明内存实现的用途是快速体验，生产环境应使用 Milvus 或 PGVector。

### 风险 2: 内存容量限制

**问题**: 大量数据入库可能导致内存不足。

**缓解**:
- 内存实现主要用于快速体验，数据量不大
- 可以考虑添加内存使用监控和警告

### 风险 3: 检索性能

**问题**: 内存实现使用线性扫描，检索性能不如 Milvus 的索引优化。

**缓解**:
- 对于小数据集，线性扫描性能足够
- 内存实现主要用于快速体验，数据量不大

## 需求规格

### 内存向量存储服务

**需求 ID**: MEM-VS-001

系统 SHALL 提供内存向量存储服务实现，用于存储和管理向量数据，无需依赖外部向量数据库。

#### 场景

1. **存储文档分块到内存**
   - **WHEN** 系统调用 indexDocumentChunks 方法，传入集合名称、文档ID和文档分块列表
   - **THEN** 系统将文档分块的向量数据存储在内存中
   - **AND** 数据按照集合名称和文档ID进行组织

2. **更新内存中的分块**
   - **WHEN** 系统调用 updateChunk 方法，传入集合名称、文档ID和更新后的文档分块
   - **THEN** 系统更新内存中对应文档分块的向量数据

3. **删除文档的所有向量**
   - **WHEN** 系统调用 deleteDocumentVectors 方法，传入集合名称和文档ID
   - **THEN** 系统删除内存中该文档的所有分块向量数据

4. **通过分块ID删除向量**
   - **WHEN** 系统调用 deleteChunkById 方法，传入集合名称和分块ID
   - **THEN** 系统删除内存中对应分块的向量数据

### 内存向量检索服务

**需求 ID**: MEM-RS-001

系统 SHALL 提供内存向量检索服务实现，用于在内存中执行向量相似度搜索。

#### 场景

1. **按查询字符串检索**
   - **WHEN** 系统调用 retrieve 方法，传入查询字符串和topK参数
   - **THEN** 系统使用EmbeddingService将查询字符串转换为向量
   - **AND** 在内存中搜索与查询向量最相似的topK个文档分块
   - **AND** 按照相似度降序返回检索结果

2. **按向量直接检索**
   - **WHEN** 系统调用 retrieveByVector 方法，传入查询向量和topK参数
   - **THEN** 系统在内存中搜索与查询向量最相似的topK个文档分块
   - **AND** 按照相似度降序返回检索结果

### 向量相似度计算

**需求 ID**: MEM-SIM-001

系统 SHALL 支持余弦相似度和L2距离两种相似度计算方法。

#### 场景

1. **计算余弦相似度**
   - **WHEN** 配置中设置 metric-type 为 COSINE
   - **THEN** 系统使用余弦相似度计算向量之间的相似度
   - **AND** 相似度范围为[-1, 1]，值越大表示越相似

2. **计算 L2 距离**
   - **WHEN** 配置中设置 metric-type 为 L2
   - **THEN** 系统使用L2距离计算向量之间的相似度
   - **AND** 距离值越小表示越相似

### 向量存储配置

**需求 ID**: MEM-CFG-001

系统 SHALL 支持通过配置选择使用内存向量存储，并提供默认配置。

#### 场景

1. **默认使用内存向量存储**
   - **WHEN** 配置文件中未显式配置 rag.vector.type
   - **THEN** 系统默认使用内存向量存储实现

2. **配置向量存储类型**
   - **WHEN** 配置文件中设置 rag.vector.type 为 memory/milvus/pg
   - **THEN** 系统加载对应的向量存储实现

3. **配置相似度度量类型**
   - **WHEN** 配置文件中设置 rag.default.metric-type 为 COSINE/L2/IP
   - **THEN** 系统使用对应的相似度计算方法

### 无需外部依赖快速启动

**需求 ID**: MEM-QUICK-001

系统 SHALL 在选择内存向量存储时，能够快速启动且不依赖外部基础设施。

#### 场景

1. **快速启动**
   - **WHEN** 系统以默认配置（内存向量存储）启动
   - **THEN** 系统在10秒内完成启动
   - **AND** 无需连接外部向量数据库

2. **基本功能支持**
   - **WHEN** 系统使用内存向量存储
   - **THEN** 系统支持文档入库、向量检索、智能问答等核心功能
