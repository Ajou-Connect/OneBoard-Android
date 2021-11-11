package kr.khs.oneboard.data

data class Assignment(
    override val id: Int,
    override val title: String,
    override val content: String,
    override val exposeDt: String,
    override val createdDt: String,
    override val updatedDt: String,
    val fileUrl: String,
    val startDt: String,
    val endDt: String,
) : LectureBase()
