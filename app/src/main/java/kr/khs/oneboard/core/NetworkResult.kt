package kr.khs.oneboard.core

data class NetworkResult<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status { SUCCESS, ERROR }

    companion object {
        fun <T> success(data: T, message: String? = null): NetworkResult<T> {
            return NetworkResult(Status.SUCCESS, data, message)
        }

        fun <T> error(message: String, data: T? = null): NetworkResult<T> {
            return NetworkResult(Status.ERROR, data, message)
        }
    }
}