package kr.khs.oneboard.core.zoom

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.adapters.ChatMsgAdapter
import kr.khs.oneboard.adapters.UserVideoAdapter
import kr.khs.oneboard.databinding.*
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.utils.UserHelper
import kr.khs.oneboard.utils.ZMAdapterOsBugHelper
import kr.khs.oneboard.views.KeyBoardLayout
import timber.log.Timber
import us.zoom.sdk.*
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
open class BaseSessionActivity : AppCompatActivity(), ZoomVideoSDKDelegate, ShareToolbar.Listener,
    UserVideoAdapter.ItemTapListener, ChatMsgAdapter.ItemClickListener,
    KeyBoardLayout.KeyBoardListener {
    companion object {
        const val RENDER_TYPE_ZOOMRENDERER = 0
        const val RENDER_TYPE_OPENGLES = 1

        const val REQUEST_SHARE_SCREEN_PERMISSION = 1001
        const val REQUEST_SYSTEM_ALERT_WINDOW = 1002
        const val REQUEST_SELECT_ORIGINAL_PIC = 1003
    }

    protected val binding: ActivitySessionAsdfBinding by lazy {
        ActivitySessionAsdfBinding.inflate(layoutInflater)
    }

    protected val actionBarBinding: LayoutBottomActionBarBinding by lazy {
        binding.actionBar
    }

    @get:JvmName("getMeetingDisplay")
    protected lateinit var display: Display
    protected lateinit var displayMetrics: DisplayMetrics
    protected lateinit var userVideoAdapter: UserVideoAdapter
    private var mScreenInfoData: Intent? = null

    protected var shareToolbar: ShareToolbar? = null
    protected lateinit var chatMsgAdapter: ChatMsgAdapter

    protected lateinit var myDisplayName: String
    protected lateinit var meetingPwd: String
    protected lateinit var sessionName: String
    protected var renderType by Delegates.notNull<Int>()

    protected val handler = Handler(Looper.getMainLooper())

    protected var isActivityPaused = false

    protected var mActiveUser: ZoomVideoSDKUser? = null
    protected var currentShareUser: ZoomVideoSDKUser? = null

    protected lateinit var textFps: TextView

    @Inject
    protected lateinit var zoom: ZoomVideoSDK
    protected lateinit var session: ZoomVideoSDKSession

    protected var renderWithSurfaceView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (renderWithSurfaceView.not()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)
        display = (getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay
        displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)

        session = zoom.session
        zoom.addListener(this)
        parseIntent()
        initView()
        initMeeting()
        updateSessionInfo()
    }

    override fun onResume() {
        super.onResume()
        if (isActivityPaused) {
            resumeSubscribe()
        }
        isActivityPaused = false
        refreshRotation()
        updateActionBarLayoutParams()
        updateChatLayoutParams()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseIntent()
    }

    override fun onPause() {
        super.onPause()
        isActivityPaused = true
        unSubscribe()
        userVideoAdapter.clear(false)
        Timber.tag("ZoomMeeting").d("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.tag("ZoomMeeting").d("onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        shareToolbar?.destroy()
        if (ZMAdapterOsBugHelper.isNeedListenOverlayPermissionChanged) {
            ZMAdapterOsBugHelper.stopListenOverlayPermissionChange(this)
        }
        zoom.removeListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_SHARE_SCREEN_PERMISSION -> {
                if (resultCode != RESULT_OK) {
                    Timber.d("onActivityResult REQUEST_SHARE_SCREEN_PERMISSION is not okay")
                    return
                }
                startShareScreen(data)
            }
            REQUEST_SYSTEM_ALERT_WINDOW -> {
                if (ZMAdapterOsBugHelper.isNeedListenOverlayPermissionChanged) {
                    ZMAdapterOsBugHelper.stopListenOverlayPermissionChange(this)
                }
                if (!Settings.canDrawOverlays(this) &&
                    (!ZMAdapterOsBugHelper.isNeedListenOverlayPermissionChanged || !ZMAdapterOsBugHelper.ismCanDraw())
                ) {
                    return
                }
                mScreenInfoData?.let { onStartShareScreen(it) }
            }
            REQUEST_SELECT_ORIGINAL_PIC -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val selectedImage = data!!.data
                        if (null != selectedImage) {
                            if (currentShareUser == null) {
                                binding.shareImage.setImageURI(selectedImage)
                                binding.shareViewGroup.visibility = View.VISIBLE
                                val ret = ZoomVideoSDK.getInstance().shareHelper.startShareView(
                                    binding.shareImage
                                )
                                Timber.d("start share $ret")
                                if (ret == ZoomVideoSDKErrors.Errors_Success) {
                                    onStartShareView()
                                } else {
                                    binding.shareImage.setImageBitmap(null)
                                    binding.shareViewGroup.visibility = View.GONE
                                    val isLocked =
                                        zoom.shareHelper.isShareLocked
                                    Toast.makeText(
                                        this,
                                        "Share Fail isLocked=$isLocked ret:$ret",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(this, "Other is sharing", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFpsOrientation()
        refreshRotation()
        updateActionBarLayoutParams()
        updateChatLayoutParams()
        updateSmallVideoLayoutParams()
    }

    private fun resumeSubscribe() {
        currentShareUser?.let {
            subscribeShareByUser(it)
        } ?: run {
            mActiveUser?.let {
                subscribeVideoByUser(it)
            }
        }

        if (zoom.isInSession) {
            val userInfoList = UserHelper.getAllUsers()
            if (userInfoList.isNotEmpty()) {
                userVideoAdapter.onUserJoin(userInfoList)
                selectAndScrollToUser(mActiveUser)
            }
        }
    }

    private fun updateFpsOrientation() {
        textFps.visibility = View.GONE
        textFps = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.textFpsLandscape
        } else {
            binding.textFps
        }

        if (zoom.isInSession)
            textFps.visibility = View.VISIBLE
    }

    private fun updateSmallVideoLayoutParams() {
//        binding.videoListContain.gravity =
//            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
//                Gravity.START or Gravity.CENTER_VERTICAL
//            else
//                Gravity.CENTER
    }

    private fun updateChatLayoutParams() {
        if (chatMsgAdapter.itemCount > 0)
            binding.chatList.scrollToPosition(chatMsgAdapter.itemCount - 1)
    }

    private fun updateActionBarLayoutParams() {
        val params = (actionBarBinding.actionBar.layoutParams) as ConstraintLayout.LayoutParams
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.topMargin = (35 * displayMetrics.scaledDensity).toInt()
            actionBarBinding.actionBarScroll.scrollTo(0, 0)
        } else {
            params.topMargin = 0
        }

        actionBarBinding.actionBar.layoutParams = params
    }

    // TODO: 2021/11/15 add bundle when start activity
    protected fun parseIntent() {
        intent.extras?.let {
            myDisplayName = it.getString("name", "UNKNOWN")
            sessionName = it.getString("sessionName", "UNKNOWN")
            renderType = it.getInt("renderType")
        }
    }

    protected open fun onStartShareView() {

    }

    fun onClickStopShare(view: View) {
        zoom.shareHelper.stopShare()
    }

    override fun onSingleTap(user: ZoomVideoSDKUser) {
        subscribeVideoByUser(user)
    }

    override fun onKeyBoardChange(isShow: Boolean, height: Int, inputHeight: Int) {
        val params = (binding.chatList.layoutParams) as ConstraintLayout.LayoutParams

        if (isShow) {
            params.bottomMargin =
                resources.getDimensionPixelSize(R.dimen.dp_13) + height + inputHeight
        } else {
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.dp_160)
        }
        Timber.tag("onKeyBoardChange").d("""
            isShow: $isShow
            height: $height
            inputHeight: $inputHeight
            bottomMargin: ${params.bottomMargin}
        """.trimIndent())
        binding.chatList.layoutParams = params
        if (chatMsgAdapter.itemCount > 0)
            binding.chatList.scrollToPosition(chatMsgAdapter.itemCount - 1)
    }

    protected fun onStartShareScreen(data: Intent) {
        shareToolbar ?: run { shareToolbar = ShareToolbar(this, this) }

        if (Build.VERSION.SDK_INT >= 29) {
            val hasForegroundNotification =
                NotificationMgr.hasNotification(NotificationMgr.PT_NOTIFICATION_ID)
            if (hasForegroundNotification.not()) {
                startForegroundService(
                    Intent(this, NotificationService::class.java)
                )
            }
        }

        val ret = zoom.shareHelper.startShareScreen(data)
        if (ret == ZoomVideoSDKErrors.Errors_Success) {
            shareToolbar!!.showToolbar()
            showDesktop()
        }
    }

    protected fun showDesktop() {
        val home = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(home)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onClickStopShare() {
        if (zoom.shareHelper.isSharingOut) {
            zoom.shareHelper.stopShare()
            showMeetingActivity()
        }
    }

    private fun showMeetingActivity() {
        startActivity(
            Intent(
                applicationContext, IntegrationActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                action = IntegrationActivity.ACTION_RETURN_TO_CONF
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    protected fun startShareScreen(data: Intent?) {
        data ?: return

        if (ZMAdapterOsBugHelper.isNeedListenOverlayPermissionChanged)
            ZMAdapterOsBugHelper.startListenOverlayPermissionChange(this)
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        mScreenInfoData = data
        startActivityForResult(intent, REQUEST_SYSTEM_ALERT_WINDOW)
    }

    protected fun refreshRotation() {
        val displayRotation = display.rotation
        Timber.d("Rotate Video: $displayRotation")
        zoom.videoHelper.rotateMyVideo(displayRotation)
    }

    protected open fun initMeeting() {

    }

    fun updateFps(statisticInfo: ZoomVideoSDKVideoStatisticInfo) {
        val fps = statisticInfo.fps
        textFps.post {
            if (statisticInfo.width > 0 && statisticInfo.height > 0) {
                textFps.visibility = View.VISIBLE
                val text = "${statisticInfo.width}X${statisticInfo.height} $fps FPS"
                Timber.tag("ZoomUpdateFps").d(text)
                textFps.text = text
            } else {
                textFps.visibility = View.GONE
            }
        }
    }

    protected open fun initView() {
        userVideoAdapter = UserVideoAdapter(this, renderType)
        binding.userVideoList.run {
            setItemViewCacheSize(0)
            layoutManager = LinearLayoutManager(
                this@BaseSessionActivity,
                RecyclerView.HORIZONTAL,
                false
            ).apply {
                isItemPrefetchEnabled = false
            }
            adapter = userVideoAdapter
        }
        textFps = binding.textFps

        chatMsgAdapter = ChatMsgAdapter(this)
        binding.chatList.run {
            layoutManager =
                LinearLayoutManager(this@BaseSessionActivity, RecyclerView.VERTICAL, false)
            adapter = chatMsgAdapter
        }

        binding.chatInputLayout.setKeyBoardListener(this)

        onKeyBoardChange(false, 0, 30)

        val margin = (5 * displayMetrics.scaledDensity).toInt()
        binding.userVideoList.run {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    outRect.set(margin, 0, margin, 0)
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val linearLayoutManager =
                            binding.userVideoList.layoutManager as LinearLayoutManager
                        var view = linearLayoutManager.getChildAt(0)
                        view ?: return

                        var index = linearLayoutManager.findFirstVisibleItemPosition()
                        val left = view.left
                        if (left < 0) {
                            if (-left > view.width / 2) {
                                index += 1
                                if (index == adapter!!.itemCount - 1) {
                                    recyclerView.scrollBy(view.width, 0)
                                } else {
                                    recyclerView.scrollBy(view.width + left + 2 * margin, 0)
                                }
                            } else {
                                recyclerView.scrollBy(left - margin, 0)
                            }
                            if (index == 0) {
                                recyclerView.scrollTo(0, 0)
                            }
                        }
                        view = linearLayoutManager.getChildAt(0)
                        if (null == view) {
                            return
                        }
                        scrollVideoViewForMargin(view)
                    }
                }
            })
        }
    }

    override fun onItemClick() {

    }

    open fun onClickSwitchShare(view: View) {

    }

    // 상단 세션 정보
    fun onClickInfo(view: View) {
        val binding = DialogSessionInfoBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this).apply {
            setView(binding.root)
        }

        var size = UserHelper.getAllUsers().size
        if (size <= 0)
            size = 1
        binding.infoUserSize.text = "$size"
        binding.infoSessionPwd.text = ""
        binding.infoSessionName.text = zoom.session.sessionName

        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    // leave 버튼
    fun onClickEnd(view: View) {
        val userInfo = session.mySelf
        val binding = DialogLeaveAlertBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this).apply {
            setView(binding.root)
        }
            .setCancelable(true)

        val dialog = builder.create()
        binding.btnLeave.setOnClickListener {
            dialog.dismiss()
            releaseResource()
            val ret = ZoomVideoSDK.getInstance().leaveSession(false)
            Timber.d("leaveSession ret = $ret")
        }
        var end = false
        if (null != userInfo && userInfo.isHost) {
            binding.btnEnd.text =
                getString(R.string.leave_end_text)
            end = true
        }
        val endSession = end
        binding.btnEnd.setOnClickListener {
            dialog.dismiss()
            if (endSession) {
                releaseResource()
                val ret = ZoomVideoSDK.getInstance().leaveSession(true)
                Timber.d("leaveSession ret = $ret")
            }
        }
        dialog.show()
    }

    // 리소스 해제
    private fun releaseResource() {
        unSubscribe()
        userVideoAdapter.clear(true)
        actionBarBinding.root.visibility = View.GONE
        binding.tvInput.visibility = View.GONE
    }

    fun onClickVideo(view: View) {
        session.mySelf?.let {
            if (it.videoStatus.isOn)
                zoom.videoHelper.stopVideo()
            else
                zoom.videoHelper.startVideo()
        }
    }

    fun onClickShare(view: View) {
        val sdkShareHelper = zoom.shareHelper
        val isShareLocked = sdkShareHelper.isShareLocked

        if (isShareLocked && session.mySelf.isHost.not()) {
            ToastUtil.shortToast(this, "Share is locked by host")
            return
        }

        currentShareUser?.let { currentShareUser ->
            if (currentShareUser != session.mySelf) {
                ToastUtil.shortToast(this, "Other is sharing")
                return
            }
        }

        if (currentShareUser == session.mySelf) {
            sdkShareHelper.stopShare()
            return
        }
        val binding = DialogShareViewBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this).apply {
            setView(binding.root)
        }
            .setCancelable(true)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)

        binding.groupScreenShare.setOnClickListener {
            dialog.dismiss()
            if (sdkShareHelper.isSharingOut) {
                sdkShareHelper.stopShare()
                shareToolbar?.let {
                    it.destroy()
                }
            } else {
                askScreenSharePermission()
            }
        }
        binding.groupPictureShare.setOnClickListener {
            selectFromGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun selectFromGallery() {
        startActivityForResult(
            Intent()
                .setAction(Intent.ACTION_GET_CONTENT)
                .setType("image/*"),
            REQUEST_SELECT_ORIGINAL_PIC
        )
    }

    protected open fun toggleView(show: Boolean) {

    }

    fun onClickChat(view: View) {
        binding.chatInputLayout.showChat()
        toggleView(true)
    }

    fun onClickAudio(view: View) {
        val zoomSDKUserInfo = session.mySelf ?: return

        if (zoomSDKUserInfo.audioStatus.audioType == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
            zoom.audioHelper.startAudio()
        } else {
            if (zoomSDKUserInfo.audioStatus.isMuted) {
                zoom.audioHelper.unMuteAudio(zoomSDKUserInfo)
            } else {
                zoom.audioHelper.muteAudio(zoomSDKUserInfo)
            }
        }
    }

    fun onClickMoreSpeaker() {
        val speaker = zoom.audioHelper.speakerStatus
        zoom.audioHelper.setSpeaker(speaker.not())
    }

    fun onClickMoreSwitchCamera() {
        val zoomSDKUserInfo = session.mySelf ?: return
        if (zoomSDKUserInfo.videoStatus.isHasVideoDevice && zoomSDKUserInfo.videoStatus.isOn) {
            zoom.videoHelper.switchCamera()
            refreshRotation()
        }
    }

    private fun isSpeakerOn() = zoom.audioHelper.speakerStatus

    @SuppressLint("UseCompatLoadingForDrawables")
    fun onClickMore(view: View) {
        val zoomSDKUserInfo = session.mySelf ?: return

        val binding = DialogMoreActionBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this).apply {
            setView(binding.root)
        }
            .setCancelable(true)

        val dialog = builder.create()
        var hasLast = false
        if (zoomSDKUserInfo.videoStatus.isOn) {
            binding.llSwitchCamera.visibility = View.VISIBLE
            binding.llSwitchCamera.setOnClickListener {
                dialog.dismiss()
                onClickMoreSwitchCamera()
            }

            hasLast = true
        } else {
            binding.llSwitchCamera.visibility = View.GONE
        }
        if (canSwitchAudioSource()) {
            binding.llSpeaker.visibility = View.VISIBLE
            binding.llSpeaker.setOnClickListener {
                dialog.dismiss()
                onClickMoreSpeaker()
            }
            if (hasLast.not()) {
                hasLast = true
                binding.llSpeaker.background =
                    resources.getDrawable(R.drawable.more_action_last_bg, null)
            }
        } else {
            binding.llSpeaker.visibility = View.GONE
        }

        if (hasLast.not())
            return

        if (isSpeakerOn()) {
            binding.tvSpeaker.text = "Turn Off Speaker"
            binding.ivSpeaker.setImageResource(R.drawable.icon_speaker_off)
        } else {
            binding.tvSpeaker.text = "Turn On Speaker"
            binding.ivSpeaker.setImageResource(R.drawable.icon_speaker_on)
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun checkMoreAction() {
        val zoomSDKUserInfo = session.mySelf ?: return

        actionBarBinding.iconMore.visibility =
            if (zoomSDKUserInfo.videoStatus.isOn.not() && canSwitchAudioSource().not())
                View.GONE
            else
                View.VISIBLE
    }

    private fun canSwitchAudioSource(): Boolean = zoom.audioHelper.canSwitchSpeaker()

    protected fun askScreenSharePermission() {
        if (zoom.shareHelper.isSharingOut)
            return

        val mgr = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mgr?.let {
            try {
                startActivityForResult(
                    mgr.createScreenCaptureIntent(),
                    REQUEST_SHARE_SCREEN_PERMISSION
                )
            } catch (e: Exception) {
                Timber.e(e, "askScreenSharePermission failed")
            }
        }
    }

    private fun updateSessionInfo() {
        if (zoom.isInSession) {
            var size = UserHelper.getAllUsers().size
            if (size <= 0)
                size = 1
            binding.textMeetingUserSize.text = "Participants: $size"

            binding.tvInput.visibility = View.VISIBLE
            textFps.visibility = View.VISIBLE
        } else {
            if (binding.chatInputLayout.isKeyBoardShow()) {
                binding.chatInputLayout.dismissChat(true)
                return
            }

            actionBarBinding.root.visibility = View.GONE
            binding.tvInput.visibility = View.GONE
            textFps.visibility = View.GONE
            binding.textMeetingUserSize.text = "Connecting..."
        }

        binding.sessionName.text = session.sessionName
//        binding.meetingLockStatus.setImageResource(R.drawable.unlock)
    }

    protected open fun unSubscribe() {

    }

    override fun onSessionJoin() {
        Timber.d("onSessionJoin()")
        updateSessionInfo()
        updateFpsOrientation()
        actionBarBinding.actionBar.visibility = View.VISIBLE
        if (zoom.shareHelper.isSharingOut)
            zoom.shareHelper.stopShare()

        userVideoAdapter.onUserJoin(UserHelper.getAllUsers())
        refreshUserListAdapter()
        binding.tvInput.visibility = View.VISIBLE
    }

    override fun onSessionLeave() {
        Timber.d("onSessionLeave")
        finish()
    }

    override fun onError(errorCode: Int) {
        ToastUtil.shortToast(this, "onError($errorCode)")
        when (errorCode) {
            ZoomVideoSDKErrors.Errors_Session_Disconnect -> {
                unSubscribe()
                userVideoAdapter.clear(true)
                updateSessionInfo()
                currentShareUser = null
                mActiveUser = null
                chatMsgAdapter.clear()
                binding.chatList.visibility = View.GONE
                binding.btnViewShare.visibility = View.GONE
            }
            ZoomVideoSDKErrors.Errors_Session_Reconncting -> {
                subscribeVideoByUser(session.mySelf)
            }
            else -> {
                zoom.leaveSession(false)
                finish()
            }
        }
    }

    protected open fun subscribeVideoByUser(user: ZoomVideoSDKUser) {

    }

    protected open fun subscribeShareByUser(user: ZoomVideoSDKUser) {

    }

    override fun onUserJoin(
        userHelper: ZoomVideoSDKUserHelper?,
        userList: MutableList<ZoomVideoSDKUser>,
    ) {
        Timber.tag("onUserJoin()")
        updateVideoListLayout()
        if (isActivityPaused.not())
            userVideoAdapter.onUserJoin(userList.toList())

        refreshUserListAdapter()
        updateSessionInfo()
    }

    protected fun selectAndScrollToUser(user: ZoomVideoSDKUser?) {
        user ?: return

        userVideoAdapter.updateSelectedVideoUser(user)
        val index = userVideoAdapter.getIndexByUser(user)
        if (index >= 0) {
            val manager = binding.userVideoList.layoutManager as LinearLayoutManager
            val first = manager.findFirstVisibleItemPosition()
            val last = manager.findLastVisibleItemPosition()

            if (index > last || index < first) {
                binding.userVideoList.scrollToPosition(index)
                userVideoAdapter.notifyDataSetChanged()
            }
        }

        val linearLayoutManager = binding.userVideoList.layoutManager as LinearLayoutManager
        val view = linearLayoutManager.getChildAt(0)

        view?.let {
            scrollVideoViewForMargin(it)
        } ?: run {
            handler.postDelayed({
                scrollVideoViewForMargin(
                    linearLayoutManager.getChildAt(0)
                )
            }, 50)
        }
    }

    private fun scrollVideoViewForMargin(view: View?) {
        view ?: return

        val margin = 5

        if (view.left > margin || view.left <= 0)
            binding.userVideoList.scrollBy(view.left - margin, 0)
    }

    private fun refreshUserListAdapter() {
        if (userVideoAdapter.itemCount > 0) {
            binding.userVideoList.visibility = View.VISIBLE
            userVideoAdapter.getSelectedVideoUser()?.let {
                session.mySelf?.let {
                    selectAndScrollToUser(it)
                }
            }
        }
    }

    private fun updateVideoListLayout() {
        val size = UserHelper.getAllUsers().size
        val params = binding.userVideoList.layoutParams as ConstraintLayout.LayoutParams
        val preWidth = params.width
        var width = LinearLayout.LayoutParams.WRAP_CONTENT
        if (size - 1 >= 3) {
            width = (325 * displayMetrics.scaledDensity).toInt()
        }
        if (width != preWidth) {
            params.width = width
            binding.userVideoList.layoutParams = params
        }
    }

    override fun onUserLeave(
        userHelper: ZoomVideoSDKUserHelper?,
        userList: MutableList<ZoomVideoSDKUser>,
    ) {
        userList ?: return
        updateVideoListLayout()
        Timber.d("onUserLeave ${userList.size}")
        userVideoAdapter.onUserLeave(userList)
        if (userVideoAdapter.itemCount == 0)
            binding.userVideoList.visibility = View.INVISIBLE

        updateSessionInfo()
    }

    override fun onUserVideoStatusChanged(
        videoHelper: ZoomVideoSDKVideoHelper?,
        userList: MutableList<ZoomVideoSDKUser>,
    ) {
        Timber.d("onUserVideoStatusChanged()")

        session.mySelf?.let { zoomSDKUserInfo ->
            actionBarBinding.iconVideo.setImageResource(
                if (zoomSDKUserInfo.videoStatus.isOn) R.drawable.icon_video_off
                else R.drawable.icon_video_on
            )
            if (userList.contains(zoomSDKUserInfo))
                checkMoreAction()
        }

        userVideoAdapter.onUserVideoStatusChanged(userList)
    }

    override fun onUserAudioStatusChanged(
        audioHelper: ZoomVideoSDKAudioHelper?,
        userList: MutableList<ZoomVideoSDKUser>,
    ) {
        Timber.d("onUserAudioStatusChanged")
        session.mySelf?.let { zoomSDKUserInfo ->
            if (userList.contains(zoomSDKUserInfo)) {
                if (zoomSDKUserInfo.audioStatus.audioType == ZoomVideoSDKAudioStatus.ZoomVideoSDKAudioType.ZoomVideoSDKAudioType_None) {
                    actionBarBinding.iconAudio.setImageResource(R.drawable.icon_join_audio)
                } else {
                    actionBarBinding.iconAudio.setImageResource(
                        if (zoomSDKUserInfo.audioStatus.isMuted) R.drawable.icon_unmute
                        else R.drawable.icon_mute
                    )
                }
                checkMoreAction()
            }
        }
    }

    override fun onUserShareStatusChanged(
        shareHelper: ZoomVideoSDKShareHelper?,
        userInfo: ZoomVideoSDKUser,
        status: ZoomVideoSDKShareStatus,
    ) {
        if (status == ZoomVideoSDKShareStatus.ZoomVideoSDKShareStatus_Stop) {
            currentShareUser = userInfo
            if (userInfo == session.mySelf)
                actionBarBinding.iconShare.setImageResource(R.drawable.icon_stop_share)
        } else if (status == ZoomVideoSDKShareStatus.ZoomVideoSDKShareStatus_Stop) {
            currentShareUser = null
            actionBarBinding.iconShare.setImageResource(R.drawable.icon_share)
            binding.shareViewGroup.visibility = View.GONE
            shareToolbar?.destroy()
        }
    }

    override fun onLiveStreamStatusChanged(
        p0: ZoomVideoSDKLiveStreamHelper?,
        p1: ZoomVideoSDKLiveStreamStatus?,
    ) {
        Timber.d("onLiveStreamStatusChanged()")
    }


    override fun onChatNewMessageNotify(
        chatHelper: ZoomVideoSDKChatHelper?,
        messageItem: ZoomVideoSDKChatMessage?,
    ) {
        messageItem ?: return

        chatMsgAdapter.onReceive(messageItem)

        updateChatLayoutParams()
    }

    override fun onUserHostChanged(p0: ZoomVideoSDKUserHelper?, p1: ZoomVideoSDKUser?) {
        Timber.d("onUserHostChanged()")
    }

    override fun onUserManagerChanged(p0: ZoomVideoSDKUser?) {
        Timber.d("onUserManagerChanged()")
    }

    override fun onUserNameChanged(p0: ZoomVideoSDKUser?) {
        Timber.d("onUserNameChanged()")
    }

    override fun onUserActiveAudioChanged(
        audioHelper: ZoomVideoSDKAudioHelper?,
        list: MutableList<ZoomVideoSDKUser>,
    ) {
        userVideoAdapter.onUserActiveAudioChanged(list, binding.userVideoList)
    }

    override fun onSessionNeedPassword(handler: ZoomVideoSDKPasswordHandler?) {
        Timber.d("onSessioNeedPassword()")
        handler?.let { handler ->
            showInputPwdDialog(handler)
        }
    }

    private fun showInputPwdDialog(handler: ZoomVideoSDKPasswordHandler) {
        val binding = DialogSessionInputPwdBinding.inflate(layoutInflater)
        val builder =
            MaterialAlertDialogBuilder(this).apply {
                setView(binding.root)
            }
                .setCancelable(false)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        binding.btnOk.setOnClickListener {
            handler.inputSessionPassword(
                binding.editPwd.text.toString()
            )
            dialog.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            handler.leaveSessionIgnorePassword()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onSessionPasswordWrong(handler: ZoomVideoSDKPasswordHandler?) {
        ToastUtil.shortToast(this, "Password Long")
        handler?.let { handler ->
            showInputPwdDialog(handler)
        }
    }

    override fun onMixedAudioRawDataReceived(p0: ZoomVideoSDKAudioRawData?) {
        Timber.d("onMixedAudioRawDataReceived()")
    }

    override fun onOneWayAudioRawDataReceived(
        p0: ZoomVideoSDKAudioRawData?,
        p1: ZoomVideoSDKUser?,
    ) {
        Timber.d("onOneWayAudioRawDataReceived()")
    }

    override fun onShareAudioRawDataReceived(p0: ZoomVideoSDKAudioRawData?) {
        Timber.d("onShareAudioRawDataReceived")
    }

}

// todo 소켓 통신