package com.example.simbionte.core.transition

class TransitionObserver(private val maxHistory: Int = 200) {

    private val history = mutableListOf<TransitionRecord>()
    private var lastContextId: String? = null
    private var lastMomentum: Double = 0.0
    private var lastTopicDrift: Double = 0.0
    private var lastConversaId: String? = null

    fun recordTransition(
        conversaId: String?,
        contextId: String?,
        momentum: Double,
        topicDrift: Double
    ) {
        if (lastContextId == null) {
            lastContextId = contextId
            lastMomentum = momentum
            lastTopicDrift = topicDrift
            lastConversaId = conversaId
            return
        }

        val record = TransitionRecord(
            timestamp = System.currentTimeMillis(),
            conversaId = conversaId,
            fromContextId = lastContextId,
            toContextId = contextId,
            previousMomentum = lastMomentum,
            currentMomentum = momentum,
            previousTopicDrift = lastTopicDrift,
            currentTopicDrift = topicDrift
        )

        if (history.size >= maxHistory) {
            history.removeAt(0)
        }
        history.add(record)

        lastContextId = contextId
        lastMomentum = momentum
        lastTopicDrift = topicDrift
        lastConversaId = conversaId
    }

    fun getHistory(): List<TransitionRecord> = history.toList()

    fun getLastRecord(): TransitionRecord? = history.lastOrNull()

    fun getPreviousContextId(): String? = lastContextId

    fun getPreviousConversaId(): String? = lastConversaId

    fun clear() {
        history.clear()
        lastContextId = null
        lastMomentum = 0.0
        lastTopicDrift = 0.0
        lastConversaId = null
    }
}
