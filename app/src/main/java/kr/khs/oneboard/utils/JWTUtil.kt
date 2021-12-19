package kr.khs.oneboard.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import kr.khs.oneboard.BuildConfig
import timber.log.Timber

private const val EXPIRED_TIME = (3600 * 4)

// String = "name"
fun createJWT(sessionName: String, userName: String): String {
    val iat = (System.currentTimeMillis() / 1000).toInt()
    val exp = iat + EXPIRED_TIME

    val headers = HashMap<String, Any>().apply {
        put("alg", "HS256")
        put("typ", "JWT")
    }
    Timber.tag("JWT").d("headers : $headers")

    val payloads = HashMap<String, Any>().apply {
        putAll(
            arrayOf(
                "app_key" to BuildConfig.SDK_KEY, "version" to 1, "user_identity" to userName,
                "iat" to iat, "exp" to exp, "tpc" to sessionName
            )
        )
    }
    Timber.tag("JWT").d("payloads : $payloads")

    val key = Keys.hmacShaKeyFor(BuildConfig.SDK_SECRET.toByteArray())
    val jwt = Jwts.builder()
        .setHeader(headers)
        .setClaims(payloads)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact()

    Timber.tag("JWT").d(jwt)

    return jwt
}