package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.NetworkResult
import kr.khs.oneboard.data.*
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.GradeUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.utils.SUCCESS
import kr.khs.oneboard.utils.toPlainRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LectureRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService
) : LectureRepository {
    override suspend fun getDetailLecture(lectureId: Int): NetworkResult<Triple<Notice?, Lesson?, Assignment?>> {
        val returnValue: NetworkResult<Triple<Notice?, Lesson?, Assignment?>>
        try {
            withContext(Dispatchers.IO) {
                val notice = async {
                    try {
                        apiService.getNoticeList(lectureId).data[0]
                    } catch (e: Exception) {
                        null
                    }
                }
                val lesson = async {
                    try {
                        apiService.getLessonList(lectureId).data[0]
                    } catch (e: Exception) {
                        null
                    }
                }
                val assignment = async {
                    try {
                        apiService.getAssignmentList(lectureId).data[0]
                    } catch (e: Exception) {
                        null
                    }
                }

                returnValue =
                    NetworkResult.success(
                        Triple(
                            notice.await(),
                            lesson.await(),
                            assignment.await()
                        )
                    )
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getNoticeList(lectureId: Int): NetworkResult<List<Notice>> {
        val response: Response<List<Notice>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getNoticeList(lectureId)
            }
        } catch (e: Exception) {
            Timber.e(e)
            return NetworkResult.error("Error")
        }

        return NetworkResult.success(
            response.data.map { notice ->
                notice.apply {
                    exposeDt = exposeDt.substring(0, exposeDt.length - 3)
                }
            }
        )
    }

    override suspend fun postNotice(
        lectureId: Int,
        notice: NoticeUpdateRequestDto
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postNotice(lectureId, notice)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            Timber.e(e)
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun putNotice(
        lectureId: Int,
        noticeId: Int,
        notice: NoticeUpdateRequestDto
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.putNotice(lectureId, noticeId, notice)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteNotice(
        lectureId: Int,
        noticeId: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.deleteNotice(lectureId, noticeId)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }


    override suspend fun getAssignmentList(lectureId: Int): NetworkResult<List<Assignment>> {
        val response: Response<List<Assignment>>
        try {
            withContext(Dispatchers.IO) {
                response = apiService.getAssignmentList(lectureId)
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }
        return if (response.result == SUCCESS)
            NetworkResult.success(
                response.data.map { assignment ->
                    assignment.apply {
                        startDt = startDt.substring(0, startDt.length - 3)
                        endDt = endDt.substring(0, endDt.length - 3)
                    }
                }
            ) else
            NetworkResult.error("Error")
    }

    override suspend fun postAssignment(
        lectureId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part?
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val map = HashMap<String, RequestBody>()
                map["title"] = assignment.title.toPlainRequestBody()
                map["content"] = assignment.content.toPlainRequestBody()
                map["startDt"] = assignment.startDt.toPlainRequestBody()
                map["endDt"] = assignment.endDt.toPlainRequestBody()
                map["exposeDt"] = assignment.exposeDt.toPlainRequestBody()
                map["score"] = assignment.score.toString().toPlainRequestBody()

                val response = apiService.postAssignment(lectureId, file, map)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            Timber.e(e)
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun putAssignment(
        lectureId: Int,
        assignmentId: Int,
        assignment: AssignmentUpdateRequestDto,
        file: MultipartBody.Part?
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val map = HashMap<String, RequestBody>()
                map["title"] = assignment.title.toPlainRequestBody()
                map["content"] = assignment.content.toPlainRequestBody()
                map["startDt"] = assignment.startDt.toPlainRequestBody()
                map["endDt"] = assignment.endDt.toPlainRequestBody()
                map["exposeDt"] = assignment.exposeDt.toPlainRequestBody()
                map["score"] = assignment.score.toString().toPlainRequestBody()

                val response = apiService.putAssignment(lectureId, assignmentId, file, map)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun deleteAssignment(
        lectureId: Int,
        assignmentId: Int
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.deleteAssignment(lectureId, assignmentId)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    NetworkResult.success(false)
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getMyAssignmentSubmitInfo(
        lectureId: Int,
        assignmentId: Int
    ): NetworkResult<Submit> {
        val returnValue: NetworkResult<Submit>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getMyAssignmentSubmitInfo(lectureId, assignmentId)
                returnValue = if (response.result == SUCCESS) {
                    NetworkResult.success(response.data)
                } else {
                    NetworkResult.error("Error")
                }
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getSubmitAssignmentList(
        lectureId: Int,
        assignmentId: Int
    ): NetworkResult<List<Submit>> {
        val returnValue: NetworkResult<List<Submit>>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getSubmitAssignmentList(lectureId, assignmentId)
                if (response.result == SUCCESS)
                    returnValue = NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            return NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getAttendanceList(lectureId: Int): NetworkResult<List<AttendanceStudent>> {
        var returnValue: NetworkResult<List<AttendanceStudent>>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getAttendanceList(lectureId)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    NetworkResult.success(listOf())
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun postAttendanceList(
        lectureId: Int,
        dto: AttendanceUpdateRequestDto
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.putAttendance(lectureId, dto)
                returnValue = NetworkResult.success(response.result == SUCCESS)
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getMyAttendance(lectureId: Int): NetworkResult<AttendanceStudent> {
        var returnValue: NetworkResult<AttendanceStudent>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getMyAttendanceList(lectureId)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    NetworkResult.error("No Data")
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getStudentOwnGrade(lectureId: Int): NetworkResult<GradeStudent> {
        var returnValue: NetworkResult<GradeStudent>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getStudentOwnGrade(lectureId)
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getStudentGrade(
        lectureId: Int,
        studentId: Int
    ): NetworkResult<GradeStudent> {
        var returnValue: NetworkResult<GradeStudent>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getStudentGrade(lectureId, studentId)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun postStudentGrade(
        lectureId: Int,
        studentId: Int,
        grade: String
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postStudentGrade(
                    lectureId, studentId, GradeUpdateRequestDto(grade)
                )
                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getStudentGradeList(lectureId: Int): NetworkResult<List<GradeStudent>> {
        var returnValue: NetworkResult<List<GradeStudent>>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getGradeList(lectureId)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun getGradeRatio(lectureId: Int): NetworkResult<GradeRatio> {
        var returnValue: NetworkResult<GradeRatio>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getGradeRatio(lectureId)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(response.data)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

    override suspend fun postGradeRatio(
        lectureId: Int,
        gradeRatio: GradeRatio
    ): NetworkResult<Boolean> {
        var returnValue: NetworkResult<Boolean>

        try {
            withContext(Dispatchers.IO) {
                val response = apiService.postGradeRatio(lectureId, gradeRatio)

                returnValue = if (response.result == SUCCESS)
                    NetworkResult.success(true)
                else
                    throw Exception()
            }
        } catch (e: Exception) {
            returnValue = NetworkResult.error("Error")
        }

        return returnValue
    }

}