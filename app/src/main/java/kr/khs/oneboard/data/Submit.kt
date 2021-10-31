package kr.khs.oneboard.data

import com.squareup.moshi.Json

data class Submit(
    val id: Int,
    @Json(name = "assignment_id")
    val assignmentId: Int,
    @Json(name = "student_id")
    val studentId: Int,
    @Json(name = "student_name")
    val studentName: String,
    val content: String,
    @Json(name = "file_url")
    val fileUrl: String? = null,
    var score: Int? = null,
    var feedBack: String? = null,
    @Json(name = "created_dt")
    val createdDT: String,
    @Json(name = "updated_dt")
    val updatedDT: String
)
