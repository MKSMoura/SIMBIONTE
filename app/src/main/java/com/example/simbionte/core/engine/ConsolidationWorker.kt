package com.example.simbionte.core.engine

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.simbionte.core.data.MemoryRepository
import com.example.simbionte.core.di.ServiceLocator
import java.util.concurrent.TimeUnit

class ConsolidationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = ServiceLocator.getMemoryRepository(applicationContext)
            val engine = ConsolidationEngine(repo)
            engine.consolidate()
            val summary = engine.getSummary()
            android.util.Log.i("Simbionte", "Consolidation done: ${summary.memoriesDecayed} decayed, ${summary.contextsPruned} pruned, ${summary.relationsClean} cleaned")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("Simbionte", "Consolidation failed", e)
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "simbionte_consolidation"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .build()

            val request = PeriodicWorkRequestBuilder<ConsolidationWorker>(
                6, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
