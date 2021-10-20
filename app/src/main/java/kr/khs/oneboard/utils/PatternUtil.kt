package kr.khs.oneboard.utils

object PatternUtil {
    fun checkEmailPattern(email: String): Boolean {
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        return pattern.matcher(email).matches()
    }
}