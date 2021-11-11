package kr.khs.oneboard.data

data class User(
    val studentNumber: String,
    val name: String,
    val userType: String,
    val email: String,
    val university: String,
    val major: String
)