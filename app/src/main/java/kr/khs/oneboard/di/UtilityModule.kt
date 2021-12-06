package kr.khs.oneboard.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.khs.oneboard.utils.WEB_DOMAIN
import timber.log.Timber
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKErrors
import us.zoom.sdk.ZoomVideoSDKInitParams
import us.zoom.sdk.ZoomVideoSDKRawDataMemoryMode
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UtilityModule {

    @Singleton
    @Provides
    fun provideZoomSDK(
        @ApplicationContext context: Context
    ): ZoomVideoSDK {
        val params = ZoomVideoSDKInitParams().apply {
            domain = WEB_DOMAIN
            logFilePrefix = "OneBoardLogPrefix"
            enableLog = true
            videoRawDataMemoryMode =
                ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap
            audioRawDataMemoryMode =
                ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap
            shareRawDataMemoryMode =
                ZoomVideoSDKRawDataMemoryMode.ZoomVideoSDKRawDataMemoryModeHeap
        }
        val ret = ZoomVideoSDK.getInstance().initialize(context, params)

        Timber.tag("ZoomInit").d(
            when (ret) {
                ZoomVideoSDKErrors.Errors_Success -> "SUCCESS"
                else -> "ERROR"
            }
        )

        return ZoomVideoSDK.getInstance()
    }
}