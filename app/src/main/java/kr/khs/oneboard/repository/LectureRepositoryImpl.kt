package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.core.UseCase
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.api.Response
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.utils.SUCCESS
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LectureRepositoryImpl @Inject constructor(
    @Named("withJWT") private val apiService: ApiService
) : LectureRepository {
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

}