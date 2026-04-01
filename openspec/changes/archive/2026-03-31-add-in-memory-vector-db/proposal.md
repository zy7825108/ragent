## Why

Ragent 项目当前默认使用 Milvus 作为向量数据库，但 Milvus 的安装和配置对快速体验项目带来了一定的门槛。为了降低用户的上手难度，让项目可以在无外部依赖的情况下快速启动和体验核心功能，需要添加内存向量数据库实现作为默认选项。

## What Changes

1. 添加内存向量数据库实现，替换 Milvus 作为默认向量存储方案
2. 新增配置选项 `rag.vector.type`，支持配置使用 milvus、pg 或 memory 向量存储
3. 实现内存版的 VectorStoreService 和 RetrieverService
4. 确保选择内存实现时，项目可以快速启动并保留基本功能（如文档入库、向量检索、智能问答）
5. 优化项目启动流程，降低体验门槛

## Capabilities

### New Capabilities
- `memory-vector-db`: 内存向量数据库实现，提供无外部依赖的向量存储和检索功能

### Modified Capabilities
- `vector-store`: 扩展向量存储服务接口的实现方式，新增内存实现
- `vector-retrieval`: 扩展向量检索服务接口的实现方式，新增内存实现

## Impact

- **后端代码**:
  - `bootstrap/src/main/java/com/nageoffer/ai/ragent/rag/core/vector/`: 新增 MemoryVectorStoreService
  - `bootstrap/src/main/java/com/nageoffer/ai/ragent/rag/core/retrieve/`: 新增 MemoryRetrieverService
  - `bootstrap/src/main/java/com/nageoffer/ai/ragent/rag/config/`: 修改配置类，支持向量存储类型选择
- **配置文件**:
  - `bootstrap/src/main/resources/application.yml`: 新增配置选项，默认值为 memory
- **依赖**: 无需新增外部依赖，使用 Java 内置数据结构实现内存存储

## Benefits

1. **快速体验**: 项目可以在无外部依赖的情况下快速启动
2. **简化配置**: 无需安装和配置 Milvus，降低部署难度
3. **保留功能**: 内存实现保留了项目的基本功能，如文档入库、向量检索、智能问答
4. **易于切换**: 用户可以根据需要配置使用 Milvus、PGVector 或内存实现
