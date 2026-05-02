package com.example.shikiflow.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shikiflow.domain.repository.MediaTracksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MediaTracksWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val mediaTracksRepository: MediaTracksRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            mediaTracksRepository.syncTracks()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}