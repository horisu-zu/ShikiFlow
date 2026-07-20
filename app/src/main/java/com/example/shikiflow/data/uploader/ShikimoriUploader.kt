package com.example.shikiflow.data.uploader

import android.content.Context
import android.net.Uri
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.domain.model.media.UploadedMedia
import com.example.shikiflow.utils.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ShikimoriUploader @Inject constructor(
    private val context: Context,
    private val userApi: UserApi
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

            val imagePart = MultipartBody.Part.createFormData("image", fileName, fileBody)

            val response = userApi.uploadImage(imagePart)

            DataResult.Success(
                data = UploadedMedia(
                    url = response.url,
                    bbCode = response.bbCode,
                    mime = mime
                )
            )
        } catch (e: HttpException) {
            DataResult.Error("Upload failed: ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            DataResult.Error("Network error: ${e.message}")
        }
    }
}