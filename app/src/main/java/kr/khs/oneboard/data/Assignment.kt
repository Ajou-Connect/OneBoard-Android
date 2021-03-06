package kr.khs.oneboard.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Assignment(
    @Json(name = "assignmentId")
    override val id: Int,
    override val title: String,
    override val content: String,
    override val exposeDt: String,
    override val updatedDt: String,
    val fileUrl: String? = null,
    var startDt: String,
    var endDt: String,
    val score: Float
) : LectureBase(), Parcelable
