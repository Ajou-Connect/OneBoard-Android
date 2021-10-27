package kr.khs.oneboard.data

import com.squareup.moshi.Json

abstract class LectureBase {
    abstract val id: Int

    @Json(name = "lecture_id")
    abstract val lectureId: Int
    abstract val title: String
    abstract val content: String

    // Todo date time이 어떤 형식으로 들어올 지 테스트 해봐야 함.
    @Json(name = "expose_dt")
    abstract val exposeDT: String

    @Json(name = "created_dt")
    abstract val createDT: Long

    @Json(name = "updated_dt")
    abstract val updateDT: Long
}