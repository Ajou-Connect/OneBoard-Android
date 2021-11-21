package kr.khs.oneboard.utils

import java.util.*

fun getDay(): String = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
    Calendar.SUNDAY -> "일"
    Calendar.MONDAY -> "월"
    Calendar.TUESDAY -> "화"
    Calendar.WEDNESDAY -> "수"
    Calendar.THURSDAY -> "목"
    Calendar.FRIDAY -> "금"
    Calendar.SATURDAY -> "토"
    else -> ""
}

fun String.toDayOfWeekInt(): Int = when (this) {
    "일" -> Calendar.SUNDAY
    "월" -> Calendar.MONDAY
    "화" -> Calendar.TUESDAY
    "수" -> Calendar.WEDNESDAY
    "목" -> Calendar.THURSDAY
    "금" -> Calendar.FRIDAY
    "토" -> Calendar.SATURDAY
    else -> 0
}