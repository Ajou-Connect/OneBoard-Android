package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.SUCCESS
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
    val apiService: ApiService
) : LectureRepository {
    override suspend fun getNoticeList(lectureId: Int): List<Notice> {
        val response: Response<List<Notice>>
        withContext(Dispatchers.IO) {
//            response = apiService.getNoticeList(lectureId)
            response = Response(
                SUCCESS,
                (0 until 20)
                    .map {
                        Notice(
                            it,
                            it,
                            "$lectureId - 공지 $it",
                            "내용 $it",
                            "노출 날짜 $it",
                            it.toLong(),
                            it.toLong()
                        )
                    }
                    .toList()
            )
        }
        return response.data
    }

    override suspend fun postNotice(notice: Notice): Boolean {
//        withContext(Dispatchers.IO) {
//            apiService.postNotice()
//        }
        return true
    }

    override suspend fun getAttendanceList(lectureId: Int): List<AttendanceStudent> {
        val response: Response<List<AttendanceStudent>>

        withContext(Dispatchers.IO) {
//            response = apiService.getAttendanceList(lectureId)
            response = Response(SUCCESS, (0 until 20)
                .map { a ->
                    AttendanceStudent(
                        a,
                        "2015209$a".toInt(),
                        "사이버보안학과 $a",
                        "김희승 $a",
                        (0 until 16).map { b ->
                            AttendanceLesson(
                                b,
                                "20201028 $b",
                                description = "${b}주차 - 목 (16:30~21:00)",
                                check = b % 2 == 0
                            )
                        },
                        false
                    )
                })
        }

        return response.data
    }

    override suspend fun postAttendanceList(list: List<AttendanceStudent>): Boolean {
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.postAttendance()
            response = Response(SUCCESS, true)
        }
        return response.data
    }

    override suspend fun getAssignmentList(lectureId: Int): List<Assignment> {
        val response: Response<List<Assignment>>
        withContext(Dispatchers.IO) {
//            response = apiService.getAssignmentList(lectureId)
            response = Response(
                SUCCESS,
                (0 until 20)
                    .map {
                        Assignment(
                            it,
                            it,
                            "$lectureId - 과제 $it",
                            "내용 $it",
                            "노출 날짜 $it",
                            it.toLong(),
                            it.toLong(),
                            "fileUrl",
                            "시작 날짜 $it",
                            "마감 날짜 $it"
                        )
                    }
                    .toList()
            )
        }
        return response.data
    }

    override suspend fun postAssignment(assignment: Assignment): Boolean {
//        withContext(Dispatchers.IO) {
//            apiService.postAssignment()
//        }
        return true
    }
}