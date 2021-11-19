package kr.khs.oneboard.data.request

data class LessonUpdateRequestDto(
    val title: String,
    val date: String,
    val note: String,
    val type: Int,
    val room: String? = null,
    val meetingId: String? = null,
    val videoUrl: String? = null
)
