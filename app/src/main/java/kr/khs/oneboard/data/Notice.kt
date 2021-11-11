package kr.khs.oneboard.data

data class Notice(
    override val id: Int,
    override val title: String,
    override val content: String,
    override val exposeDt: String,
    override val createdDt: String,
    override val updatedDt: String
) : LectureBase()

// 공지사항 등록 수정 삭제
// 시험/과제 등록 수정 삭제
// 수업 등록 수정 삭제