package kr.khs.oneboard.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.khs.oneboard.utils.WEB_DOMAIN
import timber.log.Timber
import us.zoom.sdk.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UtilityModule {

    @Provides
    fun provideListener(): ZoomInstantSDKDelegate = object : ZoomInstantSDKDelegate {
        override fun onSessionJoin() {
            Timber.tag("ZoomListener").d("onSessionJoin()")
        }

        override fun onSessionLeave() {
            Timber.tag("ZoomListener").d("onSessionLeave()")
        }

        override fun onError(errorCode: Int) {
            Timber.tag("ZoomListener").d("onError($errorCode)")
        }

        override fun onUserJoin(
            p0: ZoomInstantSDKUserHelper?,
            p1: MutableList<ZoomInstantSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserJoin()")
        }

        override fun onUserLeave(
            p0: ZoomInstantSDKUserHelper?,
            p1: MutableList<ZoomInstantSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserLeave()")
        }

        override fun onUserVideoStatusChanged(
            p0: ZoomInstantSDKVideoHelper?,
            p1: MutableList<ZoomInstantSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserVideoStatusChanged()")
        }

        override fun onUserAudioStatusChanged(
            p0: ZoomInstantSDKAudioHelper?,
            p1: MutableList<ZoomInstantSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserAudioStatusChanged()")
        }

        override fun onUserShareStatusChanged(
            p0: ZoomInstantSDKShareHelper?,
            p1: ZoomInstantSDKUser?,
            p2: ZoomInstantSDKShareStatus?
        ) {
            Timber.tag("ZoomListener").d("onUserShareStatusChanged()")
        }

        override fun onLiveStreamStatusChanged(
            p0: ZoomInstantSDKLiveStreamHelper?,
            p1: ZoomInstantSDKLiveStreamStatus?
        ) {
            Timber.tag("ZoomListener").d("onLiveStreamStatusChanged()")
        }

        override fun onChatNewMessageNotify(
            p0: ZoomInstantSDKChatHelper?,
            p1: ZoomInstantSDKChatMessage?
        ) {
            Timber.tag("ZoomListener").d("onChatNewMessageNotify()")
        }

        override fun onUserHostChanged(p0: ZoomInstantSDKUserHelper?, p1: ZoomInstantSDKUser?) {
            Timber.tag("ZoomListener").d("onUserHostChanged()")
        }

        override fun onUserManagerChanged(p0: ZoomInstantSDKUser?) {
            Timber.tag("ZoomListener").d("onUserManagerChanged()")
        }

        override fun onUserNameChanged(p0: ZoomInstantSDKUser?) {
            Timber.tag("ZoomListener").d("onUserNameChanged()")
        }

        override fun onUserActiveAudioChanged(
            p0: ZoomInstantSDKAudioHelper?,
            p1: MutableList<ZoomInstantSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserActiveAudioChanged()")
        }

        override fun onSessionNeedPassword(p0: ZoomInstantSDKPasswordHandler?) {
            Timber.tag("ZoomListener").d("onSessionNeedPassword()")
        }

        override fun onSessionPasswordWrong(p0: ZoomInstantSDKPasswordHandler?) {
            Timber.tag("ZoomListener").d("onSessionPasswordWrong()")
        }

        override fun onMixedAudioRawDataReceived(p0: ZoomInstantSDKAudioRawData?) {
            Timber.tag("ZoomListener").d("onMixedAudioRawDataReceived()")
        }

        override fun onOneWayAudioRawDataReceived(
            p0: ZoomInstantSDKAudioRawData?,
            p1: ZoomInstantSDKUser?
        ) {
            Timber.tag("ZoomListener").d("onOneWayAudioRawDataReceived()")
        }

    }

    @Singleton
    @Provides
    fun provideZoomSDK(
        @ApplicationContext context: Context,
        listener: ZoomInstantSDKDelegate
    ): ZoomInstantSDK {
        val params = ZoomInstantSDKInitParams().apply {
            domain = WEB_DOMAIN
            logFilePrefix = "OneBoardLogPrefix"
            enableLog = true
            videoRawDataMemoryMode =
                ZoomInstantSDKRawDataMemoryMode.ZoomInstantSDKRawDataMemoryModeHeap
            audioRawDataMemoryMode =
                ZoomInstantSDKRawDataMemoryMode.ZoomInstantSDKRawDataMemoryModeHeap
            shareRawDataMemoryMode =
                ZoomInstantSDKRawDataMemoryMode.ZoomInstantSDKRawDataMemoryModeHeap
        }
        val ret = ZoomInstantSDK.getInstance().initialize(context, params)

        Timber.tag("ZoomInit").d(
            when (ret) {
                ZoomInstantSDKErrors.Errors_Success -> "SUCCESS"
                else -> "ERROR"
            }
        )

        ZoomInstantSDK.getInstance().addListener(listener)
        return ZoomInstantSDK.getInstance()
    }
}