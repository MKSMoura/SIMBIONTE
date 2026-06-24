package com.example.simbionte.core.profile

import com.example.simbionte.db.UserProfile

enum class AdaptiveTone { CONCISE, BALANCED, REFLEXIVE }
enum class AdaptiveWarmth { NEUTRAL, BALANCED, WARM }

class AdaptiveResponseEngine {

    fun adjustBucket(baseBucket: Int, profile: UserProfile): Int {
        val warmthShift = when {
            profile.warmthPreference > 0.6 -> 1
            profile.warmthPreference < 0.4 -> -1
            else -> 0
        }
        return ((baseBucket + warmthShift) % 3 + 3) % 3
    }

    fun selectTone(profile: UserProfile): AdaptiveTone {
        return when {
            profile.communicationDepth > 0.6 && profile.totalInteractions > 15 -> AdaptiveTone.REFLEXIVE
            profile.communicationDepth < 0.3 && profile.totalInteractions > 10 -> AdaptiveTone.CONCISE
            else -> AdaptiveTone.BALANCED
        }
    }

    fun selectWarmth(profile: UserProfile): AdaptiveWarmth {
        return when {
            profile.warmthPreference > 0.6 && profile.totalInteractions > 10 -> AdaptiveWarmth.WARM
            profile.warmthPreference < 0.4 && profile.totalInteractions > 10 -> AdaptiveWarmth.NEUTRAL
            else -> AdaptiveWarmth.BALANCED
        }
    }

    fun shouldProbeQuestions(profile: UserProfile): Boolean {
        return profile.questionRate > 0.3 && profile.totalInteractions > 20
    }

    fun shouldOfferReflection(profile: UserProfile): Boolean {
        return profile.reflectionRate > 0.25 && profile.totalInteractions > 15
    }

    fun shouldUseMinimal(profile: UserProfile, textLength: Int): Boolean {
        return profile.totalInteractions > 20 &&
               profile.avgMessageLength < 50f &&
               profile.communicationDepth < 0.3f &&
               textLength < 50
    }
}
