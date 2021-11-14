package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lecture(
    val id: Int,
    val title: String,
    val semester: String,
    val professor: String,
    val lecturePlan: String? = null,
) : Parcelable