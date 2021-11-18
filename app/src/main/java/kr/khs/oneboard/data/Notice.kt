package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notice(
    override val id: Int,
    override val title: String,
    override val content: String,
    override val exposeDt: String,
    override val updatedDt: String
) : LectureBase(), Parcelable