package kr.khs.oneboard.data

import java.util.*

// list가 1이면 바로 확장되도록
data class AttendanceStudent(
    val studentId: Int,
    val studentNumber: String,
    val studentName: String,
    val attendanceList: List<AttendanceLesson>
) {
    var isExpanded: Boolean = false

    override fun hashCode(): Int {
        return Objects.hash(studentId, attendanceList)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceStudent

        if (studentId != other.studentId) return false
        if (studentNumber != other.studentNumber) return false
        if (studentName != other.studentName) return false
        for (idx in attendanceList.indices)
            if (attendanceList[idx] != other.attendanceList[idx])
                return false

        if (isExpanded != other.isExpanded) return false

        return true
    }
}

data class AttendanceLesson(
    val lessonId: Int,
    val lessonDate: String,
    var status: Int
)

// A 출석, B 지각, F 결석
enum class AttendanceEnum {
    A, B, F
}