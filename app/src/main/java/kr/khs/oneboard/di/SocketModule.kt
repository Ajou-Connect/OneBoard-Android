package kr.khs.oneboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.socket.client.IO
import io.socket.client.Socket
import kr.khs.oneboard.utils.SOCKET_API_URL

@InstallIn(ActivityComponent::class)
@Module
class SocketModule {
    @Provides
    fun provideSocket(): Socket {
        return IO.socket(SOCKET_API_URL)
    }
}