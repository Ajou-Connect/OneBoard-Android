package kr.khs.oneboard.data.request

data class AssignmentUpdateRequestDto(
    val title: String,
    val content: String,
    val fileUrl: String,
    val startDt: String,
    val endDt: String,
    val exposeDt: String,
    val score: Float
)