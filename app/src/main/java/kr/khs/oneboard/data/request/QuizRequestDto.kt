package kr.khs.oneboard.data.request

data class QuizRequestDto(
    val question: String,
    val answer1: String = "",
    val answer2: String = "",
    val answer3: String = "",
    val answer4: String = "",
    val answer5: String = "",
    val answer: Int
)
