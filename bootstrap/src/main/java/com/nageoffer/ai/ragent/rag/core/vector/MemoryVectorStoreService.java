/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nageoffer.ai.ragent.rag.core.vector;

import com.nageoffer.ai.ragent.core.chunk.VectorChunk;
import com.nageoffer.ai.ragent.rag.config.RAGDefaultProperties;
import com.nageoffer.ai.ragent.framework.exception.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存向量存储服务实现
 * <p>
 * 用于在内存中存储和管理向量数据，无需依赖外部向量数据库
 * 适用于快速体验和开发测试场景
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rag.vector.type", havingValue = "memory", matchIfMissing = true)
public class MemoryVectorStoreService implements VectorStoreService {

    private final RAGDefaultProperties ragDefaultProperties;

    /**
     * 内存存储结构：
     * - 第一级 key: collectionName（知识库名称）
     * - 第二级 key: chunkId（文档分块唯一标识）
     * - Value: 包含向量和元数据的对象
     */
    private final Map<String, Map<String, VectorChunkWithEmbedding>> collectionMap = new ConcurrentHashMap<>();

    @Override
    public void indexDocumentChunks(String collectionName, String docId, List<VectorChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            throw new ClientException("文档分块不允许为空");
        }

        // 确保集合存在
        collectionMap.computeIfAbsent(collectionName, k -> new ConcurrentHashMap<>());

        // 存储文档分块
        Map<String, VectorChunkWithEmbedding> chunkMap = collectionMap.get(collectionName);
        for (VectorChunk chunk : chunks) {
            chunkMap.put(chunk.getChunkId(), new VectorChunkWithEmbedding(chunk));
        }

        log.info("内存向量存储：成功索引文档分块，collection={}, docId={}, chunksCount={}",
                collectionName, docId, chunks.size());
    }

    @Override
    public void updateChunk(String collectionName, String docId, VectorChunk chunk) {
        if (chunk == null) {
            throw new ClientException("Chunk 对象不能为空");
        }

        // 确保集合存在
        collectionMap.computeIfAbsent(collectionName, k -> new ConcurrentHashMap<>());

        // 更新分块
        Map<String, VectorChunkWithEmbedding> chunkMap = collectionMap.get(collectionName);
        chunkMap.put(chunk.getChunkId(), new VectorChunkWithEmbedding(chunk));

        log.info("内存向量存储：成功更新文档分块，collection={}, docId={}, chunkId={}",
                collectionName, docId, chunk.getChunkId());
    }

    @Override
    public void deleteDocumentVectors(String collectionName, String docId) {
        Map<String, VectorChunkWithEmbedding> chunkMap = collectionMap.get(collectionName);
        if (chunkMap == null || chunkMap.isEmpty()) {
            log.info("内存向量存储：集合不存在或为空，collection={}", collectionName);
            return;
        }

        // 删除该文档的所有分块
        Iterator<Map.Entry<String, VectorChunkWithEmbedding>> iterator = chunkMap.entrySet().iterator();
        int deleteCount = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, VectorChunkWithEmbedding> entry = iterator.next();
            if (docId.equals(entry.getValue().getVectorChunk().getDocId())) {
                iterator.remove();
                deleteCount++;
            }
        }

        log.info("内存向量存储：成功删除文档的所有分块，collection={}, docId={}, deleteCount={}",
                collectionName, docId, deleteCount);
    }

    @Override
    public void deleteChunkById(String collectionName, String chunkId) {
        Map<String, VectorChunkWithEmbedding> chunkMap = collectionMap.get(collectionName);
        if (chunkMap == null) {
            log.info("内存向量存储：集合不存在，collection={}", collectionName);
            return;
        }

        VectorChunkWithEmbedding removed = chunkMap.remove(chunkId);
        if (removed != null) {
            log.info("内存向量存储：成功删除分块，collection={}, chunkId={}",
                    collectionName, chunkId);
        } else {
            log.info("内存向量存储：分块不存在，collection={}, chunkId={}",
                    collectionName, chunkId);
        }
    }

    /**
     * 根据集合名称获取所有分块
     *
     * @param collectionName 集合名称
     * @return 分块列表
     */
    public Collection<VectorChunkWithEmbedding> getChunksByCollection(String collectionName) {
        Map<String, VectorChunkWithEmbedding> chunkMap = collectionMap.get(collectionName);
        if (chunkMap == null) {
            return Collections.emptyList();
        }
        return chunkMap.values();
    }

    /**
     * 内部类：包含向量和元数据的对象
     */
    public static class VectorChunkWithEmbedding {
        private final VectorChunk vectorChunk;

        public VectorChunkWithEmbedding(VectorChunk vectorChunk) {
            this.vectorChunk = vectorChunk;
        }

        public VectorChunk getVectorChunk() {
            return vectorChunk;
        }

        public float[] getEmbedding() {
            return vectorChunk.getEmbedding();
        }

        public String getChunkId() {
            return vectorChunk.getChunkId();
        }

        public String getContent() {
            return vectorChunk.getContent();
        }

        public Map<String, Object> getMetadata() {
            return vectorChunk.getMetadata();
        }
    }
}
