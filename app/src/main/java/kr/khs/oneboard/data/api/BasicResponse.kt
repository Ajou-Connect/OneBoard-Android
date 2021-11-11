package kr.khs.oneboard.data.api

abstract class BasicResponse {
    abstract val result: String
}

class BasicResponseImpl(override val result: String) : BasicResponse()