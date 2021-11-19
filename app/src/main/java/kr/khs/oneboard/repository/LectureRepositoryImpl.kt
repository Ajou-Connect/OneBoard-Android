package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.utils.SUCCESS
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LectureRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService
) : LectureRepository {
    override suspend fun getDetailLecture(lectureId: Int): UseCase<Lecture> {
        val response: Response<Lecture>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getDetailLecture(lectureId)
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }

        return UseCase.success(response.data)
    }

    override suspend fun getNoticeList(lectureId: Int): UseCase<List<Notice>> {
        val response: Response<List<Notice>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getNoticeList(lectureId)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return UseCase.error("Error")
        }

        return UseCase.success(response.data)
    }

    override suspend fun postNotice(
        lectureId: Int,
        notice: NoticeUpdateRequestDto
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postNotice(lectureId, notice)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            Timber.e(e)
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun putNotice(
        lectureId: Int,
        noticeId: Int,
        notice: NoticeUpdateRequestDto
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.putNotice(lectureId, noticeId, notice)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteNotice(
        lectureId: Int,
        noticeId: Int
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.deleteNotice(lectureId, noticeId)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }


    override suspend fun getAssignmentList(lectureId: Int): UseCase<List<Assignment>> {
        val response: Response<List<Assignment>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getAssignmentList(lectureId)
            }
        } catch (e: Exception) {
            return UseCase.error("Error")
        }
        return if (response.result == SUCCESS) UseCase.success(response.data) else UseCase.error("Error")
    }

    override suspend fun postAssignment(
        lectureId: Int,
        assignment: AssignmentUpdateRequestDto
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postAssignment(lectureId, assignment)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            Timber.e(e)
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun putAssignment(
        lectureId: Int,
        assignmentId: Int,
        assignment: AssignmentUpdateRequestDto
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.putAssignment(lectureId, assignmentId, assignment)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteAssignment(lectureId: Int, assignmentId: Int): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.deleteAssignment(lectureId, assignmentId)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(true)
                else
                    UseCase.success(false)
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun getAttendanceList(lectureId: Int): UseCase<List<AttendanceStudent>> {
        var returnValue: UseCase<List<AttendanceStudent>>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getAttendanceList(lectureId)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(response.data)
                else
                    UseCase.success(listOf())
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun postAttendanceList(
        lectureId: Int,
        dto: AttendanceUpdateRequestDto
    ): UseCase<Boolean> {
        var returnValue: UseCase<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.putAttendance(lectureId, dto)
                returnValue = UseCase.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun getMyAttendance(lectureId: Int): UseCase<AttendanceStudent> {
        var returnValue: UseCase<AttendanceStudent>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getMyAttendanceList(lectureId)
                returnValue = if (response.result == SUCCESS)
                    UseCase.success(response.data)
                else
                    UseCase.error("No Data")
            }
        } catch (e: Exception) {
            returnValue = UseCase.error("Error")
        }

        return returnValue
    }

    override suspend fun getGradeList(lectureId: Int): List<GradeStudent> {
        val response: Response<List<GradeStudent>>
        withContext(Dispatchers.IO) {
//            response = apiService.getGradeList(lectureId)
            response = Response(
                SUCCESS,
                (0 until 20)
                    .map {
                        GradeStudent(
                            lectureId = lectureId,
                            studentId = "201520930$it",
                            studentName = "김희승$it",
                            studentMajor = "사이버보안학과",
                            assignmentList = (0 until 5).map {
                                Pair("과제 $it", (0 until 100).random())
                            }.toList(),
                            score = (1..4).random() + (0..1).random() / 2f
                        )
                    }
            )
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