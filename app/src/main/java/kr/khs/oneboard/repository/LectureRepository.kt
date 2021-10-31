package kr.khs.oneboard.repository

import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.Submit

interface LectureRepository {
    suspend fun getNoticeList(lectureId: Int): List<Notice>

    suspend fun postNotice(notice: Notice): Boolean

    suspend fun getAttendanceList(lectureId: Int): List<AttendanceStudent>

    suspend fun postAttendanceList(list: List<AttendanceStudent>): Boolean

    suspend fun getAssignmentList(lectureId: Int): List<Assignment>

    suspend fun postAssignment(assignment: Assignment): Boolean

    suspend fun getSubmitAssignmentList(assignmentId: Int): List<Submit>

    suspend fun postAssignmentFeedBack(submit: Submit): Boolean

    suspend fun getGradeRatio(lectureId: Int): HashMap<String, Int>
}