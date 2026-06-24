package com.example.simbionte.core.engine

import com.example.simbionte.core.association.AssociationCandidate
import com.example.simbionte.core.association.AssociationEngine
import com.example.simbionte.core.continuity.ContinuityEngine
import com.example.simbionte.core.data.MemoryRepository
import com.example.simbionte.core.flow.FlowCoordinator
import com.example.simbionte.core.flow.FlowState
import com.example.simbionte.core.presence.MicroBehavior
import com.example.simbionte.core.presence.PresenceCoordinator
import com.example.simbionte.core.presence.ResponseEnvelope
import com.example.simbionte.core.profile.InputPattern
import com.example.simbionte.core.profile.UserModelEngine
import com.example.simbionte.core.transition.TransitionObserver
import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.UserProfile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

enum class State { AQUI, PENSANDO }

sealed class Event {
    data class StateChange(val state: State, val cognitiveState: LiveContextState? = null) : Event()
    data class UserEcho(val text: String) : Event()
    data class EntityMessage(val text: String, val relatedMemories: List<MemoryBlock> = emptyList()) : Event()
    data class SystemMessage(val kind: String) : Event()
}

class Brain private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    val events: SharedFlow<Event> = _events

    @Volatile
    private var repository: MemoryRepository? = null
    @Volatile
    private var pipeline: CognitivePipeline? = null
    @Volatile
    private var continuityEngine: ContinuityEngine? = null
    @Volatile
    private var transitionObserver: TransitionObserver? = null
    @Volatile
    private var flowCoordinator: FlowCoordinator? = null
    @Volatile
    private var presenceCoordinator: PresenceCoordinator? = null
    @Volatile
    private var microBehavior: MicroBehavior? = null
    @Volatile
    private var associationEngine: AssociationEngine? = null
    @Volatile
    private var userModelEngine: UserModelEngine? = null
    @Volatile
    private var lastAssociations: List<AssociationCandidate>? = null

    private val presenceLayer: CognitivePresenceLayer = CognitivePresenceLayerImpl()
    private val responseGenerator = ResponseGenerator()
    private val centralityEngine = CentralityEngine()
    private val onboardingEngine = OnboardingEngine()

    companion object {
        @Volatile
        private var INSTANCE: Brain? = null

        fun getInstance(): Brain {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Brain().also { INSTANCE = it }
            }
        }
    }

    fun init(
        repo: MemoryRepository,
        continuity: ContinuityEngine? = null,
        transitionObserver: TransitionObserver? = null,
        flowCoordinator: FlowCoordinator? = null,
        presenceCoordinator: PresenceCoordinator? = null,
        microBehavior: MicroBehavior? = null,
        associationEngine: AssociationEngine? = null,
        userModelEngine: UserModelEngine? = null
    ) {
        synchronized(this) {
            this.repository = repo
            this.continuityEngine = continuity
            this.transitionObserver = transitionObserver
            this.flowCoordinator = flowCoordinator
            this.presenceCoordinator = presenceCoordinator
            this.microBehavior = microBehavior
            this.associationEngine = associationEngine
            this.userModelEngine = userModelEngine
            this.lastAssociations = null
            this.pipeline = CognitivePipelineImpl(
                repository = repo,
                timelineEngine = TimelineEngineImpl(),
                contextTracker = ContextMemoryTrackerImpl(),
                contextResolver = ContextResolverImpl(),
                contextRanker = ContextRankerImpl(),
                relationBuilder = RelationBuilderImpl(),
                scoringEngine = MemoryScoringEngineImpl(),
                decayEngine = MemoryDecayEngineImpl()
            )
        }
    }

    fun getMemoryStream(conversaId: String): Flow<List<MemoryBlock>> {
        return repository?.getMemoriesByConversation(conversaId) ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }

    fun onUserInput(conversaId: String, text: String) {
        if (text.isBlank()) return

        scope.launch {
            continuityEngine?.openThread(conversaId)
            continuityEngine?.touchThread(conversaId)
            val pipe = pipeline ?: return@launch
            val repo = repository ?: return@launch

            try {
                _events.emit(Event.StateChange(State.PENSANDO))

                val result = pipe.process(conversaId, text)

                val liveState = presenceLayer.processPresence(text, result)

                val allMemories = repo.allMemories().firstOrNull() ?: emptyList()
                val recentMemories = repo.getMemoriesByConversation(conversaId).firstOrNull() ?: emptyList()
                val allRelations = repo.getAllRelations()
                val centralityMap = centralityEngine.calculateAll(allRelations)

                val associations = associationEngine?.findRelated(text, limit = 5)
                lastAssociations = associations

                val primaryRelation = result.relations.maxByOrNull { it.weight }
                val relType = primaryRelation?.relationshipType
                val prelimCtx = ResponseGenerator.GenContext(
                    userText = text,
                    window = result.window,
                    relations = result.relations,
                    liveState = liveState,
                    currentBlock = result.currentBlock,
                    contextId = result.contextId,
                    recentMemories = recentMemories,
                    allMemories = allMemories,
                    centralityMap = centralityMap
                )
                val pattern = responseGenerator.analyzePattern(prelimCtx)
                val profile = userModelEngine?.updateFromInteraction(text, pattern) ?: UserProfile()

                if (profile.onboardingStep in 0..4) {
                    handleOnboarding(conversaId, text, result, liveState, profile)
                    return@launch
                }

                _events.emit(Event.StateChange(State.AQUI, liveState))

                val prevContextId = transitionObserver?.getPreviousContextId()
                val historyBefore = transitionObserver?.getHistory() ?: emptyList()
                transitionObserver?.recordTransition(
                    conversaId = conversaId,
                    contextId = result.contextId,
                    momentum = liveState.momentum,
                    topicDrift = liveState.topicDrift
                )
                val flowState = flowCoordinator?.evaluate(
                    previousContextId = prevContextId,
                    currentContextId = result.contextId,
                    transitionHistory = historyBefore
                )
                val presenceState = flowState?.let { presenceCoordinator?.evaluate(it) }
                microBehavior?.setContext(liveState.momentum, associations?.size ?: 0, liveState.topicDrift)
                val importantMemories = allMemories
                    .filter { it.cognitiveSignal > 2.0f }
                    .sortedByDescending { it.cognitiveSignal }
                    .take(5)
                microBehavior?.setImportantMemories(importantMemories)
                val opening = presenceState?.let { microBehavior?.openingFor(it, conversaId) }

                val thinkDelay = Random.nextLong(300, 1000)
                delay(thinkDelay)

                generateFeedback(repo, conversaId, text, result, liveState, recentMemories, allMemories, centralityMap, flowState, opening, associations, profile, pattern, relType)
            } catch (e: Exception) {
                _events.emit(Event.SystemMessage("Erro no processamento: ${e.message}"))
                _events.emit(Event.StateChange(State.AQUI))
                transitionObserver?.recordTransition(
                    conversaId = conversaId,
                    contextId = null,
                    momentum = 0.0,
                    topicDrift = 0.0
                )
            }
        }
    }

    private suspend fun generateFeedback(
        repo: MemoryRepository,
        conversaId: String,
        userText: String,
        result: PipelineResult,
        liveState: LiveContextState,
        recentMemories: List<MemoryBlock>,
        allMemories: List<MemoryBlock>,
        centralityMap: Map<String, CentralityEngine.CentralityResult>,
        flowState: FlowState? = null,
        opening: String? = null,
        associations: List<AssociationCandidate>? = null,
        profile: UserProfile = UserProfile(),
        pattern: InputPattern = InputPattern(),
        relType: RelationType? = null
    ) {
        val selected = associations?.maxByOrNull { it.similarity }
        val ctx = ResponseGenerator.GenContext(
            userText = userText,
            window = result.window,
            relations = result.relations,
            liveState = liveState,
            currentBlock = result.currentBlock,
            contextId = result.contextId,
            recentMemories = recentMemories,
            allMemories = allMemories,
            centralityMap = centralityMap,
            flowState = flowState,
            associations = associations ?: emptyList(),
            selectedAssociation = selected,
            userProfile = profile,
            pattern = pattern,
            relationType = relType
        )
        val body = responseGenerator.generate(ctx)
        val envelope = ResponseEnvelope(opening = opening, body = body)
        repo.saveMemory(conversaId, envelope.toDisplayString(), isFromUser = false, contextId = result.contextId)
    }

    fun sendInitialGreeting(conversaId: String) {
        scope.launch {
            try {
                val repo = repository ?: return@launch
                _events.emit(Event.StateChange(State.PENSANDO))
                delay(Random.nextLong(800, 2200))

                val profile = userModelEngine?.getProfile() ?: UserProfile()
                val isNewUser = profile.totalInteractions == 0 && profile.onboardingStep == -1
                if (isNewUser) {
                    val startedProfile = profile.copy(onboardingStep = 0)
                    userModelEngine?.saveProfile(startedProfile)
                }

                if (profile.onboardingStep == -1 || isNewUser) {
                    val message = onboardingEngine.getFirstGreeting(0)
                    repo.saveMemory(conversaId, message, isFromUser = false)
                    _events.emit(Event.EntityMessage(message))
                } else {
                    val greetings = listOf(
                        "Olá! Que bom te ver de novo.",
                        "Oi! Bem-vindo de volta.",
                        "Olá! É bom te encontrar de novo por aqui."
                    )
                    val greeting = greetings[(conversaId.hashCode() and Int.MAX_VALUE) % greetings.size]
                    repo.saveMemory(conversaId, greeting, isFromUser = false)
                    _events.emit(Event.EntityMessage(greeting))
                }
                _events.emit(Event.StateChange(State.AQUI))
            } catch (_: Exception) {
                _events.emit(Event.StateChange(State.AQUI))
            }
        }
    }

    private suspend fun handleOnboarding(
        conversaId: String,
        userText: String,
        result: PipelineResult,
        liveState: LiveContextState,
        profile: UserProfile
    ) {
        val repo = repository ?: return
        val step = profile.onboardingStep
        val parsed = onboardingEngine.parseResponse(step, userText)

        val updated = profile.copy(
            onboardingStep = parsed.nextStep,
            userName = parsed.userName ?: profile.userName,
            userNickname = parsed.userNickname ?: profile.userNickname,
            purpose = parsed.purpose ?: profile.purpose,
            characterName = parsed.characterName ?: profile.characterName,
            limits = parsed.limits ?: profile.limits,
            tonePreference = parsed.tonePreference ?: profile.tonePreference
        )
        lastAssociations = null

        if (parsed.nextStep >= 5) {
            val completed = updated.copy(onboardingStep = -1)
            userModelEngine?.saveProfile(completed)
            val name = parsed.userNickname ?: parsed.userName
            val message = onboardingEngine.getCompletionMessage(name)
            repo.saveMemory(conversaId, message, isFromUser = false, contextId = result.contextId)
            _events.emit(Event.EntityMessage(message))
        } else {
            userModelEngine?.saveProfile(updated)
            val question = onboardingEngine.getQuestion(parsed.nextStep)
            repo.saveMemory(conversaId, question, isFromUser = false, contextId = result.contextId)
            _events.emit(Event.EntityMessage(question))
        }
        _events.emit(Event.StateChange(State.AQUI, liveState))
    }

    suspend fun getCharacterName(): String? {
        return userModelEngine?.getProfile()?.characterName
    }

    fun getLastAssociations(): List<AssociationCandidate>? = lastAssociations

    fun release() {
        synchronized(this) {
            scope.cancel()
            repository = null
            pipeline = null
            lastAssociations = null
            INSTANCE = null
        }
    }
}
