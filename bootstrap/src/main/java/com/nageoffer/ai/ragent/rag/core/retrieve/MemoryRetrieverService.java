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

package com.nageoffer.ai.ragent.rag.core.retrieve;

import com.nageoffer.ai.ragent.rag.config.RAGDefaultProperties;
import com.nageoffer.ai.ragent.framework.convention.RetrievedChunk;
import com.nageoffer.ai.ragent.infra.embedding.EmbeddingService;
import com.nageoffer.ai.ragent.rag.core.vector.MemoryVectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 内存向量检索服务实现
 * <p>
 * 用于在内存中执行向量相似度搜索，无需依赖外部向量数据库
 * 适用于快速体验和开发测试场景
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rag.vector.type", havingValue = "memory", matchIfMissing = true)
public class MemoryRetrieverService implements RetrieverService {

    private final EmbeddingService embeddingService;
    private final MemoryVectorStoreService memoryVectorStoreService;
    private final RAGDefaultProperties ragDefaultProperties;

    @Override
    public List<RetrievedChunk> retrieve(RetrieveRequest retrieveParam) {
        List<Float> emb = embeddingService.embed(retrieveParam.getQuery());
        float[] vec = toArray(emb);

        // 归一化向量（用于余弦相似度计算）
        float[] normalizedVec = normalize(vec);

        return retrieveByVector(normalizedVec, retrieveParam);
    }

    @Override
    public List<RetrievedChunk> retrieveByVector(float[] vector, RetrieveRequest retrieveParam) {
        String collectionName = retrieveParam.getCollectionName();
        if (collectionName == null) {
            collectionName = ragDefaultProperties.getCollectionName();
        }

        // 获取集合中的所有分块
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> chunks =
                memoryVectorStoreService.getChunksByCollection(collectionName);

        if (chunks.isEmpty()) {
            log.info("内存向量检索：集合中没有分块，collection={}", collectionName);
            return List.of();
        }

        // 计算查询向量与所有分块向量的相似度
        List<ScoredChunk> scoredChunks = new ArrayList<>();
        for (MemoryVectorStoreService.VectorChunkWithEmbedding chunk : chunks) {
            float[] chunkEmbedding = chunk.getEmbedding();
            if (chunkEmbedding == null || chunkEmbedding.length == 0) {
                continue;
            }

            double score = calculateSimilarity(vector, chunkEmbedding);
            scoredChunks.add(new ScoredChunk(chunk, score));
        }

        // 按相似度降序排序
        scoredChunks.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // 取前 topK 个结果
        int topK = retrieveParam.getTopK();
        List<ScoredChunk> topChunks = scoredChunks.stream()
                .limit(topK)
                .collect(Collectors.toList());

        // 转换为 RetrievedChunk 类型
        return topChunks.stream()
                .map(scoredChunk -> new RetrievedChunk(
                        scoredChunk.getChunk().getChunkId(),
                        scoredChunk.getChunk().getContent(),
                        scoredChunk.getScore()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 计算向量相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数（值越大表示越相似）
     */
    private double calculateSimilarity(float[] vector1, float[] vector2) {
        String metricType = ragDefaultProperties.getMetricType();
        if (metricType == null) {
            metricType = "COSINE";
        }

        switch (metricType.toUpperCase()) {
            case "COSINE":
                return cosineSimilarity(vector1, vector2);
            case "L2":
                return l2Similarity(vector1, vector2);
            case "IP":
                return innerProduct(vector1, vector2);
            default:
                log.warn("未知的相似度度量类型：{}, 使用默认的余弦相似度", metricType);
                return cosineSimilarity(vector1, vector2);
        }
    }

    /**
     * 计算余弦相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度（范围：[-1, 1]）
     */
    private double cosineSimilarity(float[] vector1, float[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length && i < vector2.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 计算 L2 距离相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return L2 距离的相似度（值越大表示越相似）
     */
    private double l2Similarity(float[] vector1, float[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length && i < vector2.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        double distance = Math.sqrt(sum);
        // 将距离转换为相似度（值越大表示越相似）
        return 1.0 / (1.0 + distance);
    }

    /**
     * 计算内积相似度
     *
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 内积相似度
     */
    private double innerProduct(float[] vector1, float[] vector2) {
        double product = 0.0;
        for (int i = 0; i < vector1.length && i < vector2.length; i++) {
            product += vector1[i] * vector2[i];
        }
        return product;
    }

    /**
     * 归一化向量
     *
     * @param vector 原向量
     * @return 归一化后的向量
     */
    private float[] normalize(float[] vector) {
        double sum = 0.0;
        for (float x : vector) {
            sum += x * x;
        }
        double len = Math.sqrt(sum);

        float[] normalizedVec = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalizedVec[i] = (float) (vector[i] / len);
        }

        return normalizedVec;
    }

    /**
     * 将 List<Float> 转换为 float[]
     *
     * @param list 浮点数列表
     * @return 浮点数数组
     */
    private float[] toArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * 内部类：包含分数的分块
     */
    private static class ScoredChunk {
        private final MemoryVectorStoreService.VectorChunkWithEmbedding chunk;
        private final double score;

        public ScoredChunk(MemoryVectorStoreService.VectorChunkWithEmbedding chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }

        public MemoryVectorStoreService.VectorChunkWithEmbedding getChunk() {
            return chunk;
        }

        public double getScore() {
            return score;
        }
    }
}
