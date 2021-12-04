package kr.khs.oneboard.data.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonUpdateRequestDto(
    val title: String,
    val date: String,
    val type: Int,
    val room: String? = null,
    val videoUrl: String? = null
) : Parcelable
