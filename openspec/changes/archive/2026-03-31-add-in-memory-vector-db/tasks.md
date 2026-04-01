## 1. 核心实现

- [x] 1.1 创建 MemoryVectorStoreService 类，实现 VectorStoreService 接口
- [x] 1.2 创建 MemoryRetrieverService 类，实现 RetrieverService 接口
- [x] 1.3 实现向量相似度计算方法（余弦相似度和 L2 距离）

## 2. 配置管理

- [x] 2.1 修改 RAGDefaultProperties 类，添加向量存储类型配置属性
- [x] 2.2 创建内存向量存储的配置类 (RAGVectorProperties)
- [x] 2.3 修改 application.yml 配置文件，默认使用内存向量存储

## 3. 条件装配

- [x] 3.1 为 MemoryVectorStoreService 添加 @ConditionalOnProperty 注解
- [x] 3.2 为 MemoryRetrieverService 添加 @ConditionalOnProperty 注解
- [x] 3.3 确保 Milvus 和 PGVector 实现的条件注解配置正确

## 4. 测试与验证

- [x] 4.1 为 MemoryVectorStoreService 编写单元测试 (MemoryVectorStoreServiceTest.java)
- [x] 4.2 为 MemoryRetrieverService 编写单元测试 (MemoryRetrieverServiceTest.java)
