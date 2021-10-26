package kr.khs.oneboard.data.api

data class Response<T : Any>(val result: String, val data: T)