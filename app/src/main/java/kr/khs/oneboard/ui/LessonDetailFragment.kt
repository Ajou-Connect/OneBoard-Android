package kr.khs.oneboard.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.core.zoom.BaseSessionActivity
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.databinding.FragmentLessonDetailBinding
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.LessonDetailViewModel
import timber.log.Timber
import us.zoom.sdk.*
import javax.inject.Inject

@AndroidEntryPoint
class LessonDetailFragment : BaseFragment<FragmentLessonDetailBinding, LessonDetailViewModel>(),
    ZoomVideoSDKDelegate {
    companion object {
        const val REQUEST_PERMISSION_CODE = 1010
    }

    override val viewModel: LessonDetailViewModel by viewModels()

    @Inject
    lateinit var zoom: ZoomVideoSDK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isCreateLesson.observe(viewLifecycleOwner) {
            if (it) {
                createOrJoinSession()
                viewModel.doneCreateLesson()
            }
        }

        viewModel.sessionError.observe(viewLifecycleOwner) {
            if (it != "") {
                DialogUtil.createDialog(
                    context = requireContext(),
                    message = it,
                    positiveText = "확인",
                    positiveAction = { }
                )
                viewModel.setSessionErrorMessage("")
            }
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonDetailBinding = FragmentLessonDetailBinding.inflate(inflater, container, false)

    override fun init() {
        binding.viewTitle.root.text = "수업 상세"
        getSafeArgs()

        initViews()
        zoom.addListener(this)
    }

    private fun initViews() {
        Timber.tag("SessionInfo").d("${viewModel.getLesson()}")

        binding.lessonDetailTitle.text = viewModel.getLesson().title
        binding.lessonDetailDate.text = viewModel.getLesson().date
        binding.lessonDetailInfo.text = when (viewModel.getLesson().type) {
            TYPE_FACE_TO_FACE -> {
                binding.lessonDetailLessonBtn.visibility = View.INVISIBLE
                "대면 강의, 강의실 : ${viewModel.getLesson().room ?: "미정"}"
            }
            TYPE_NON_FACE_TO_FACE -> {
                binding.lessonDetailLessonBtn.text = "수업 입장"
                "비대면 강의"
            }
            TYPE_RECORDING -> {
                binding.lessonDetailLessonBtn.text = "녹화 강의 시청"
                "녹화강의 : ${viewModel.getLesson().videoUrl ?: "아직 영상이 올라오지 않았습니다."}"
            }
            else -> ""
        }

        binding.lessonDetailLessonBtn.setOnClickListener {
            when (viewModel.getLesson().type) {
                TYPE_RECORDING -> {
                }
                TYPE_FACE_TO_FACE -> {
                }
                TYPE_NON_FACE_TO_FACE -> {
                    if (UserInfoUtil.type == TYPE_PROFESSOR) {
                        Timber.tag("SessionInfo").d("type professor")
                        viewModel.createLesson(
                            parentViewModel.getLecture().id,
                            viewModel.getLesson().lessonId
                        )
                    } else {
                        Timber.tag("SessionInfo").d("type student")
                        viewModel.enterLesson(
                            parentViewModel.getLecture().id,
                            viewModel.getLesson().lessonId
                        )
                    }
                }
            }
        }

        if (viewModel.getLesson().noteUrl == null) {
            binding.lessonDetailWebView.visibility = View.GONE
            binding.lessonDetailNoteDownloadBtn.visibility = View.GONE
            binding.lessonDetailPlanUrl.text = "강의노트가 아직 등록되지 않았습니다."
            return
        }

        val url =
            "https://docs.google.com/gview?embedded=true&url=${API_URL}lecture/${parentViewModel.getLecture().id}/lesson/${viewModel.getLesson().lessonId}/note"

        Timber.tag("NoteUrl").d(url)

        with(binding.lessonDetailWebView) {
            val url =
                "https://docs.google.com/gview?embedded=true&url=http://115.85.182.194:8080/lecture/${parentViewModel.getLecture().id}/lesson/${viewModel.getLesson().lessonId}/note"

            webViewClient = WebViewClient() // 클릭 시 새창 안뜨게
            with(this.settings) {
                javaScriptEnabled = true
                setSupportMultipleWindows(false)
                javaScriptCanOpenWindowsAutomatically = false
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(false)
                builtInZoomControls = false
                layoutAlgorithm = android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
            }

            loadUrl(url)
        }

        val downloadUrl =
            "${API_URL}lecture/${parentViewModel.getLecture().id}/lesson/${viewModel.getLesson().lessonId}/note"
        Timber.tag("NoteUrl").d(downloadUrl)

        binding.lessonDetailNoteDownloadBtn.setOnClickListener {
            val fileName = "${viewModel.getLesson().title} 강의노트.pdf"
            fileDownload("$fileName 다운로드 ", "강의노트 다운로드", downloadUrl, fileName)
        }
    }

    private fun createOrJoinSession() {
        if (checkPermission().not())
            return

        val sessionContext = ZoomVideoSDKSessionContext().apply {
            sessionName = viewModel.getLesson().session
            userName = UserInfoUtil.name
            token = createJWT(sessionName, userName)
            audioOption = ZoomVideoSDKAudioOption().apply {
                connect = true
                mute = true
            }
            videoOption = ZoomVideoSDKVideoOption().apply {
                localVideoOn = true
            }
        }

        zoom.joinSession(sessionContext)
        zoom.session?.run {
            Timber.tag("Session").d("name : $sessionName")
            Timber.tag("Session").d("password: $sessionPassword")
            Timber.tag("Session").d("host : $sessionHost")
            Timber.tag("Session").d("mySelf : ${mySelf.userName}")
            Timber.tag("SessionHost").d("host : $sessionHostName")

            startActivity(
                Intent(requireContext(), SessionActivity::class.java).apply {
                    putExtra("name", mySelf.userName)
                    putExtra("sessionName", sessionName)
                    putExtra("sessionDisplayName", viewModel.getLesson().title)
                    putExtra("renderType", BaseSessionActivity.RENDER_TYPE_ZOOMRENDERER)
                    putExtra("lectureId", parentViewModel.getLecture().id)
                    putExtra("lessonId", viewModel.getLesson().lessonId)
                }
            )
        } ?: run {
            ToastUtil.shortToast(requireContext(), "세션 생성에 실패했습니다.")
        }
    }

    private fun getSafeArgs() {
        arguments?.let {
            it.getParcelable<Lesson>("item")?.let { lesson ->
                viewModel.setLesson(lesson)
            } ?: goBackWhenError()
        } ?: goBackWhenError()
    }

    private fun checkPermission(): Boolean {
        Timber.d("Check Permission")
        val cameraPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val recordAudioPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (cameraPermission != permissionGranted || recordAudioPermission != permissionGranted || storagePermission != permissionGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )

            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                val cameraPermission =
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                val recordAudioPermission =
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                    )
                val storagePermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                val permissionGranted = PackageManager.PERMISSION_GRANTED
                if (cameraPermission != permissionGranted && recordAudioPermission != permissionGranted && storagePermission != permissionGranted) {
                    Timber.d("OnPermissionGranted!!")
                    onPermissionGranted()
                }
            }
        }
    }

    private fun onPermissionGranted() {
        createOrJoinSession()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        zoom.removeListener(this)
    }

    override fun onSessionJoin() {
        Timber.tag("ZoomListener").d("onSessionJoin()")

    }

    override fun onSessionLeave() {
        Timber.tag("ZoomListener").d("onSessionLeave()")
    }

    override fun onError(errorCode: Int) {
        Timber.tag("ZoomListener").d("onError($errorCode)")
        ToastUtil.shortToast(requireContext(), "에러가 발생하였습니다.\n다시 시도해주세요.")
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