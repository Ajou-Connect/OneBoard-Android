package kr.khs.oneboard.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.rawdata.RawDataRenderer
import kr.khs.oneboard.core.zoom.AudioRawDataUtil
import kr.khs.oneboard.core.zoom.BaseSessionActivity
import kr.khs.oneboard.core.zoom.NotificationService
import kr.khs.oneboard.utils.ToastUtil
import timber.log.Timber
import us.zoom.sdk.*
import kotlin.random.Random

@AndroidEntryPoint
class SessionActivity : BaseSessionActivity() {
    private lateinit var audioRawDataUtil: AudioRawDataUtil
    private lateinit var zoomCanvas: ZoomVideoSDKVideoView
    private lateinit var rawDataRenderer: RawDataRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioRawDataUtil = AudioRawDataUtil(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    override fun onSessionJoin() {
        super.onSessionJoin()
        startMeetingService()
    }

    private fun startMeetingService() {
        val intent = Intent(
            this, NotificationService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopMeetingService() {
        stopService(
            Intent(this, NotificationService::class.java)
        )
    }

    override fun onSessionLeave() {
        super.onSessionLeave()
        audioRawDataUtil.unSubscribe()
        shareToolbar?.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMeetingService()
        handler.removeCallbacks(runnable)
    }

    override fun initView() {
        super.initView()

        binding.bigVideoContain.setOnClickListener(onEmptyContentClick)
        binding.chatList.setOnClickListener(onEmptyContentClick)
    }

    private val onEmptyContentClick = object : View.OnClickListener {
        override fun onClick(v: View) {
            if (zoom.isInSession.not())
                return

            Timber.tag("onEmptyContentClick")
                .d("${actionBarBinding.actionBar.visibility == View.VISIBLE}")
            val isShow = actionBarBinding.actionBar.visibility == View.VISIBLE
            toggleView(isShow.not())
        }
    }

    private fun changeResolution() {
        if (renderType == RENDER_TYPE_OPENGLES) {
            var resolution = Random.nextInt(3)
            resolution++
            if (resolution > ZoomVideoSDKVideoResolution.VideoResolution_360P.value) {
                resolution = 0
            }

            val size = ZoomVideoSDKVideoResolution.fromValue(resolution)

            Timber.d("changeResolution : $size")

            if (currentShareUser == null && mActiveUser != null) {
                mActiveUser!!.videoPipe.subscribe(size, rawDataRenderer)
            }
        }
    }

    override fun onItemClick() {
        super.onItemClick()
        if (zoom.isInSession.not())
            return

        val isShow = actionBarBinding.actionBar.visibility == View.VISIBLE
        toggleView(isShow.not())
    }

    override fun toggleView(show: Boolean) {
        if (show.not()) {
            if (binding.chatInputLayout.isKeyBoardShow()) {
                binding.chatInputLayout.dismissChat(true)
                return
            }
        }

        actionBarBinding.actionBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.chatList.visibility = if (show) View.VISIBLE else View.GONE
        Timber.tag("toggleView").d(
            """
            Show : $show
            ActionBar visibility : ${actionBarBinding.actionBar.visibility}
        """.trimIndent()
        )
    }

    override fun initMeeting() {
        zoom.addListener(this)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )

        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            zoomCanvas = ZoomVideoSDKVideoView(this, renderWithSurfaceView.not())
            binding.bigVideoContain.addView(zoomCanvas, 0, params)
        } else {
            rawDataRenderer = RawDataRenderer(this)
            binding.bigVideoContain.addView(rawDataRenderer, 0, params)
        }

        val mySelf = zoom.session.mySelf
        subscribeVideoByUser(mySelf)
        refreshFps()
    }

    private val runnable = Runnable {
        mActiveUser?.let { activeUser ->
            updateFps(
                if (activeUser == currentShareUser)
                    activeUser.shareStatisticInfo
                else
                    activeUser.videoStatisticInfo
            )
            Timber.tag("FpsRunnable").d("updateFps, ${textFps.visibility == View.VISIBLE}")
        }
        Timber.tag("FpsRunnable").d("runnable!")
    }

    private fun refreshFps() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 500)
    }

    override fun onClickSwitchShare(view: View) {
        currentShareUser?.let { currentShareUser ->
            updateVideoAvatar(true)
            subscribeShareByUser(currentShareUser)
            selectAndScrollToUser(mActiveUser)
        }
    }

    override fun unSubscribe() {
        currentShareUser?.let { currentShareUser ->
            if (renderType == RENDER_TYPE_ZOOMRENDERER) {
                currentShareUser.videoCanvas.unSubscribe(zoomCanvas)
                currentShareUser.shareCanvas.unSubscribe(zoomCanvas)
            } else {
                currentShareUser.videoPipe.unSubscribe(rawDataRenderer)
            }
        }

        mActiveUser?.let { mActiveUser ->
            if (renderType == RENDER_TYPE_ZOOMRENDERER) {
                mActiveUser.videoCanvas.unSubscribe(zoomCanvas)
                mActiveUser.shareCanvas.unSubscribe(zoomCanvas)
            } else {
                mActiveUser.videoPipe.unSubscribe(rawDataRenderer)
            }
        }
    }

    override fun subscribeVideoByUser(user: ZoomVideoSDKUser) {
        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            var aspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_LetterBox
            if (zoom.isInSession)
                aspect = ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
            currentShareUser?.shareCanvas?.unSubscribe(zoomCanvas)

            user.videoCanvas.unSubscribe(zoomCanvas)
            val ret = user.videoCanvas.subscribe(zoomCanvas, aspect)
            if (ret != ZoomVideoSDKErrors.Errors_Success) {
                ToastUtil.shortToast(this, "subscribe error : $ret")
            }
        } else {
            if (zoom.isInSession) {
                rawDataRenderer.setVideoAspectModel(RawDataRenderer.VideoAspect_Original)
            } else {
                rawDataRenderer.setVideoAspectModel(RawDataRenderer.VideoAspect_Full_Filled)
            }

            currentShareUser?.sharePipe?.unSubscribe(rawDataRenderer)
            user.videoPipe.unSubscribe(rawDataRenderer)

            val ret = user.videoPipe.subscribe(
                ZoomVideoSDKVideoResolution.VideoResolution_360P,
                rawDataRenderer
            )
            if (ret != ZoomVideoSDKErrors.Errors_Success) {
                ToastUtil.shortToast(this, "subscribe error : $ret")
            }
        }
        mActiveUser = user

        user.videoStatus?.let {
            updateVideoAvatar(user.videoStatus.isOn)
        }

        currentShareUser?.let {
            binding.btnViewShare.visibility = View.VISIBLE
        } ?: run { binding.btnViewShare.visibility = View.GONE }
    }

    override fun subscribeShareByUser(user: ZoomVideoSDKUser) {
        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            mActiveUser?.let { mActiveUser ->
                mActiveUser.videoCanvas.unSubscribe(zoomCanvas)
                mActiveUser.shareCanvas.unSubscribe(zoomCanvas)
            }
            user.shareCanvas.subscribe(
                zoomCanvas,
                ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_Original
            )
        } else {
            rawDataRenderer.setVideoAspectModel(RawDataRenderer.VideoAspect_Original)
            rawDataRenderer.subscribe(user, ZoomVideoSDKVideoResolution.VideoResolution_720P, true)
        }
        mActiveUser = user
        binding.btnViewShare.visibility = View.GONE
    }

    private fun updateVideoAvatar(isOn: Boolean) {
        if (isOn) {
            binding.videoOffTips.visibility = View.GONE
        } else {
            binding.videoOffTips.visibility = View.VISIBLE
            textFps.visibility = View.GONE
            binding.videoOffTips.setImageResource(R.drawable.zm_conf_no_avatar)
        }
    }

    override fun onUserLeave(
        userHelper: ZoomVideoSDKUserHelper?,
        userList: MutableList<ZoomVideoSDKUser>
    ) {
        super.onUserLeave(userHelper, userList)
        if (mActiveUser == null || userList.contains(mActiveUser)) {
            subscribeVideoByUser(session.mySelf)
            selectAndScrollToUser(session.mySelf)
        }
    }

    override fun onUserVideoStatusChanged(
        videoHelper: ZoomVideoSDKVideoHelper?,
        userList: MutableList<ZoomVideoSDKUser>
    ) {
        super.onUserVideoStatusChanged(videoHelper, userList)

        mActiveUser?.let { activeUser ->
            if (userList.contains(activeUser))
                updateVideoAvatar(activeUser.videoStatus.isOn)
        }
    }

    override fun onStartShareView() {
        super.onStartShareView()
        if (renderType == RENDER_TYPE_ZOOMRENDERER) {
            mActiveUser?.videoCanvas?.unSubscribe(zoomCanvas)
        } else {
            rawDataRenderer.unSubscribe()
        }

        userVideoAdapter.clear(false)
    }

    override fun onUserShareStatusChanged(
        shareHelper: ZoomVideoSDKShareHelper?,
        userInfo: ZoomVideoSDKUser,
        status: ZoomVideoSDKShareStatus
    ) {
        super.onUserShareStatusChanged(shareHelper, userInfo, status)

        if (status == ZoomVideoSDKShareStatus.ZoomVideoSDKShareStatus_Start) {
            if (userInfo != session.mySelf) {
                subscribeShareByUser(userInfo)
                updateVideoAvatar(true)
                selectAndScrollToUser(userInfo)
            } else {
                if (zoom.shareHelper.isScreenSharingOut.not()) {
                    unSubscribe()
                    userVideoAdapter.clear(false)
                }
            }
        } else if (status == ZoomVideoSDKShareStatus.ZoomVideoSDKShareStatus_Stop) {
            currentShareUser = null
            subscribeVideoByUser(userInfo)

            if (userVideoAdapter.itemCount == 0)
                userVideoAdapter.addAll()

            selectAndScrollToUser(userInfo)
        }
    }

    override fun onBackPressed() {}
}