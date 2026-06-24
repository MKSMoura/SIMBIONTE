package com.example.simbionte.core.di

import android.content.Context
import com.example.simbionte.core.continuity.ContinuityEngine
import com.example.simbionte.core.data.ConversationRepository
import com.example.simbionte.core.data.MemoryRepository
import com.example.simbionte.core.association.AssociationEngine
import com.example.simbionte.core.association.AssociationEngineImpl
import com.example.simbionte.core.engine.Brain
import com.example.simbionte.core.engine.WordSimilarity
import com.example.simbionte.core.flow.FlowCoordinator
import com.example.simbionte.core.presence.MicroBehavior
import com.example.simbionte.core.presence.PresenceCoordinator
import com.example.simbionte.core.profile.UserModelEngine
import com.example.simbionte.core.transition.TransitionObserver
import com.example.simbionte.db.AppDatabase

object ServiceLocator {
    @Volatile
    private var database: AppDatabase? = null

    @Volatile
    private var memoryRepository: MemoryRepository? = null

    @Volatile
    private var conversationRepository: ConversationRepository? = null

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

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getDatabase(context.applicationContext).also { database = it }
        }
    }

    fun getMemoryRepository(context: Context): MemoryRepository {
        return memoryRepository ?: synchronized(this) {
            memoryRepository ?: MemoryRepository(getDatabase(context).memoryDao()).also { memoryRepository = it }
        }
    }

    fun getConversationRepository(context: Context): ConversationRepository {
        return conversationRepository ?: synchronized(this) {
            conversationRepository ?: ConversationRepository(getMemoryRepository(context)).also { conversationRepository = it }
        }
    }

    fun getContinuityEngine(context: Context): ContinuityEngine {
        return continuityEngine ?: synchronized(this) {
            continuityEngine ?: ContinuityEngine(getDatabase(context).continuityDao()).also { continuityEngine = it }
        }
    }

    fun getTransitionObserver(): TransitionObserver {
        return transitionObserver ?: synchronized(this) {
            transitionObserver ?: TransitionObserver().also { transitionObserver = it }
        }
    }

    fun getFlowCoordinator(): FlowCoordinator {
        return flowCoordinator ?: synchronized(this) {
            flowCoordinator ?: FlowCoordinator().also { flowCoordinator = it }
        }
    }

    fun getPresenceCoordinator(): PresenceCoordinator {
        return presenceCoordinator ?: synchronized(this) {
            presenceCoordinator ?: PresenceCoordinator().also { presenceCoordinator = it }
        }
    }

    fun getMicroBehavior(): MicroBehavior {
        return microBehavior ?: synchronized(this) {
            microBehavior ?: MicroBehavior().also { microBehavior = it }
        }
    }

    fun getAssociationEngine(context: Context): AssociationEngine {
        return associationEngine ?: synchronized(this) {
            associationEngine ?: AssociationEngineImpl(
                wordSimilarity = WordSimilarity(),
                repository = getMemoryRepository(context)
            ).also { associationEngine = it }
        }
    }

    fun getUserModelEngine(context: Context): UserModelEngine {
        return userModelEngine ?: synchronized(this) {
            userModelEngine ?: UserModelEngine(getDatabase(context).userProfileDao()).also { userModelEngine = it }
        }
    }

    fun initBrain(context: Context) {
        val repo = getMemoryRepository(context)
        val continuity = getContinuityEngine(context)
        val observer = getTransitionObserver()
        val flowCoordinator = getFlowCoordinator()
        val presenceCoordinator = getPresenceCoordinator()
        val microBehavior = getMicroBehavior()
        val associationEngine = getAssociationEngine(context)
        val userModelEngine = getUserModelEngine(context)
        Brain.getInstance().init(repo, continuity, observer, flowCoordinator, presenceCoordinator, microBehavior, associationEngine, userModelEngine)
    }

    fun reset() {
        synchronized(this) {
            database?.close()
            database = null
            memoryRepository = null
            conversationRepository = null
            continuityEngine = null
            transitionObserver = null
            flowCoordinator = null
            presenceCoordinator = null
            microBehavior = null
            associationEngine = null
            userModelEngine = null
            Brain.getInstance().release()
        }
    }
}
