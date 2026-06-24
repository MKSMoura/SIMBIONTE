package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RelationBuilderTest {

    private lateinit var builder: RelationBuilderImpl

    @Before
    fun setup() {
        builder = RelationBuilderImpl()
    }

    @Test
    fun `IMMEDIATE window creates CONTINUATION`() {
        val novo = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "segunda mensagem", isFromUser = true)
        val anterior = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "primeira mensagem", isFromUser = true)
        val relations = builder.buildRelations(novo, anterior, TemporalWindow.IMMEDIATE, emptyList(), emptyList())
        val hasContinuation = relations.any { it.relationshipType == RelationType.CONTINUATION }
        assertTrue("IMMEDIATE window should create CONTINUATION", hasContinuation)
    }

    @Test
    fun `RECENT window with low tag overlap creates SIDE_THOUGHT`() {
        val novo = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "xyz", isFromUser = true)
        val anterior = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "abc", isFromUser = true)
        val relations = builder.buildRelations(novo, anterior, TemporalWindow.RECENT, emptyList(), emptyList())
        val hasSideThought = relations.any { it.relationshipType == RelationType.SIDE_THOUGHT }
        assertTrue("RECENT window with no tag overlap should create SIDE_THOUGHT", hasSideThought)
    }

    @Test
    fun `DISTANT window with low tag overlap creates ASSOCIATION`() {
        val novo = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "xyz", isFromUser = true)
        val anterior = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "abc", isFromUser = true)
        val relations = builder.buildRelations(novo, anterior, TemporalWindow.DISTANT, emptyList(), emptyList())
        val hasAssociation = relations.any { it.relationshipType == RelationType.ASSOCIATION }
        assertTrue("DISTANT window with no tag overlap should create ASSOCIATION", hasAssociation)
    }

    @Test
    fun `contradiction detected with negation and moderate similarity`() {
        val novo = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "esse método não funciona para este caso", isFromUser = true)
        val anterior = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "esse método funciona bem na prática", isFromUser = true)
        val relations = builder.buildRelations(novo, anterior, TemporalWindow.IMMEDIATE, emptyList(), emptyList())
        val hasContradiction = relations.any { it.relationshipType == RelationType.CONTRADICTION }
        assertTrue("Contradiction should be detected with negation + moderate n-gram overlap", hasContradiction)
    }

    @Test
    fun `similar dominant memory creates RETURN`() {
        val novo = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "inteligencia artificial explicada", isFromUser = true, timestamp = 5_000_000L)
        val anterior = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "sobre tecnologia", isFromUser = true)
        val similar = MemoryBlock(id = "3", conversaId = "c1", contextId = "ctx_dominante", content = "inteligencia artificial e machine learning", isFromUser = true, timestamp = 100_000L)
        val relations = builder.buildRelations(novo, anterior, TemporalWindow.IMMEDIATE,
            listOf(similar), listOf("ctx_dominante"))
        val hasReturn = relations.any { it.relationshipType == RelationType.RETURN }
        assertTrue("Return should be detected for dominant context with similar content", hasReturn)
    }

    @Test
    fun `no last block returns no temporal relation`() {
        val novo = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "primeira mensagem", isFromUser = true)
        val relations = builder.buildRelations(novo, null, TemporalWindow.IMMEDIATE, emptyList(), emptyList())
        assertTrue("Should have no temporal relation without last block", relations.isEmpty())
    }
}
