package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import com.example.data.model.SelectedMediaSource
import java.io.InputStream
import java.text.DecimalFormat

object MediaHelper {

    fun resolveSelectedMedia(
        context: Context,
        uri: Uri,
        isVideo: Boolean
    ): SelectedMediaSource {
        var displayName = if (isVideo) "recitation_video.mp4" else "recitation_audio.mp3"
        var sizeBytes = 0L

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        cursor.getString(nameIndex)?.let { displayName = it }
                    }
                    if (sizeIndex != -1) {
                        sizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) {}

        val mimeType = context.contentResolver.getType(uri)
            ?: if (isVideo) "video/mp4" else "audio/mpeg"

        val formattedSize = formatFileSize(sizeBytes)
        val base64 = extractBase64Sample(context, uri, maxBytes = 2 * 1024 * 1024)

        return SelectedMediaSource(
            uriString = uri.toString(),
            fileName = displayName,
            fileSizeFormatted = formattedSize,
            mimeType = mimeType,
            isVideo = isVideo,
            base64Data = base64
        )
    }

    private fun extractBase64Sample(context: Context, uri: Uri, maxBytes: Int): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream: InputStream ->
                val buffer = ByteArray(maxBytes)
                var totalRead = 0
                var read: Int
                while (totalRead < maxBytes) {
                    read = stream.read(buffer, totalRead, maxBytes - totalRead)
                    if (read == -1) break
                    totalRead += read
                }
                if (totalRead > 0) {
                    Base64.encodeToString(buffer, 0, totalRead, Base64.NO_WRAP)
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "حجم غير محدد"
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        val df = DecimalFormat("#.##")
        return "${df.format(size)} ${units[unitIndex]}"
    }
}
