package kr.khs.oneboard.data.api

data class Response<T : Any>(override val result: String, val data: T) : BasicResponse()