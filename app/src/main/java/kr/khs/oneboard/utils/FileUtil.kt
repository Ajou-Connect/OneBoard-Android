package kr.khs.oneboard.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import timber.log.Timber


// https://ohdbjj.tistory.com/49
fun Uri.asMultipart(name: String, contentResolver: ContentResolver): MultipartBody.Part? {
    return contentResolver.query(this, null, null, null, null)?.let {
        if (it.moveToNext()) {
            Timber.tag("ricky").d(this.getFileName(contentResolver))
            val displayName = this.getFileName(contentResolver)
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return contentResolver.getType(this@asMultipart)?.toMediaType()
                }

                override fun writeTo(sink: BufferedSink) {
                    sink.writeAll(contentResolver.openInputStream(this@asMultipart)?.source()!!)
                }
            }
            it.close()
            MultipartBody.Part.createFormData(name, displayName, requestBody)
        } else {
            it.close()
            null
        }
    }
}

@SuppressLint("Range")
fun Uri.getFileName(contentResolver: ContentResolver): String {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor: Cursor? = contentResolver.query(this, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = this.lastPathSegment
    }
    return result ?: "nullll"
}

fun Fragment.fileDownload(
    title: String,
    description: String,
    downloadUrl: String,
    fileName: String
) {
    val request = DownloadManager.Request(Uri.parse(downloadUrl))
        .setTitle(title)
        .setDescription(description)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
        .setRequiresCharging(false)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    (requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
        request
    )
}