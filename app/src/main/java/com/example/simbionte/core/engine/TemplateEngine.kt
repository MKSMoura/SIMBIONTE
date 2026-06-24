package com.example.simbionte.core.engine

import com.example.simbionte.core.engine.FragmentRole.*
import kotlin.random.Random

class TemplateEngine {

    private val recentFragments = mutableMapOf<String, MutableList<String>>()

    private val templates: List<Template> = listOf(
        Template("first_meeting", listOf(
            SlotDef(ABERTURA, "first_meeting"),
            SlotDef(FECHO, "open_question")
        ), condition = { ctx ->
            ctx.userProfile.totalInteractions <= 2
        }, priority = 100),

        Template("minimal", listOf(
            SlotDef(ABERTURA, "default")
        ), condition = { ctx ->
            ctx.userProfile.totalInteractions > 20 &&
            ctx.userProfile.avgMessageLength < 50f &&
            ctx.userProfile.communicationDepth < 0.3f &&
            ctx.userText.length < 50
        }, priority = 90),

        Template("greeting_warm", listOf(
            SlotDef(ABERTURA, "greeting_warm"),
            SlotDef(FECHO, "warm_check")
        ), condition = { ctx ->
            ctx.pattern.isGreeting &&
            ctx.userProfile.sentimentScore > 0.3f &&
            ctx.userProfile.totalInteractions > 10
        }, priority = 80),

        Template("greeting", listOf(
            SlotDef(ABERTURA, "greeting"),
            SlotDef(FECHO, "state_question")
        ), condition = { ctx ->
            ctx.pattern.isGreeting
        }, priority = 79),

        Template("affirmation", listOf(
            SlotDef(ABERTURA, "affirmation"),
            SlotDef(FECHO, "affirmation_note")
        ), condition = { ctx ->
            ctx.pattern.hasAffirmation
        }, priority = 70),

        Template("denial_warm", listOf(
            SlotDef(ABERTURA, "denial_warm"),
            SlotDef(FECHO, "denial_warm_note")
        ), condition = { ctx ->
            ctx.pattern.hasDenial &&
            ctx.userProfile.sentimentScore < -0.3f &&
            ctx.userProfile.totalInteractions > 10
        }, priority = 69),

        Template("denial", listOf(
            SlotDef(ABERTURA, "denial"),
            SlotDef(FECHO, "denial_note")
        ), condition = { ctx ->
            ctx.pattern.hasDenial
        }, priority = 68),

        Template("low_momentum", listOf(
            SlotDef(ABERTURA, "low_momentum"),
            SlotDef(FECHO, "momentum_note")
        ), condition = { ctx ->
            ctx.liveState.momentum < 0.2 && !ctx.pattern.isQuestion
        }, priority = 60),

        Template("low_sentiment", listOf(
            SlotDef(ABERTURA, "low_sentiment"),
            SlotDef(CONECTOR, "companion")
        ), condition = { ctx ->
            ctx.userProfile.sentimentScore < -0.3f &&
            ctx.userProfile.totalInteractions > 10 &&
            (ctx.relationType == RelationType.CONTRADICTION || ctx.pattern.isReflection)
        }, priority = 55),

        Template("high_sentiment", listOf(
            SlotDef(ABERTURA, "high_sentiment"),
            SlotDef(FECHO, "warm")
        ), condition = { ctx ->
            ctx.userProfile.sentimentScore > 0.3f &&
            ctx.liveState.momentum > 0.3
        }, priority = 54),

        Template("short_question", listOf(
            SlotDef(ABERTURA, "short_question"),
            SlotDef(FECHO, "thinking")
        ), condition = { ctx ->
            ctx.pattern.isQuestion && ctx.pattern.isShort
        }, priority = 50),

        Template("short_prompt", listOf(
            SlotDef(ABERTURA, "short_prompt"),
            SlotDef(FECHO, "prompt")
        ), condition = { ctx ->
            ctx.pattern.isShort && ctx.liveState.momentum < 0.3
        }, priority = 49),

        Template("drift_revisit", listOf(
            SlotDef(ABERTURA, "drift_revisit"),
            SlotDef(CONECTOR, "but"),
            SlotDef(FECHO, "revisit")
        ), condition = { ctx ->
            ctx.pattern.isTopicRevisit && ctx.liveState.topicDrift > 0.5
        }, priority = 45),

        Template("drift", listOf(
            SlotDef(ABERTURA, "drift"),
            SlotDef(CONECTOR, "drift_note")
        ), condition = { ctx ->
            ctx.liveState.topicDrift > 0.5
        }, priority = 44),

        Template("return_revisit", listOf(
            SlotDef(ABERTURA, "return_response"),
            SlotDef(CONECTOR, "tag_ref")
        ), condition = { ctx ->
            ctx.pattern.isTopicRevisit && ctx.relationType == RelationType.RETURN
        }, priority = 43),

        Template("return", listOf(
            SlotDef(ABERTURA, "return_response"),
            SlotDef(CONECTOR, "tag_ref")
        ), condition = { ctx ->
            ctx.relationType == RelationType.RETURN
        }, priority = 42),

        Template("reflection", listOf(
            SlotDef(ABERTURA, "reflection"),
            SlotDef(FECHO, "depth")
        ), condition = { ctx ->
            (ctx.pattern.isReflection &&
             ctx.userProfile.totalInteractions > 15 &&
             ctx.userProfile.reflectionRate > 0.25f) ||
            ctx.pattern.isReflection
        }, priority = 35),

        Template("flow_high", listOf(
            SlotDef(ABERTURA, "flow_high"),
            SlotDef(CONECTOR, "default")
        ), condition = { ctx ->
            ctx.liveState.momentum > 0.6 && ctx.relationType == RelationType.CONTINUATION
        }, priority = 30),

        Template("flow_med", listOf(
            SlotDef(ABERTURA, "flow_med"),
            SlotDef(CONECTOR, "default")
        ), condition = { ctx ->
            ctx.liveState.momentum > 0.4
        }, priority = 29),

        Template("contradiction", listOf(
            SlotDef(ABERTURA, "contradiction"),
            SlotDef(CONECTOR, "gentle"),
            SlotDef(FECHO, "contradiction_note")
        ), condition = { ctx ->
            ctx.relationType == RelationType.CONTRADICTION
        }, priority = 25),

        Template("continuation", listOf(
            SlotDef(ABERTURA, "continuation")
        ), condition = { ctx ->
            ctx.relationType == RelationType.CONTINUATION
        }, priority = 24),

        Template("side_thought", listOf(
            SlotDef(ABERTURA, "side_thought"),
            SlotDef(CONECTOR, "default")
        ), condition = { ctx ->
            ctx.relationType == RelationType.SIDE_THOUGHT
        }, priority = 23),

        Template("question", listOf(
            SlotDef(ABERTURA, "question"),
            SlotDef(FECHO, "thinking")
        ), condition = { ctx ->
            ctx.pattern.isQuestion
        }, priority = 20),

        Template("default", listOf(
            SlotDef(ABERTURA, "default")
        ), condition = { true }, priority = 0)
    )

