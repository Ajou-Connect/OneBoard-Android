package kr.khs.oneboard.data

data class Lesson(
    val id: Int,
    val title: String,
    val date: String,
    val note: String? = null,
    val type: Int,
    val room: String? = null,
    val meetingId: String? = null,
    val videoUrl: String? = null
)
