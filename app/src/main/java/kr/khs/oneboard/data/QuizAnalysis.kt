package kr.khs.oneboard.data

data class QuizAnalysis(
    val quizId: Int,
    val createdDt: String,
    val question: String,
    val answer: Int,
    val answerStr: String,
    val correctNum: Int,
    val incorrectNum: Int
)
