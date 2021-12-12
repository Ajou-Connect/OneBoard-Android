package kr.khs.oneboard.data

data class Understanding(
    val lessonId: Int,
    val lessonTitle: String,
    val understandId: Int,
    val createdDt: String,
    val yes: Int,
    val no: Int,
    val understandOList: List<UnderstandingItem>,
    val understandXList: List<UnderstandingItem>
)

data class UnderstandingItem(
    val studentId: Int,
    val studentName: String,
    val studentNumber: String,
    val response: Int
)

data class UnderstandingIdWrapper(
    val understandId: Int
)