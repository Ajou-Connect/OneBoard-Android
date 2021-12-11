@file:Suppress("NonAsciiCharacters")

package kr.khs.oneboard

import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.data.request.AttendanceUpdateRequestDto
import kr.khs.oneboard.utils.toDayOfWeekInt
import kr.khs.oneboard.utils.toUpdateRequestDTO
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class UtilTest {
    @Test
    fun `요일 문자를 Calendar Int 로 변환하는 테스트`() {
        assertEquals("일".toDayOfWeekInt(), Calendar.SUNDAY)

        assertEquals("월".toDayOfWeekInt(), Calendar.MONDAY)

        assertEquals("화".toDayOfWeekInt(), Calendar.TUESDAY)

        assertEquals("수".toDayOfWeekInt(), Calendar.WEDNESDAY)

        assertEquals("목".toDayOfWeekInt(), Calendar.THURSDAY)

        assertEquals("금".toDayOfWeekInt(), Calendar.FRIDAY)

        assertEquals("토".toDayOfWeekInt(), Calendar.SATURDAY)

        assertEquals("잘못된 문자열".toDayOfWeekInt(), 0)
    }

    @Test
    fun `List AttendanceStudent transformation to AttendanceUpdateRequestDto`() {
        val studentSize = 5
        val lessonSize = 10

        val list = (0 until studentSize).map { studentIdx ->
            AttendanceStudent(
                studentId = studentIdx,
                studentNumber = "$studentIdx$studentIdx",
                studentName = "${studentIdx}번쨰 사람",
                attendanceList = (0 until lessonSize).map { lessonIdx ->
                    AttendanceLesson(
                        lessonId = lessonIdx,
                        lessonDate = "$lessonIdx",
                        lessonIdx % 3
                    )
                }
            )
        }

        val answer = AttendanceUpdateRequestDto(
            (0 until studentSize * lessonSize).map { idx ->
                AttendanceUpdateRequestDto.AttendanceUpdate(
                    studentId = idx / lessonSize,
                    lessonId = idx % lessonSize,
                    status = (idx % lessonSize) % 3,
                )
            }
        )

        assertEquals(
            list.toUpdateRequestDTO(), answer
        )
    }
}