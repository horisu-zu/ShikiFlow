package com.example.shikiflow.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.shikiflow.utils.DateUtils.calculateDelayUntil
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaTracksScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private const val ONE_TIME_WORK_NAME = "MediaTracksLibraryOneTime"
        private const val PERIODIC_WORK_NAME = "MediaTracksLibrary"
        private const val ONE_TIME_BACKOFF_DELAY = 15 * 1000L //15 seconds
    }

    private val workManager by lazy { WorkManager.getInstance(context) }

    fun scheduleOneTimeSync(userId: Int) {
        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val mediaTracksRequest = OneTimeWorkRequestBuilder<MediaTracksWorker>()
            .setInputData(
                workDataOf(
                    "userId" to userId
                )
            )
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, ONE_TIME_BACKOFF_DELAY, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = ONE_TIME_WORK_NAME,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = mediaTracksRequest
        )
    }

    fun schedulePeriodicSync(userId: Int) {
        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val mediaTracksRequest = PeriodicWorkRequestBuilder<MediaTracksWorker>(
            repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInputData(
                workDataOf(
                    "userId" to userId
                )
            )
            .setConstraints(networkConstraints)
            .setInitialDelay(calculateDelayUntil(hour = 5, minute = 0), TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = PERIODIC_WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = mediaTracksRequest
        )
    }

    fun scheduleSyncs(userId: Int) {
        scheduleOneTimeSync(userId)
        schedulePeriodicSync(userId)
    }

    fun cancelAll() {
        workManager.cancelUniqueWork(ONE_TIME_WORK_NAME)
        workManager.cancelUniqueWork(PERIODIC_WORK_NAME)
    }
}