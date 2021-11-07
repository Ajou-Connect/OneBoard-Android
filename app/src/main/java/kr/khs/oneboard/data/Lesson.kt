package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    val id: Int,
    val title: String,
    val date: String,
    val note: String? = null,
    val type: Int,
    val room: String? = null,
    val meetingId: String? = null,
    val videoUrl: String? = null
) : Parcelable
