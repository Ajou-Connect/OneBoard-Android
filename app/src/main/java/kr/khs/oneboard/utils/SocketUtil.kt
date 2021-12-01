package kr.khs.oneboard.utils

import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber

import java.net.URISyntaxException

private lateinit var socket: Socket

fun createSocket(): Socket {
    if (::socket.isInitialized)
        return socket

    Timber.tag("Socket").d("Create Socket!!")

    try {
        socket = IO.socket(SOCKET_API_URL)
    } catch (e: URISyntaxException) {
        Timber.e(e)
    } catch (e: Exception) {
        Timber.e(e)
    }

    return socket
}