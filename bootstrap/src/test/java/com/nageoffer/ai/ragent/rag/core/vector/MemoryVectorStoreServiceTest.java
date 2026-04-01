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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemoryVectorStoreServiceTest {

    @Mock
    private RAGDefaultProperties ragDefaultProperties;

    private MemoryVectorStoreService memoryVectorStoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memoryVectorStoreService = new MemoryVectorStoreService(ragDefaultProperties);
    }

    @Test
    void testIndexDocumentChunks() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        List<VectorChunk> chunks = createTestChunks();

        // When
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // Then - Verify chunks are stored
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> storedChunks =
                memoryVectorStoreService.getChunksByCollection(collectionName);
        assertEquals(chunks.size(), storedChunks.size());
    }

    @Test
    void testUpdateChunk() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        List<VectorChunk> chunks = createTestChunks();
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);

        // When - Update the first chunk
        VectorChunk chunkToUpdate = chunks.get(0);
        VectorChunk updatedChunk = new VectorChunk(
                chunkToUpdate.getChunkId(),
                "Updated content",
                chunkToUpdate.getIndex(),
                chunkToUpdate.getMetadata(),
                new float[]{0.1f, 0.2f, 0.3f} // Updated embedding
        );
        memoryVectorStoreService.updateChunk(collectionName, docId, updatedChunk);

        // Then
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> storedChunks =
                memoryVectorStoreService.getChunksByCollection(collectionName);
        Optional<MemoryVectorStoreService.VectorChunkWithEmbedding> foundChunk =
                storedChunks.stream()
                        .filter(c -> c.getChunkId().equals(updatedChunk.getChunkId()))
                        .findFirst();

        assertTrue(foundChunk.isPresent());
        assertEquals("Updated content", foundChunk.get().getContent());
        assertArrayEquals(new float[]{0.1f, 0.2f, 0.3f}, foundChunk.get().getEmbedding());
    }

    @Test
    void testDeleteDocumentVectors() {
        // Given
        String collectionName = "test_collection";
        String docId1 = "test_doc_1";
        String docId2 = "test_doc_2";
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId1, createTestChunks());
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId2, createTestChunks());

        // When
        memoryVectorStoreService.deleteDocumentVectors(collectionName, docId1);

        // Then
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> storedChunks =
                memoryVectorStoreService.getChunksByCollection(collectionName);
        // Check that no chunks from docId1 remain
        assertFalse(storedChunks.stream().anyMatch(c ->
                c.getMetadata().get("doc_id").equals(docId1)));
    }

    @Test
    void testDeleteChunkById() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";
        List<VectorChunk> chunks = createTestChunks();
        memoryVectorStoreService.indexDocumentChunks(collectionName, docId, chunks);
        String chunkIdToDelete = chunks.get(0).getChunkId();

        // When
        memoryVectorStoreService.deleteChunkById(collectionName, chunkIdToDelete);

        // Then
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> storedChunks =
                memoryVectorStoreService.getChunksByCollection(collectionName);
        assertFalse(storedChunks.stream().anyMatch(c ->
                c.getChunkId().equals(chunkIdToDelete)));
        // Verify other chunks are still present
        assertEquals(chunks.size() - 1, storedChunks.size());
    }

    @Test
    void testIndexDocumentChunksWithEmptyList() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";

        // When & Then
        assertThrows(Exception.class, () -> {
            memoryVectorStoreService.indexDocumentChunks(collectionName, docId, Collections.emptyList());
        });
    }

    @Test
    void testUpdateChunkWithNullChunk() {
        // Given
        String collectionName = "test_collection";
        String docId = "test_doc_1";

        // When & Then
        assertThrows(Exception.class, () -> {
            memoryVectorStoreService.updateChunk(collectionName, docId, null);
        });
    }

    @Test
    void testGetChunksByNonExistentCollection() {
        // Given
        String nonExistentCollection = "non_existent_collection";

        // When
        Collection<MemoryVectorStoreService.VectorChunkWithEmbedding> chunks =
                memoryVectorStoreService.getChunksByCollection(nonExistentCollection);

        // Then
        assertTrue(chunks.isEmpty());
    }

    private List<VectorChunk> createTestChunks() {
        List<VectorChunk> chunks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("doc_id", "test_doc_1");
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
