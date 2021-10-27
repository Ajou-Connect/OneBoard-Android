package kr.khs.oneboard.data

import com.squareup.moshi.Json

data class Assignment(
    override val id: Int,
    override val lectureId: Int,
    override val title: String,
    override val content: String,
    override val exposeDT: String,
    override val createDT: Long,
    override val updateDT: Long,
    @Json(name = "file_url")
    val fileUrl: String,
    @Json(name = "start_dt")
    val startDT: String,
    @Json(name = "end_dt")
    val endDT: String,
) : LectureBase()
