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
    fun provideListener(): ZoomVideoSDKDelegate = object : ZoomVideoSDKDelegate {
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
            p0: ZoomVideoSDKUserHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserJoin()")
        }

        override fun onUserLeave(
            p0: ZoomVideoSDKUserHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserLeave()")
        }

        override fun onUserVideoStatusChanged(
            p0: ZoomVideoSDKVideoHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserVideoStatusChanged()")
        }

        override fun onUserAudioStatusChanged(
            p0: ZoomVideoSDKAudioHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserAudioStatusChanged()")
        }

        override fun onUserShareStatusChanged(
            p0: ZoomVideoSDKShareHelper?,
            p1: ZoomVideoSDKUser?,
            p2: ZoomVideoSDKShareStatus?
        ) {
            Timber.tag("ZoomListener").d("onUserShareStatusChanged()")
        }

        override fun onLiveStreamStatusChanged(
            p0: ZoomVideoSDKLiveStreamHelper?,
            p1: ZoomVideoSDKLiveStreamStatus?
        ) {
            Timber.tag("ZoomListener").d("onLiveStreamStatusChanged()")
        }

        override fun onChatNewMessageNotify(
            p0: ZoomVideoSDKChatHelper?,
            p1: ZoomVideoSDKChatMessage?
        ) {
            Timber.tag("ZoomListener").d("onChatNewMessageNotify()")
        }

        override fun onUserHostChanged(p0: ZoomVideoSDKUserHelper?, p1: ZoomVideoSDKUser?) {
            Timber.tag("ZoomListener").d("onUserHostChanged()")
        }

        override fun onUserManagerChanged(p0: ZoomVideoSDKUser?) {
            Timber.tag("ZoomListener").d("onUserManagerChanged()")
        }

        override fun onUserNameChanged(p0: ZoomVideoSDKUser?) {
            Timber.tag("ZoomListener").d("onUserNameChanged()")
        }

        override fun onUserActiveAudioChanged(
            p0: ZoomVideoSDKAudioHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
            Timber.tag("ZoomListener").d("onUserActiveAudioChanged()")
        }

        override fun onSessionNeedPassword(p0: ZoomVideoSDKPasswordHandler?) {
            Timber.tag("ZoomListener").d("onSessionNeedPassword()")
        }

        override fun onSessionPasswordWrong(p0: ZoomVideoSDKPasswordHandler?) {
            Timber.tag("ZoomListener").d("onSessionPasswordWrong()")
        }

        override fun onMixedAudioRawDataReceived(p0: ZoomVideoSDKAudioRawData?) {
            Timber.tag("ZoomListener").d("onMixedAudioRawDataReceived()")
        }

        override fun onOneWayAudioRawDataReceived(
            p0: ZoomVideoSDKAudioRawData?,
            p1: ZoomVideoSDKUser?
        ) {
            Timber.tag("ZoomListener").d("onOneWayAudioRawDataReceived()")
        }

        override fun onShareAudioRawDataReceived(p0: ZoomVideoSDKAudioRawData?) {
            Timber.tag("ZoomListener").d("onShareAudioRawDataReceived()")
        }

    }

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