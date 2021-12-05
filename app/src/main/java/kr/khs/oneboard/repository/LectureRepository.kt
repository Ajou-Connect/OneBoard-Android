package kr.khs.oneboard.repository

import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import okhttp3.MultipartBody

interface LectureRepository {
    suspend fun getDetailLecture(lectureId: Int): UseCase<Triple<Notice?, Lesson?, Assignment?>>

    suspend fun getNoticeList(lectureId: Int): UseCase<List<Notice>>

    suspend fun postNotice(lectureId: Int, notice: NoticeUpdateRequestDto): UseCase<Boolean>

    suspend fun putNotice(
        lectureId: Int,
        noticeId: Int,
        notice: NoticeUpdateRequestDto
    ): UseCase<Boolean>

    suspend fun deleteNotice(lectureId: Int, noticeId: Int): UseCase<Boolean>

    suspend fun getAssignmentList(lectureId: Int): UseCase<List<Assignment>>

    suspend fun postAssignment(
        lectureId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part? = null
    ): UseCase<Boolean>

    suspend fun putAssignment(
        lectureId: Int,
        assignmentId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part? = null
    ): UseCase<Boolean>

    suspend fun deleteAssignment(lectureId: Int, assignmentId: Int): UseCase<Boolean>

    suspend fun getMyAssignmentSubmitInfo(lectureId: Int, assignmentId: Int): UseCase<Submit>

    suspend fun getSubmitAssignmentList(lectureId: Int, assignmentId: Int): UseCase<List<Submit>>

    suspend fun getAttendanceList(lectureId: Int): UseCase<List<AttendanceStudent>>

    suspend fun postAttendanceList(
        lectureId: Int,
        dto: AttendanceUpdateRequestDto
    ): UseCase<Boolean>

    suspend fun getMyAttendance(lectureId: Int): UseCase<AttendanceStudent>

    suspend fun getStudentOwnGrade(lectureId: Int): UseCase<GradeStudent>

    suspend fun getStudentGrade(lectureId: Int, studentId: Int): UseCase<GradeStudent>

    suspend fun postStudentGrade(lectureId: Int, studentId: Int, grade: String): UseCase<Boolean>

    suspend fun getStudentGradeList(lectureId: Int): UseCase<List<GradeStudent>>

    suspend fun getGradeRatio(lectureId: Int): UseCase<GradeRatio>

    suspend fun postGradeRatio(lectureId: Int, gradeRatio: GradeRatio): UseCase<Boolean>
}