    fun assemble(ctx: ResponseGenerator.GenContext, seed: Int): String {
        val rng = Random(seed + System.currentTimeMillis().toInt())
        val template = selectTemplate(ctx) ?: templates.last()

        val parts = mutableListOf<String>()
        for (slot in template.slots) {
            val pool = fragmentsFor(slot, ctx)
            if (pool.isEmpty()) continue
            if (slot.role == CONECTOR && rng.nextFloat() < 0.3f) continue
            if (slot.role == FECHO && rng.nextFloat() < 0.2f && template.slots.size > 1) continue
            val chosen = pickFragment(slot.role, slot.category, pool, rng)
            val resolved = resolvePlaceholders(chosen, ctx)
            if (slot.optional && resolved.isBlank()) continue
            parts.add(resolved)
        }

        val raw = parts.joinToString("")
        return postProcess(guardRecapitulacao(raw, ctx.userText))
    }

    private fun selectTemplate(ctx: ResponseGenerator.GenContext): Template? {
        return templates
            .filter { it.condition(ctx) }
            .maxByOrNull { it.priority }
    }

    private fun fragmentsFor(slot: SlotDef, ctx: ResponseGenerator.GenContext): List<Fragment> {
        val pool = when (slot.role) {
            ABERTURA -> FragmentCatalog.aberturas[slot.category] ?: emptyList()
            CONECTOR -> FragmentCatalog.conectores[slot.category] ?: emptyList()
            FECHO -> FragmentCatalog.fechos[slot.category] ?: emptyList()
            REFERENCIA -> emptyList()
        }
        return filterByTone(pool, ctx.userProfile.tonePreference)
    }

    private fun filterByTone(pool: List<Fragment>, tonePreference: String?): List<Fragment> {
        if (tonePreference == null || tonePreference == "neutral") return pool
        return pool.filter { it.tone == null || it.tone == tonePreference }
    }

    private fun pickFragment(role: FragmentRole, category: String, pool: List<Fragment>, rng: Random): Fragment {
        val key = "$role:$category"
        val used = recentFragments.getOrPut(key) { mutableListOf() }
        val available = pool.filter { it.text !in used }
        val source = if (available.isNotEmpty()) available else pool
        val chosen = source[rng.nextInt(source.size)]
        used.add(chosen.text)
        if (used.size > 6) used.removeAt(0)
        return chosen
    }

    private fun resolvePlaceholders(fragment: Fragment, ctx: ResponseGenerator.GenContext): String {
        var text = fragment.text
        if (text.contains("\$tagNote")) {
            val tags = ctx.currentBlock.tags?.takeIf { it.isNotBlank() }
                ?.split(",")?.filter { it.isNotBlank() }
            val tagNote = if (!tags.isNullOrEmpty()) tags.take(3).joinToString(", ") else "algo"
            text = text.replace("\$tagNote", tagNote)
        }
        if (text.contains("\$snippet")) {
            val snippet = ctx.selectedAssociation?.content?.take(50)
                ?.replace(Regex("""\s+\S*$"""), "") ?: ""
            text = text.replace("\$snippet", snippet)
        }
        return text
    }

    private fun postProcess(raw: String): String {
        var result = raw
            .trim()
            .replace(Regex("""\s+"""), " ")
            .replace(Regex("""\s+([.,!?:;])"""), "$1")
            .replaceFirstChar { it.uppercase() }
            .trim()
        if (result.endsWith("...")) {
            result = result.dropLast(3) + "…"
        }
        return result
    }

    private fun guardRecapitulacao(output: String, userText: String): String {
        if (userText.length < 15) return output
        val longWords = userText.split(Regex("\\s+")).filter { it.length > 10 }
        var result = output
        for (word in longWords) {
            result = result.replace(word, "...", ignoreCase = true)
        }
        return result
    }
}
