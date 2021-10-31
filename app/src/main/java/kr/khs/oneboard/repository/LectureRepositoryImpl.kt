package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.utils.SUCCESS
import javax.inject.Inject
import kotlin.random.Random

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
                            "$it 교수",
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
                                10 * a + b,
                                "20201028 $b",
                                description = "${b}주차 - 목 (16:30~21:00)",
                                check = (0 until 3).random()
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
                            "$it 교수",
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

    override suspend fun getSubmitAssignmentList(assignmentId: Int): List<Submit> {
        val response: Response<List<Submit>>
        withContext(Dispatchers.IO) {
//            response = apiService.getSubmitAssignmentList(assignmentId)
            response = Response(
                SUCCESS,
                (0 until 20)
                    .map {
                        val isScore = Random.nextBoolean()
                        Submit(
                            id = it,
                            assignmentId = it * 10,
                            studentId = it,
                            studentName = "김희승$it",
                            content = "Content $it",
                            createdDT = "2021.10.$it",
                            updatedDT = "$it",
                            fileUrl = if (Random.nextBoolean()) "https://naver.com" else null,
                            score = if (isScore) (0..100).random() else null,
                            feedBack = if (isScore) "Feed Back $it" else null
                        )
                    }
            )
        }

        return response.data
    }

    override suspend fun postAssignmentFeedBack(submit: Submit): Boolean {
        val response: Response<Boolean>
        withContext(Dispatchers.IO) {
//            response = apiService.postAssignmentFeedBack()
            response = Response(SUCCESS, true)
        }
        return response.data
    }

    override suspend fun getGradeRatio(lectureId: Int): HashMap<String, Int> {
        val response: Response<HashMap<String, Int>>
        withContext(Dispatchers.IO) {
//            response = apiService.getGradeRatio(lectureId)
            response = Response(
                SUCCESS,
                hashMapOf("A" to 30, "B" to 70)
            )
        }

        return response.data
    }
}