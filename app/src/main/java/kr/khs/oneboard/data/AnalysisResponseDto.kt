package kr.khs.oneboard.data

data class AnalysisResponseDto(
    val lessonId: Int,
    val lessonTitle: String,
    val understandAnalysisDtoList: List<UnderstandingAnalysis>,
    val quizAnalysisDtoList: List<QuizAnalysis>
)
