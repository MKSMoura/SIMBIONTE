package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemoryScoringEngineTest {

    private lateinit var engine: MemoryScoringEngineImpl

    @Before
    fun setup() {
        engine = MemoryScoringEngineImpl()
    }

    @Test
    fun `empty block scores baseline`() {
        val block = MemoryBlock(
            id = "1", conversaId = "c1", contextId = null, content = "oi",
            isFromUser = true
        )
        val score = engine.calculateScore(block, emptyList())
        assertTrue("Baseline should be >= 1.0f, got $score", score >= 1.0f)
    }

    @Test
    fun `long content gets bonus`() {
        val short = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "a".repeat(30), isFromUser = true)
        val long = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "a".repeat(150), isFromUser = true)
        val shortScore = engine.calculateScore(short, emptyList())
        val longScore = engine.calculateScore(long, emptyList())
        assertTrue("Long content should score higher", longScore > shortScore)
    }

    @Test
    fun `relations increase score`() {
        val block = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "texto medio para teste", isFromUser = true)
        val relations = listOf(
            MemoryRelation("1", "2", 0.8f, RelationType.CONTINUATION),
            MemoryRelation("1", "3", 0.5f, RelationType.ASSOCIATION)
        )
        val scoreWith = engine.calculateScore(block, relations)
        val scoreWithout = engine.calculateScore(block, emptyList())
        assertTrue("Relations should increase score", scoreWith > scoreWithout)
    }

    @Test
    fun `tags increase score`() {
        val withTags = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "texto", isFromUser = true, tags = "tag1,tag2,tag3")
        val withoutTags = MemoryBlock(id = "2", conversaId = "c1", contextId = null, content = "texto", isFromUser = true)
        val scoreTags = engine.calculateScore(withTags, emptyList())
        val scoreNoTags = engine.calculateScore(withoutTags, emptyList())
        assertTrue("Tags should increase score", scoreTags > scoreNoTags)
    }

    @Test
    fun `score caps at 10-0`() {
        val block = MemoryBlock(id = "1", conversaId = "c1", contextId = null, content = "a".repeat(250), isFromUser = true,
            tags = "a,b,c,d,e", cognitiveSignal = 5.0f)
        val relations = listOf(
            MemoryRelation("1", "2", 1.0f, RelationType.CONTINUATION),
            MemoryRelation("1", "3", 1.0f, RelationType.RETURN),
            MemoryRelation("1", "4", 1.0f, RelationType.ASSOCIATION)
        )
        val score = engine.calculateScore(block, relations)
        assertTrue("Score should be <= 10.0f, got $score", score <= 10.0f)
    }
}
