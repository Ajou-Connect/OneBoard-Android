package kr.khs.oneboard.utils

import android.content.Context
import android.content.SharedPreferences

object UserInfoUtil {
    private const val SPF_NAME = "OneBoard"
    private const val USER_TOKEN = "UserToken"
    private const val ON_BOARDING = "OnBoarding"

    var email: String = ""
        get() {
            if (field == "")
                throw Exception("Invalid Email")
            return field
        }
    var name: String = ""
        get() {
            if (field == "")
                throw Exception("Invalid Name")
            return field
        }
    var type: Boolean = TYPE_PROFESSOR
    var univ: String = ""
        get() {
            if (field == "")
                throw Exception("Invalid Type")
            return field
        }
    var major: String = ""
        get() {
            if (field == "")
                throw Exception("Invalid Major")
            return field
        }
    var studentId: String = ""
        get() {
            if (field == "")
                throw Exception("Invalid Student Id")
            return field
        }

    private fun getSPF(context: Context): SharedPreferences =
        context.getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)

    fun setToken(context: Context, token: String) {
        val editor = getSPF(context).edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(context: Context) = getSPF(context).getString(USER_TOKEN, "") ?: ""

    fun getOnBoarding(context: Context) = getSPF(context).getBoolean(ON_BOARDING, false)

    fun setOnBoarding(context: Context, isReset: Boolean = false) {
        val editor = getSPF(context).edit()
        editor.putBoolean(ON_BOARDING, isReset.not())
        editor.apply()
    }
}