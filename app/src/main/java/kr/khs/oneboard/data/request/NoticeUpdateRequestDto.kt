package kr.khs.oneboard.data.request

data class NoticeUpdateRequestDto(
    val title: String,
    val content: String,
    val exposeDt: String
)