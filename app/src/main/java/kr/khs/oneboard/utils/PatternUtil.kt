package kr.khs.oneboard.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

object PatternUtil {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm")

    fun checkEmailPattern(email: String): Boolean {
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        return pattern.matcher(email).matches()
    }

    fun convertLongToTime(time: Long): String = sdf.format(time)

    fun convertTimeToLong(time: String): Long = sdf.parse(time).time
}