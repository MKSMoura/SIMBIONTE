package com.example.simbionte.core.flow

import com.example.simbionte.core.transition.TransitionRecord

class FlowCoordinator {

    fun evaluate(
        previousContextId: String?,
        currentContextId: String?,
        transitionHistory: List<TransitionRecord>
    ): FlowState {
        if (previousContextId == null) return FlowState.FRESH

        val currentExistsInHistory = transitionHistory.any { it.toContextId == currentContextId }

        return when {
            currentContextId == previousContextId -> FlowState.CONTINUING
            currentExistsInHistory -> FlowState.RETURNING
            else -> FlowState.INTERRUPTED
        }
    }
}
