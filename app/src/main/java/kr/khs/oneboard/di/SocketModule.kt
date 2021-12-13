package kr.khs.oneboard.di

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import kr.khs.oneboard.utils.SOCKET_API_URL
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.inject.Named
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@InstallIn(ActivityComponent::class)
@Module
class SocketModule {

    @Provides
    fun provideHostNameVerifier() = HostnameVerifier { _, _ ->
        return@HostnameVerifier true
    }

    @Provides
    fun provideTrustAllCerts() = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

    @Provides
    fun provideSslContext(trustManager: Array<TrustManager>): SSLContext =
        SSLContext.getInstance("TLS").apply {
            init(null, trustManager, null)
        }

    @Named("SocketOkHttp")
    @Provides
    fun provideSocketOkHttpClient(
        hostnameVerifier: HostnameVerifier,
        sslContext: SSLContext,
        trustManager: Array<TrustManager>
    ) = OkHttpClient.Builder()
        .hostnameVerifier(hostnameVerifier)
        .sslSocketFactory(sslContext.socketFactory, trustManager[0] as X509TrustManager)
        .build()

    @Provides
    fun provideSocket(@Named("SocketOkHttp") okhttpClient: OkHttpClient): Socket {
        val options = IO.Options().apply {
            transports = arrayOf(Polling.NAME)
            callFactory = okhttpClient
            webSocketFactory = okhttpClient
        }

        return IO.socket(SOCKET_API_URL, options)
    }
}