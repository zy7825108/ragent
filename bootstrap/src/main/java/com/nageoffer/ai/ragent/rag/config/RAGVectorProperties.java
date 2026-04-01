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

package com.nageoffer.ai.ragent.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RAG 向量存储配置类
 * <p>
 * 用于管理向量存储类型的配置，支持选择不同的向量数据库实现
 * </p>
 *
 * <pre>
 * 示例配置：
 *
 * rag:
 *   vector:
 *     type: memory  # 可选值: memory, milvus, pg
 * </pre>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rag.vector")
public class RAGVectorProperties {

    /**
     * 向量存储类型
     * <p>
     * 支持的向量存储类型：
     * <ul>
     *   <li>{@code memory}：内存向量存储（默认，无外部依赖）</li>
     *   <li>{@code milvus}：Milvus 向量数据库</li>
     *   <li>{@code pg}：PostgreSQL 向量存储（pgvector 扩展）</li>
     * </ul>
     * </p>
     */
    private String type = "memory";
}
