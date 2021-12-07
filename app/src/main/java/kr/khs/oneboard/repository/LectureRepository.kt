package kr.khs.oneboard.repository

import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import okhttp3.MultipartBody

interface LectureRepository {
    suspend fun getDetailLecture(lectureId: Int): NetworkResult<Triple<Notice?, Lesson?, Assignment?>>

    suspend fun getNoticeList(lectureId: Int): NetworkResult<List<Notice>>

    suspend fun postNotice(lectureId: Int, notice: NoticeUpdateRequestDto): NetworkResult<Boolean>

    suspend fun putNotice(
        lectureId: Int,
        noticeId: Int,
        notice: NoticeUpdateRequestDto
    ): NetworkResult<Boolean>

    suspend fun deleteNotice(lectureId: Int, noticeId: Int): NetworkResult<Boolean>

    suspend fun getAssignmentList(lectureId: Int): NetworkResult<List<Assignment>>

    suspend fun postAssignment(
        lectureId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part? = null
    ): NetworkResult<Boolean>

    suspend fun putAssignment(
        lectureId: Int,
        assignmentId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part? = null
    ): NetworkResult<Boolean>

    suspend fun deleteAssignment(lectureId: Int, assignmentId: Int): NetworkResult<Boolean>

    suspend fun getMyAssignmentSubmitInfo(lectureId: Int, assignmentId: Int): NetworkResult<Submit>

    suspend fun getSubmitAssignmentList(
        lectureId: Int,
        assignmentId: Int
    ): NetworkResult<List<Submit>>

    suspend fun getAttendanceList(lectureId: Int): NetworkResult<List<AttendanceStudent>>

    suspend fun postAttendanceList(
        lectureId: Int,
        dto: AttendanceUpdateRequestDto
    ): NetworkResult<Boolean>

    suspend fun getMyAttendance(lectureId: Int): NetworkResult<AttendanceStudent>

    suspend fun getStudentOwnGrade(lectureId: Int): NetworkResult<GradeStudent>

    suspend fun getStudentGrade(lectureId: Int, studentId: Int): NetworkResult<GradeStudent>

    suspend fun postStudentGrade(
        lectureId: Int,
        studentId: Int,
        grade: String
    ): NetworkResult<Boolean>

    suspend fun getStudentGradeList(lectureId: Int): NetworkResult<List<GradeStudent>>

    suspend fun getGradeRatio(lectureId: Int): NetworkResult<GradeRatio>

    suspend fun postGradeRatio(lectureId: Int, gradeRatio: GradeRatio): NetworkResult<Boolean>
}