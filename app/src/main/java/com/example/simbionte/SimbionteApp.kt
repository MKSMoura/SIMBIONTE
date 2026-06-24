package com.example.simbionte

import android.app.Application
import com.example.simbionte.core.di.ServiceLocator
import com.example.simbionte.core.engine.ConsolidationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SimbionteApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initBrain(this)
        appScope.launch {
            ServiceLocator.getContinuityEngine(this@SimbionteApp).startSession()
        }
        ConsolidationWorker.schedule(this)
    }

    override fun onTerminate() {
        appScope.launch {
            ServiceLocator.getContinuityEngine(this@SimbionteApp).endSession()
        }
        ServiceLocator.reset()
        super.onTerminate()
    }
}
