package kr.khs.oneboard.data

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginResponse(
    val email: String,
    val token: String
)
