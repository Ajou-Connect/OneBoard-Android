package kr.khs.oneboard.data

data class Quiz(
    val lessonId: Int,
    val lessonTitle: String,
    val quizId: Int,
    val createdDt: String,
    val correctNum: Int,
    val incorrectNum: Int,
    val quizOList: List<QuizStudent>,
    val quizXList: List<QuizStudent>
) {
    data class QuizStudent(
        val studentId: Int,
        val studentName: String,
        val studentNumber: String,
        val response: Int
    )
}

data class StudentQuizResponse(
    val lessonId: String,
    val lessonTitle: String,
    val quizId: Int,
    val createdDt: String,
    val yourAnswerNum: Int,
    val yourAnswerStr: String,
    val correctAnswerNum: Int,
    val correctAnswerStr: String,
    val correctNum: Int,
    val incorrectNum: Int
)

data class QuizIdWrapper(
    val quizId: Int
)