package kr.khs.oneboard.utils

import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto.AttendanceUpdate
import kr.khs.oneboard.views.ThreeStateCheckBox

fun List<AttendanceStudent>.toUpdateRequestDTO(): AttendanceUpdateRequestDto {
    val list = mutableListOf<AttendanceUpdate>()

    for (student in this) {
        val studentId = student.studentId
        for (attendance in student.attendanceList) {
            list.add(
                AttendanceUpdate(
                    studentId,
                    attendance.lessonId,
                    attendance.status
                )
            )
        }
    }

    return AttendanceUpdateRequestDto(list)
}

fun List<AttendanceLesson>.countRatio(): String {
    val size = this.size
    var count = 0f
    for (lesson in this) {
        when (lesson.status) {
            ThreeStateCheckBox.STATE_CHECKED -> {
                count += 1f
            }
            ThreeStateCheckBox.STATE_INDETERMINATE -> {
                count += 0.5f
            }
        }
    }

    return String.format("%.1f / %d", count, size)
}