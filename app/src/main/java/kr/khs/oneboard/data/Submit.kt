package kr.khs.oneboard.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Submit(
    val submitId: Int,
    val assignmentId: Int,
    val assignmentTitle: String,
    val userId: Int,
    val userName: String,
    val studentNumber: String,
    val content: String,
    val fileUrl: String? = null,
    var score: Float? = null,
    var feedback: String? = null,
    val createdDt: String,
    val updatedDt: String
) : Parcelable