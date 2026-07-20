package com.example.shikiflow.data.uploader

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

class UriRequestBody(
    private val context: Context,
    private val uri: Uri,
    private val mime: String,
    private val onProgress: ((uploaded: Long, total: Long) -> Unit)? = null
) : RequestBody() {

    private val totalBytes: Long by lazy { resolveSize() }

    override fun contentType() = mime.toMediaTypeOrNull()

    override fun contentLength(): Long = totalBytes

    override fun writeTo(sink: BufferedSink) {
        val resolver = context.contentResolver
        val total = totalBytes
        var written = 0L
        var lastFireMs = 0L

        resolver.openInputStream(uri)?.use { input ->
            input.source().use { source ->
                val buf = okio.Buffer()
                while (true) {
                    val read = source.read(buf, CHUNK)
                    if (read == -1L) break
                    sink.write(buf, read)
                    written += read
                    val now = System.currentTimeMillis()
                    if (now - lastFireMs >= MIN_PROGRESS_INTERVAL_MS) {
                        lastFireMs = now
                        onProgress?.invoke(written, total)
                    }
                }
                onProgress?.invoke(written, total)
            }
        } ?: throw IOException("Could not open input stream for $uri")
    }

    private fun resolveSize(): Long {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else -1L }
                ?: -1L
        }.getOrDefault(-1L)
    }

    companion object {
        private const val CHUNK = 256L * 1024L
        private const val MIN_PROGRESS_INTERVAL_MS = 50L
    }
}

fun Context.queryDisplayName(uri: Uri): String {
    return runCatching {
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getString(0) else null }
    }.getOrNull() ?: "upload.bin"
}