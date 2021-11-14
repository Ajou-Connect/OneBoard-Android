package kr.khs.oneboard.data

abstract class LectureBase {
    abstract val id: Int
    abstract val title: String
    abstract val content: String
    abstract val exposeDt: String
    abstract val updatedDt: String
}