package com.example.simbionte.core.profile

import com.example.simbionte.db.UserProfile
import com.example.simbionte.db.UserProfileDao

data class InputPattern(
    val isQuestion: Boolean = false,
    val isGreeting: Boolean = false,
    val isShort: Boolean = false,
    val isLong: Boolean = false,
    val isReflection: Boolean = false,
    val isTopicRevisit: Boolean = false,
    val hasAffirmation: Boolean = false,
    val hasDenial: Boolean = false
)

class UserModelEngine(private val dao: UserProfileDao) {

    companion object {
        private val positiveWords = setOf(
            "bom", "otimo", "excelente", "maravilhoso", "feliz", "alegre", "contente",
            "gratidão", "obrigado", "obrigada", "sim", "adoro", "amo", "amor",
            "esperança", "confianca", "paz", "calma", "tranquilo", "conforto",
            "sucesso", "conquista", "realizado", "orgulho", "satisfeito",
            "incrivel", "fantastico", "belo", "lindo", "melhor", "abencoado",
            "saude", "bem", "positivo", "alivio", "gratidão", "abençoado",
            "risonho", "sorrir", "sorriso", "confortavel", "acolhedor",
            "abencoado", "vitoria", "sonho", "realizar", "consegui", "consegue"
        )

        private val negativeWords = setOf(
            "mal", "triste", "deprimido", "ansioso", "nervoso", "preocupado",
            "cansado", "exausto", "esgotado", "frustrado", "irritado", "raiva",
            "medo", "receio", "inseguro", "sozinho", "solidao", "perda", "luto",
            "dificil", "complicado", "problema", "erro", "falha", "fracasso",
            "horrivel", "pessimo", "terrivel", "inferno", "odio", "nojo",
            "culpa", "arrependimento", "vergonha", "desespero", "angustia",
            "dor", "sofrimento", "doenca", "perigo", "ameaca", "pior",
            "chateado", "magoado", "ofendido", "injustica", "revolta",
            "desanimado", "desmotivado", "perdi", "perdeu", "falhou"
        )
    }

    suspend fun getProfile(): UserProfile {
        return dao.get() ?: UserProfile().also { dao.save(it) }
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.save(profile)
    }

    suspend fun updateFromInteraction(text: String, pattern: InputPattern): UserProfile {
        val profile = dao.get() ?: UserProfile()
        val textLength = text.length.toFloat()
        val total = profile.totalInteractions + 1

        val alpha = 0.15f
        val newAvgLength = if (profile.totalInteractions == 0) {
            textLength.coerceAtMost(500f)
        } else {
            ((1f - alpha) * profile.avgMessageLength + alpha * textLength.coerceAtMost(500f))
        }

        val newQuestionRate = updateRate(profile.questionRate, profile.totalInteractions, pattern.isQuestion)
        val newReflectionRate = updateRate(profile.reflectionRate, profile.totalInteractions, pattern.isReflection)

        val depthObs = when {
            textLength > 200 -> 1.0f
            textLength > 100 -> 0.7f
            textLength > 50 -> 0.4f
            else -> 0.1f
        }
        val depthAlpha = 0.08f
        val newDepth = if (profile.totalInteractions == 0) depthObs
            else ((1f - depthAlpha) * profile.communicationDepth + depthAlpha * depthObs)

        val warmthObs = when {
            pattern.isGreeting || pattern.hasAffirmation -> 0.7f
            pattern.hasDenial -> 0.3f
            else -> 0.5f
        }
        val warmthAlpha = 0.04f
        val newWarmth = if (profile.totalInteractions == 0) warmthObs
            else ((1f - warmthAlpha) * profile.warmthPreference + warmthAlpha * warmthObs)

        val stabilityObs = if (pattern.isTopicRevisit) 0.7f else 0.3f
        val stabilityAlpha = 0.06f
        val newStability = if (profile.totalInteractions == 0) stabilityObs
            else ((1f - stabilityAlpha) * profile.topicStability + stabilityAlpha * stabilityObs)

        val sentimentObs = analyzeSentiment(text)
        val sentimentAlpha = 0.12f
        val newSentiment = if (profile.totalInteractions == 0) sentimentObs
            else ((1f - sentimentAlpha) * profile.sentimentScore + sentimentAlpha * sentimentObs)

        val updated = profile.copy(
            communicationDepth = newDepth.coerceIn(0f, 1f),
            warmthPreference = newWarmth.coerceIn(0f, 1f),
            avgMessageLength = newAvgLength,
            questionRate = newQuestionRate.coerceIn(0f, 1f),
            reflectionRate = newReflectionRate.coerceIn(0f, 1f),
            topicStability = newStability.coerceIn(0f, 1f),
            sentimentScore = newSentiment.coerceIn(-1f, 1f),
            totalInteractions = total,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        dao.save(updated)
        return updated
    }

    private fun analyzeSentiment(text: String): Float {
        val words = text.lowercase()
            .replace(Regex("""\P{L}"""), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
        if (words.isEmpty()) return 0f

        var score = 0f
        var matches = 0
        for (word in words) {
            when {
                word in positiveWords -> { score += 1f; matches++ }
                word in negativeWords -> { score -= 1f; matches++ }
            }
        }
        return if (matches == 0) 0f else (score / matches).coerceIn(-1f, 1f)
    }

    private fun updateRate(currentRate: Float, total: Int, observed: Boolean): Float {
        return (currentRate * total + (if (observed) 1f else 0f)) / (total + 1)
    }
}
