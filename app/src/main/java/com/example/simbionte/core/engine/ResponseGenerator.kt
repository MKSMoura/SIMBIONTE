package com.example.simbionte.core.engine

import com.example.simbionte.core.association.AssociationCandidate
import com.example.simbionte.core.flow.FlowState
import com.example.simbionte.core.profile.AdaptiveResponseEngine
import com.example.simbionte.core.profile.InputPattern
import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation
import com.example.simbionte.db.UserProfile
import kotlin.random.Random

class ResponseGenerator(
    private val wordSimilarity: WordSimilarity = WordSimilarity(),
    private val centralityEngine: CentralityEngine = CentralityEngine(),
    private val adaptiveEngine: AdaptiveResponseEngine = AdaptiveResponseEngine(),
    private val templateEngine: TemplateEngine = TemplateEngine()
) {

    data class GenContext(
        val userText: String,
        val window: TemporalWindow,
        val relations: List<MemoryRelation>,
        val liveState: LiveContextState,
        val currentBlock: MemoryBlock,
        val contextId: String,
        val recentMemories: List<MemoryBlock>,
        val allMemories: List<MemoryBlock>,
        val centralityMap: Map<String, CentralityEngine.CentralityResult>?,
        val flowState: FlowState? = null,
        val associations: List<AssociationCandidate> = emptyList(),
        val selectedAssociation: AssociationCandidate? = null,
        val userProfile: UserProfile = UserProfile(),
        val pattern: InputPattern = InputPattern(),
        val relationType: RelationType? = null
    )

    suspend fun generate(ctx: GenContext): String {
        val body = templateEngine.assemble(ctx, ctx.userText.hashCode())

        val recall = buildRecallSnippet(ctx)
        val recognition = buildCentralityRecognition(ctx)
        val withRecall = if (recall != null) recall else ""
        val full = if (recognition != null) "$recognition $withRecall $body" else "$withRecall $body"

        val hasModifier = ctx.allMemories.size > 5 && Random.nextInt(10) < 4
        return if (hasModifier && body.length > 15) {
            val modifier = organicModifier(ctx.userProfile.sentimentScore, ctx.userText.hashCode())
            "$modifier $full"
        } else full.trim()
    }

    private fun organicModifier(sentiment: Float, seed: Int): String {
        val rng = Random(seed + System.currentTimeMillis().toInt())
        val modifiers = listOf(
            "Sabe,", "Pensando bem…", "Deixe-me ver…", "Hmm…",
            "É interessante…", "Sabe o que pensei?", "Pra ser sincero…",
            "Olha…", "Vamos ver…", "Deixa eu pensar…"
        )
        return modifiers[rng.nextInt(modifiers.size)]
    }

    fun analyzePattern(ctx: GenContext): InputPattern {
        val text = ctx.userText.trim()

        val isQuestion = text.contains("?") || listOf(
            "o que", "como", "por que", "porque", "quando", "onde", "quem",
            "qual", "quais", "será", "sera", "vc acha", "você acha", "pode"
        ).any { text.lowercase().startsWith(it) }

        val isGreeting = listOf("olá", "ola", "oi", "oie", "bom dia", "boa tarde", "boa noite",
            "fala", "e aí", "e ai", "hey", "iae", "beleza").any { text.lowercase().startsWith(it) }

        val isShort = text.length < 25
        val isLong = text.length > 200
        val isReflection = text.length > 120

        val newTags = wordSimilarity.extractTags(text)
        val recentSimilar = ctx.recentMemories.dropLast(1).count { mem ->
            val memTags = wordSimilarity.extractTags(mem.content)
            newTags.isNotEmpty() && memTags.isNotEmpty() &&
                wordSimilarity.tagOverlap(newTags, memTags) > 0.2
        }
        val isTopicRevisit = recentSimilar >= 2

        val affirmationWords = listOf("sim", "exato", "isso", "verdade", "correto", "concordo", "ok", "beleza", "entendi")
        val denialWords = listOf("não", "nao", "nada", "nunca", "discordo", "errado")

        val words = text.lowercase().split(Regex("[\\s,;:.!?]+"))
        val hasAffirmation = affirmationWords.any { it in words }
        val hasDenial = denialWords.any { it in words }

        return InputPattern(
            isQuestion = isQuestion,
            isGreeting = isGreeting,
            isShort = isShort,
            isLong = isLong,
            isReflection = isReflection,
            isTopicRevisit = isTopicRevisit,
            hasAffirmation = hasAffirmation,
            hasDenial = hasDenial
        )
    }

    private fun buildCentralityRecognition(ctx: GenContext): String? {
        if (ctx.allMemories.size < 10) return null
        val blockId = ctx.currentBlock.id
        val centrality = ctx.centralityMap?.get(blockId)
        val highCentrality = centrality != null && centrality.degree >= 3
        val highSignal = ctx.currentBlock.cognitiveSignal > 2.0f
        if (!highCentrality && !highSignal) return null

        val fragments = FragmentCatalog.aberturas["centrality"] ?: return null
        val idx = (blockId.hashCode() and Int.MAX_VALUE) % fragments.size
        return fragments[idx].text + "…"
    }

    private fun buildRecallSnippet(ctx: GenContext): String? {
        val assoc = ctx.selectedAssociation ?: return null
        if (assoc.similarity < 0.25) return null
        val content = assoc.content.take(50).replace(Regex("""\s+\S*$"""), "")

        val rng = Random(assoc.memoryId.hashCode() + ctx.userText.hashCode())
        val connectors = FragmentCatalog.conectores["recall"] ?: return null
        val connector = connectors[rng.nextInt(connectors.size)].text
        val tails = FragmentCatalog.conectores["recall_tail"] ?: return null
        val tail = tails[rng.nextInt(tails.size)].text
            .replace("\$snippet", content)

        return if (rng.nextBoolean()) {
            "$connector $tail"
        } else {
            "$content… $connector $tail"
        }
    }
}
