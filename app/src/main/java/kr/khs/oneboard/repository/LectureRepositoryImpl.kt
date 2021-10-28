package kr.khs.oneboard.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.data.Assignment
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