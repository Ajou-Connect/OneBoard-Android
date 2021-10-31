package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notice(
    override val id: Int,
    override val lectureId: Int,
    override val title: String,
    override val content: String,
    override val writer: String,
    override val exposeDT: String,
    override val createDT: Long,
    override val updateDT: Long
) : LectureBase(), Parcelable

// 공지사항 등록 수정 삭제
// 시험/과제 등록 수정 삭제
// 수업 등록 수정 삭제