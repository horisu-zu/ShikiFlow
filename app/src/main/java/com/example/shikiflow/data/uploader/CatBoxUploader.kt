package com.example.shikiflow.data.uploader

import android.content.Context
import android.net.Uri
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.media.UploadedMedia
import com.example.shikiflow.utils.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class CatBoxUploader @Inject constructor(
    private val context: Context,
    private val client: OkHttpClient
) : MediaUploader {

    override suspend fun upload(
        uri: Uri,
        mime: String,
        onProgress: (progress: Float) -> Unit
    ): DataResult<UploadedMedia> = withContext(Dispatchers.IO) {
        try {
            val fileName = context.queryDisplayName(uri)
            val fileBody = UriRequestBody(context, uri, mime) { up, total ->
                onProgress(up.toFloat() / total)
            }

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reqtype", "fileupload")
                .addFormDataPart("fileToUpload", fileName, fileBody)
                .build()

            val request = Request.Builder()
                .url(BuildConfig.CATBOX_ENDPOINT)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext DataResult.Error(
                        "HTTP ${response.code}: ${response.message}"
                    )
                }

                val body = response.body.string().trim()
                if (body.isEmpty() || !body.startsWith("http")) {
                    return@withContext DataResult.Error(
                        "Unexpected response: $body"
                    )
                }

                DataResult.Success(
                    data = UploadedMedia(
                        url = body,
                        bbCode = null,
                        mime = mime
                    )
                )
            }
        } catch (e: IOException) {
            DataResult.Error(message = "Network error: ${e.message}")
        } catch (e: Exception) {
            DataResult.Error(message = "Unexpected error: ${e.message}")
        }
    }
}