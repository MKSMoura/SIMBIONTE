package com.example.simbionte.core.presence

import com.example.simbionte.core.flow.FlowState

class PresenceCoordinator {

    fun evaluate(flowState: FlowState): PresenceState = when (flowState) {
        FlowState.FRESH -> PresenceState.INITIAL_CONTACT
        FlowState.CONTINUING -> PresenceState.ACTIVE_DIALOG
        FlowState.RETURNING -> PresenceState.RESUMED_DIALOG
        FlowState.INTERRUPTED -> PresenceState.INITIAL_CONTACT
    }
}
