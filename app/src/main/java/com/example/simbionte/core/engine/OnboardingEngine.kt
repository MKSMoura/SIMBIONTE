package com.example.simbionte.core.engine

import kotlin.random.Random

class OnboardingEngine {

    data class StepResult(
        val userName: String? = null,
        val userNickname: String? = null,
        val purpose: String? = null,
        val characterName: String? = null,
        val limits: String? = null,
        val tonePreference: String? = null,
        val nextStep: Int
    )

    private val questions = mapOf(
        0 to listOf(
            "Como você prefere ser chamado?",
            "Qual é o seu nome?",
            "Como posso te chamar?"
        ),
        1 to listOf(
            "O que te traz aqui?",
            "O que você está buscando?",
            "Me conta, o que te motivou a vir?"
        ),
        2 to listOf(
            "Como você gostaria de me chamar?",
            "Que nome você quer me dar?",
            "Tem algum nome que você prefere usar pra mim?"
        ),
        3 to listOf(
            "Existe algum assunto que você prefere evitar?",
            "Tem algo que você não quer que eu toque?",
            "Prefere evitar algum tema em especial?"
        ),
        4 to listOf(
            "Você prefere uma conversa mais formal ou mais casual?",
            "Qual estilo de conversa você prefere — mais relaxado ou mais sério?",
            "Você prefere que eu fale de um jeito mais formal ou mais informal?"
        )
    )

    fun getQuestion(step: Int, rng: Random = Random): String {
        val variants = questions[step] ?: return ""
        return variants[rng.nextInt(variants.size)]
    }

    fun getFirstGreeting(step: Int, rng: Random = Random): String {
        val greetings = listOf(
            "Olá! Prazer em te conhecer! Sou o Simbionte, seu espaço de reflexão.",
            "Oi! Que bom ter você aqui. Sou o Simbionte — um lugar para pensar em voz alta.",
            "Olá! É um prazer te conhecer. Sou o Simbionte, seu companheiro cognitivo."
        )
        val greeting = greetings[rng.nextInt(greetings.size)]
        val question = getQuestion(step, rng)
        return "$greeting $question"
    }

    fun getCompletionMessage(name: String?, rng: Random = Random): String {
        val messages = if (!name.isNullOrBlank()) {
            listOf(
                "Perfeito, $name! Já configurei tudo. Pode falar à vontade.",
                "Pronto, $name! Agora é só conversar — estou aqui.",
                "Tudo certo, $name! Pode desabafar à vontade."
            )
        } else {
            listOf(
                "Perfeito! Já configurei tudo. Pode falar à vontade.",
                "Pronto! Agora é só conversar — estou aqui.",
                "Tudo certo! Pode desabafar à vontade."
            )
        }
        return messages[rng.nextInt(messages.size)]
    }

    fun parseResponse(step: Int, text: String): StepResult {
        val cleaned = text.trim()
        val nextStep = if (step >= 4) 5 else step + 1

        return when (step) {
            0 -> parseNameStep(cleaned, nextStep)
            1 -> parsePurposeStep(cleaned, nextStep)
            2 -> parseCharacterNameStep(cleaned, nextStep)
            3 -> parseLimitsStep(cleaned, nextStep)
            4 -> parseToneStep(cleaned, nextStep)
            else -> StepResult(nextStep = nextStep)
        }
    }

    private fun parseNameStep(text: String, nextStep: Int): StepResult {
        val patterns = listOf(
            Regex("""pode me chamar de\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""me chamo\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""meu nome (é|e)\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""sou (o|a)\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""sou\s+(.+)$""", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val value = match.groupValues.last().trim().replaceFirstChar { it.uppercase() }
                val isNickname = match.groupValues[0].contains("chamar")
                return if (isNickname) {
                    StepResult(userNickname = value, nextStep = nextStep)
                } else {
                    StepResult(userName = value, nextStep = nextStep)
                }
            }
        }
        val cleaned = text.trim().replaceFirstChar { it.uppercase() }
        return if (cleaned.length in 2..30) {
            StepResult(userNickname = cleaned, nextStep = nextStep)
        } else {
            StepResult(nextStep = nextStep)
        }
    }

    private fun parsePurposeStep(text: String, nextStep: Int): StepResult {
        val patterns = listOf(
            Regex("""quero\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""vim (para|por)\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""estou aqui (para|por)\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""preciso de\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""por causa de\s+(.+)$""", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return StepResult(purpose = match.groupValues.last().trim(), nextStep = nextStep)
            }
        }
        val cleaned = text.trim().take(200)
        return if (cleaned.length >= 5) {
            StepResult(purpose = cleaned, nextStep = nextStep)
        } else {
            StepResult(nextStep = nextStep)
        }
    }

    private fun parseCharacterNameStep(text: String, nextStep: Int): StepResult {
        val patterns = listOf(
            Regex("""pode ser\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""quero te chamar de\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""você pode se chamar\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""te chamo de\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""pode me chamar de\s+(.+)$""", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return StepResult(characterName = match.groupValues.last().trim(), nextStep = nextStep)
            }
        }
        val cleaned = text.trim().replaceFirstChar { it.uppercase() }
        return if (cleaned.length in 2..30) {
            StepResult(characterName = cleaned, nextStep = nextStep)
        } else {
            StepResult(nextStep = nextStep)
        }
    }

    private fun parseLimitsStep(text: String, nextStep: Int): StepResult {
        val denialPatterns = listOf(
            Regex("""não (tenho|quero|preciso|sei).*""", RegexOption.IGNORE_CASE),
            Regex("""^(nada|nenhum|não|tanto faz|qualquer|tudo bem)$""", RegexOption.IGNORE_CASE)
        )
        for (pattern in denialPatterns) {
            if (pattern.matches(text.trim())) {
                return StepResult(limits = "", nextStep = nextStep)
            }
        }
        val patterns = listOf(
            Regex("""não gosto de\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""evite\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""não quero falar (sobre|de)\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""prefiro evitar\s+(.+)$""", RegexOption.IGNORE_CASE),
            Regex("""melhor evitar\s+(.+)$""", RegexOption.IGNORE_CASE)
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return StepResult(limits = match.groupValues.last().trim(), nextStep = nextStep)
            }
        }
        val cleaned = text.trim().take(200)
        return if (cleaned.length >= 3) {
            StepResult(limits = cleaned, nextStep = nextStep)
        } else {
            StepResult(nextStep = nextStep)
        }
    }

    private fun parseToneStep(text: String, nextStep: Int): StepResult {
        val lower = text.trim().lowercase()
        val tone = when {
            lower.contains("formal") || lower.contains("sério") || lower.contains("serio") -> "formal"
            lower.contains("casual") || lower.contains("informal") || lower.contains("relaxado") -> "casual"
            else -> "neutral"
        }
        return StepResult(tonePreference = tone, nextStep = nextStep)
    }
}
