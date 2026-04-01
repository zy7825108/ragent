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

import com.nageoffer.ai.ragent.core.chunk.VectorChunk;
import com.nageoffer.ai.ragent.framework.convention.RetrievedChunk;
import com.nageoffer.ai.ragent.infra.embedding.EmbeddingService;
import com.nageoffer.ai.ragent.rag.config.RAGDefaultProperties;
import com.nageoffer.ai.ragent.rag.core.vector.MemoryVectorStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemoryRetrieverServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private RAGDefaultProperties ragDefaultProperties;

    private MemoryVectorStoreService memoryVectorStoreService;

    private MemoryRetrieverService memoryRetrieverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memoryVectorStoreService = new MemoryVectorStoreService(ragDefaultProperties);
        memoryRetrieverService = new MemoryRetrieverService(
                embeddingService,
                memoryVectorStoreService,
                ragDefaultProperties
        );
    }

    @Test
    void testRetrieveByQuery() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        String query = "query about test content";

        // Setup mock embedding
        when(embeddingService.embed(query)).thenReturn(List.of(0.1f, 0.2f, 0.3f));

        // Setup default properties
        when(ragDefaultProperties.getCollectionName()).thenReturn(collectionName);
        when(ragDefaultProperties.getMetricType()).thenReturn("COSINE");

        // Add test chunks
        List<VectorChunk> chunks = createTestChunks(docId);
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When
        List<RetrievedChunk> results = memoryRetrieverService.retrieve(new RetrieveRequest(query, 2));

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 2);
    }

    @Test
    void testRetrieveByVector() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        float[] queryVector = {0.1f, 0.2f, 0.3f};

        // Setup default properties
        when(ragDefaultProperties.getCollectionName()).thenReturn(collectionName);
        when(ragDefaultProperties.getMetricType()).thenReturn("COSINE");

        // Add test chunks
        List<VectorChunk> chunks = createTestChunks(docId);
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When
        List<RetrievedChunk> results = memoryRetrieverService.retrieveByVector(
                queryVector,
                new RetrieveRequest("test query", 2)
        );

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 2);
    }

    @Test
    void testRetrieveFromEmptyCollection() {
        // Given
        String query = "test query";
        when(embeddingService.embed(query)).thenReturn(List.of(0.1f, 0.2f, 0.3f));
        when(ragDefaultProperties.getCollectionName()).thenReturn("empty_collection");
        when(ragDefaultProperties.getMetricType()).thenReturn("COSINE");

        // When
        List<RetrievedChunk> results = memoryRetrieverService.retrieve(new RetrieveRequest(query, 2));

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testRetrieveWithL2Distance() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        float[] queryVector = {0.1f, 0.2f, 0.3f};

        // Setup default properties
        when(ragDefaultProperties.getCollectionName()).thenReturn(collectionName);
        when(ragDefaultProperties.getMetricType()).thenReturn("L2");

        // Add test chunks
        List<VectorChunk> chunks = createTestChunks(docId);
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When
        List<RetrievedChunk> results = memoryRetrieverService.retrieveByVector(
                queryVector,
                new RetrieveRequest("test query", 2)
        );

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testRetrieveWithTopK() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        String query = "test query";

        when(embeddingService.embed(query)).thenReturn(List.of(0.1f, 0.2f, 0.3f));
        when(ragDefaultProperties.getCollectionName()).thenReturn(collectionName);
        when(ragDefaultProperties.getMetricType()).thenReturn("COSINE");

        // Add 3 test chunks
        List<VectorChunk> chunks = createTestChunks(docId);
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When - Request top 2
        List<RetrievedChunk> results = memoryRetrieverService.retrieve(new RetrieveRequest(query, 2));

        // Then
        assertEquals(2, results.size());
    }

    @Test
    void testRetrieveWithIPMetric() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        String query = "test query";

        when(embeddingService.embed(query)).thenReturn(List.of(0.1f, 0.2f, 0.3f));
        when(ragDefaultProperties.getCollectionName()).thenReturn(collectionName);
        when(ragDefaultProperties.getMetricType()).thenReturn("IP");

        // Add test chunks
        List<VectorChunk> chunks = createTestChunks(docId);
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When
        List<RetrievedChunk> results = memoryRetrieverService.retrieve(new RetrieveRequest(query, 2));

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    private List<VectorChunk> createTestChunks(String docId) {
        List<VectorChunk> chunks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("doc_id", docId);
            metadata.put("chunk_index", i);
            VectorChunk chunk = new VectorChunk(
                    "chunk_" + i,
                    "Test content " + i,
                    i,
                    metadata,
                    new float[]{0.1f + i * 0.1f, 0.2f + i * 0.1f, 0.3f + i * 0.1f}
            );
            chunks.add(chunk);
        }
        return chunks;
    }
}
