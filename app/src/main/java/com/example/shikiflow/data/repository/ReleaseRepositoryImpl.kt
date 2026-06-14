package com.example.shikiflow.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.di.annotations.GithubOkHttpClient
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.repository.ReleaseRepository
import com.example.shikiflow.presentation.viewmodel.more.about.UpdateState
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject

class ReleaseRepositoryImpl @Inject constructor(
    private val githubApi: GithubApi,
    @param:GithubOkHttpClient private val okHttpClient: OkHttpClient,
    @param:ApplicationContext private val context: Context
): ReleaseRepository {
    override fun getLatestRelease(
        owner: String,
        repo: String
    ): Flow<DataResult<GithubRelease>> = flow {
        emit(DataResult.Loading)

        try {
            val latestRelease = githubApi.getLatestRelease(owner, repo)

            emit(DataResult.Success(latestRelease))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Flow<DataResult<GithubRelease>> = flow {
        emit(DataResult.Loading)

        try {
            val versionRelease = githubApi.getReleaseByVersion(owner, repo, versionTag)

            versionRelease?.let {
                emit(DataResult.Success(versionRelease))
            } ?: emit(DataResult.Error(message = "Empty Response"))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun downloadRelease(url: String, fileName: String): Flow<UpdateState> = flow {
        try {
            val request = Request.Builder().url(url).build()
            val outputFile = File(context.getExternalFilesDir("apk"), fileName)

            okHttpClient.newCall(request).execute().use { response ->
                val body = response.body

                val totalBytes = body.contentLength()
                var downloadedBytes = 0L
                val buffer = ByteArray(8 * 1024)

                body.byteStream().use { input ->
                    outputFile.outputStream().use { output ->
                        var bytes: Int
                        while (input.read(buffer).also { bytes = it } != -1) {
                            output.write(buffer, 0, bytes)
                            downloadedBytes += bytes

                            val progress = if (totalBytes > 0) {
                                downloadedBytes.toFloat() / totalBytes
                            } else 0f

                            emit(UpdateState.Updating(progress))
                        }
                    }
                }

                emit(UpdateState.Completed(fileName))
            }
        } catch (e: Exception) {
            emit(UpdateState.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(Dispatchers.IO)

    override fun installRelease(fileName: String) {
        try {
            val apkFile = File(context.getExternalFilesDir("apk"), fileName)
            if(!apkFile.exists()) return

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, "application/vnd.android.package-archive")
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("ReleaseRepository", "Failed to launch APK installer", e)
        }
    }
}