package com.example.simbionte.core.presence

import com.example.simbionte.db.MemoryBlock

class MicroBehavior {

    private val initialOpenings = listOf(
        "Estava esperando você. Como você está?",
        "É você! Tava sentindo sua falta por aqui. Como anda a vida?",
        "Opa, que bom te encontrar. Me conta, o que tem passado pela sua cabeça?",
        "Ah, você chegou! Tava pensando em como você tá. E então?",
        "Fala aí! Tava aqui mesmo matutando umas ideias. O que você traz de novo?",
        "Que bom te ver por aqui. Me diz, como tá o dia?",
        "E aí! Tava sentindo que você ia aparecer. Tem algo em mente?",
        "Opa! Ia te perguntar: como você tá hoje? Parece que tem algo no ar.",
        "Bom te encontrar. O que você andou pensando desde a última vez?",
        "Você por aqui! Senta e conta. Tô todo ouvidos."
    )

    private val resumedOpenings = listOf(
        "Bem-vindo de volta.",
        "Você voltou! Tava com saudade.",
        "Aí sim, de volta à ativa.",
        "Você de novo! Sempre bom.",
        "Que bom que você voltou.",
        "Voltou! Tava pensando em você.",
        "E aí, de volta! Como você está?",
        "Você apareceu de novo! Que bom.",
        "Bem-vindo de volta. Senta e conta como foi lá fora."
    )

    private var interactionCount = 0
    private var lastCommentInteraction = 0
    private var highMomentumStreak = 0
    private var lastMomentum = 0.0
    private var lastAssociationCount = 0
    private var lastTopicDrift = 0.0
    private var importantMemories: List<MemoryBlock> = emptyList()

    fun setContext(momentum: Double, associationCount: Int, topicDrift: Double) {
        lastMomentum = momentum
        lastAssociationCount = associationCount
        lastTopicDrift = topicDrift
    }

    fun setImportantMemories(memories: List<MemoryBlock>) {
        importantMemories = memories
    }

    fun openingFor(state: PresenceState, conversaId: String?): String? = when (state) {
        PresenceState.INITIAL_CONTACT -> {
            interactionCount = 0
            val index = ((conversaId?.hashCode() ?: 0) and Int.MAX_VALUE) % initialOpenings.size
            initialOpenings[index]
        }
        PresenceState.ACTIVE_DIALOG -> {
            interactionCount++
            checkSpontaneousComment()
        }
        PresenceState.RESUMED_DIALOG -> {
            interactionCount = 0
            lastCommentInteraction = 0
            val hook = buildMemoryHook()
            if (hook != null) hook else {
                val index = ((conversaId?.hashCode() ?: 0) and Int.MAX_VALUE) % resumedOpenings.size
                resumedOpenings[index]
            }
        }
    }

    private fun buildMemoryHook(): String? {
        if (importantMemories.isEmpty()) return null
        val topMemories = importantMemories
            .filter { it.isFromUser }
            .sortedByDescending { it.cognitiveSignal }
            .take(3)
        if (topMemories.isEmpty()) return null

        val selected = topMemories.first()
        if (selected.cognitiveSignal < 2.5f) return null

        val content = selected.content.take(50).replace(Regex("""\s+\S*$"""), "")
        val lower = content.lowercase()
        val keywords = listOf("preciso", "vou", "quero", "estou", "tenho", "estava", "tava")
        val isPending = keywords.any { lower.startsWith(it) || lower.contains(" $it") }

        return if (isPending) {
            listOf(
                "Fiquei pensando naquilo que você falou sobre \"$content\"… Como é que ficou isso?",
                "Aquilo sobre \"$content\" ainda tá na minha cabeça. Como você tá em relação a isso?",
                "Sabe o que ficou ecoando aqui depois que a gente conversou? Aquilo sobre \"$content\".",
                "Eu fiquei matutando sobre \"$content\" depois que você foi. E aí, mudou alguma coisa?"
            )[selected.id.hashCode() and Int.MAX_VALUE % 4]
        } else {
            listOf(
                "Lembra que você tocou naquele assunto de \"$content\"? Fiquei refletindo sobre isso depois.",
                "Uma coisa que você disse ficou comigo: \"$content\". Você ainda pensa assim?",
                "Depois que a gente conversou, fiquei pensando muito naquilo sobre \"$content\".",
                "Sabe o que me marcou na nossa última conversa? Quando você falou sobre \"$content\"."
            )[selected.id.hashCode() and Int.MAX_VALUE % 4]
        }
    }

    private fun checkSpontaneousComment(): String? {
        if (lastMomentum > 0.6) highMomentumStreak++ else highMomentumStreak = 0
        if (interactionCount - lastCommentInteraction < 5) return null

        return when {
            highMomentumStreak >= 3 -> {
                lastCommentInteraction = interactionCount
                highMomentumStreak = 0
                highMomentumComment()
            }
            lastAssociationCount >= 3 -> {
                lastCommentInteraction = interactionCount
                associationComment()
            }
            lastTopicDrift > 0.5 -> {
                lastCommentInteraction = interactionCount
                driftComment()
            }
            else -> null
        }
    }

    private fun highMomentumComment(): String = listOf(
        "Tô sentindo uma energia boa nessa conversa.",
        "O ritmo tá gostoso, não tá?",
        "Essa conversa tá fluindo demais.",
        "Que bom que isso tá rendendo tanto.",
        "Tô gostando de onde isso tá indo."
    )[(lastCommentInteraction / 5) % 5]

    private fun associationComment(): String = listOf(
        "Esse assunto é quase uma marca sua.",
        "Esse tema volta sempre — deve ter um peso especial.",
        "Isso que você fala é algo que aparece muito aqui.",
        "Você toca nesse ponto com frequência. Acho que é importante pra você.",
        "Isso é um tema que retorna bastante. Tem um lugar seu aqui."
    )[(lastCommentInteraction / 5) % 5]

    private fun driftComment(): String = listOf(
        "Sinto que você tá mudando de direção agora.",
        "Tô percebendo um desvio natural no que você tá trazendo.",
        "O rumo tá mudando, e parece orgânico.",
        "Você tá indo pra outro lugar agora. Tô acompanhando.",
        "Tô sentindo que o foco se moveu. E tá tudo bem."
    )[(lastCommentInteraction / 5) % 5]
}
