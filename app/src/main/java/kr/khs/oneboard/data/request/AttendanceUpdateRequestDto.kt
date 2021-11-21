package kr.khs.oneboard.data.request

data class AttendanceUpdateRequestDto(
    val updateDataList: List<AttendanceUpdate>
) {
    data class AttendanceUpdate(
        val studentId: Int,
        val lessonId: Int,
        val status: Int
    )
}