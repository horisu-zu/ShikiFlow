package com.example.shikiflow.data.repository

import android.net.Uri
import com.example.shikiflow.data.uploader.MediaUploader
import com.example.shikiflow.di.annotations.CatBoxMediaUploader
import com.example.shikiflow.di.annotations.ShikimoriMediaUploader
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media.UploadedMedia
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.MediaUploaderRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.result.DataResult
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaUploaderRepositoryImpl @Inject constructor(
    @param:ShikimoriMediaUploader private val shikimoriUploader: MediaUploader,
    @param:CatBoxMediaUploader private val catBoxUploader: MediaUploader,
    private val settingsRepository: SettingsRepository
): MediaUploaderRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .distinctUntilChanged()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriUploader
                AuthType.ANILIST -> catBoxUploader
            }
        }

    override suspend fun upload(
        uri: Uri,
        mime: String,
        onProgress: (progress: Float) -> Unit
    ): DataResult<UploadedMedia> = withSourceSuspend(dataSource) { dataSource ->
        dataSource.upload(uri, mime, onProgress)
    }
}