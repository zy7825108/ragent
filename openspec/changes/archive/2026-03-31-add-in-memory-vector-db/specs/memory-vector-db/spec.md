## ADDED Requirements

### Requirement: Memory vector store service
系统 SHALL 提供内存向量存储服务实现，用于存储和管理向量数据，无需依赖外部向量数据库。

#### Scenario: Store document chunks in memory
- **WHEN** 系统调用 indexDocumentChunks 方法，传入集合名称、文档ID和文档分块列表
- **THEN** 系统将文档分块的向量数据存储在内存中
- **AND** 数据按照集合名称和文档ID进行组织

#### Scenario: Update chunk in memory
- **WHEN** 系统调用 updateChunk 方法，传入集合名称、文档ID和更新后的文档分块
- **THEN** 系统更新内存中对应文档分块的向量数据

#### Scenario: Delete document vectors from memory
- **WHEN** 系统调用 deleteDocumentVectors 方法，传入集合名称和文档ID
- **THEN** 系统删除内存中该文档的所有分块向量数据

#### Scenario: Delete chunk by ID from memory
- **WHEN** 系统调用 deleteChunkById 方法，传入集合名称和分块ID
- **THEN** 系统删除内存中对应分块的向量数据

### Requirement: Memory vector retrieval service
系统 SHALL 提供内存向量检索服务实现，用于在内存中执行向量相似度搜索。

#### Scenario: Retrieve chunks by query
- **WHEN** 系统调用 retrieve 方法，传入查询字符串和topK参数
- **THEN** 系统使用EmbeddingService将查询字符串转换为向量
- **AND** 在内存中搜索与查询向量最相似的topK个文档分块
- **AND** 按照相似度降序返回检索结果

#### Scenario: Retrieve chunks by vector
- **WHEN** 系统调用 retrieveByVector 方法，传入查询向量和topK参数
- **THEN** 系统在内存中搜索与查询向量最相似的topK个文档分块
- **AND** 按照相似度降序返回检索结果

### Requirement: Vector similarity calculation
系统 SHALL 支持余弦相似度和L2距离两种相似度计算方法。

#### Scenario: Calculate cosine similarity
- **WHEN** 配置中设置 metric-type 为 COSINE
- **THEN** 系统使用余弦相似度计算向量之间的相似度
- **AND** 相似度范围为[-1, 1]，值越大表示越相似

#### Scenario: Calculate L2 distance
- **WHEN** 配置中设置 metric-type 为 L2
- **THEN** 系统使用L2距离计算向量之间的相似度
- **AND** 距离值越小表示越相似

### Requirement: Vector store configuration
系统 SHALL 支持通过配置选择使用内存向量存储，并提供默认配置。

#### Scenario: Default to memory vector store
- **WHEN** 配置文件中未显式配置 rag.vector.type
- **THEN** 系统默认使用内存向量存储实现

#### Scenario: Configure vector store type
- **WHEN** 配置文件中设置 rag.vector.type 为 memory/milvus/pg
- **THEN** 系统加载对应的向量存储实现

#### Scenario: Configure metric type
- **WHEN** 配置文件中设置 rag.default.metric-type 为 COSINE/L2/IP
- **THEN** 系统使用对应的相似度计算方法

### Requirement: Quick startup without external dependencies
系统 SHALL 在选择内存向量存储时，能够快速启动且不依赖外部基础设施。

#### Scenario: Fast startup with memory vector store
- **WHEN** 系统以默认配置（内存向量存储）启动
- **THEN** 系统在10秒内完成启动
- **AND** 无需连接外部向量数据库

#### Scenario: Basic functionality with memory vector store
- **WHEN** 系统使用内存向量存储
- **THEN** 系统支持文档入库、向量检索、智能问答等核心功能
