package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Assignment(
    override val id: Int,
    override val title: String,
    override val content: String,
    override val exposeDt: String,
    override val updatedDt: String,
    val fileUrl: String,
    val startDt: String,
    val endDt: String,
    val score: Float
) : LectureBase(), Parcelable
