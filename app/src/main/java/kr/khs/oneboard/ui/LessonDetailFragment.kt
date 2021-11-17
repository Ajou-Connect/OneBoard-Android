package kr.khs.oneboard.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class LessonDetailFragment : BaseFragment<FragmentLessonDetailBinding, LessonDetailViewModel>() {
    companion object {
        const val REQUEST_PERMISSION_CODE = 1010
    }

    override val viewModel: LessonDetailViewModel by viewModels()

    @Inject
    lateinit var zoom: ZoomVideoSDK

    @Inject
    lateinit var listener: ZoomVideoSDKDelegate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonDetailBinding = FragmentLessonDetailBinding.inflate(inflater, container, false)

    override fun init() {
        getSafeArgs()

        initViews()
        zoom.addListener(listener)
    }

    private fun initViews() {
        val lesson = viewModel.getLesson()
        binding.lessonDetailTV.text = lesson.title

        // TODO: 2021/11/07 추후에는 타입에 따라서 다른 화면 보여지도록. 현재는 줌 테스트
        when (lesson.type) {
            TYPE_FACE_TO_FACE -> {
            }
            TYPE_NON_FACE_TO_FACE -> {
            }
            TYPE_RECORDING -> {
            }
        }

        binding.lessonDetailBtn.setOnClickListener {
            if (UserInfoUtil.type == TYPE_PROFESSOR) {
                createOrJoinSession()
            }
        }
    }

    private fun createOrJoinSession() {
        if (checkPermission().not())
            return

        val sessionContext = ZoomVideoSDKSessionContext().apply {
            // TODO: 2021/11/13 change session name, user name
            sessionName = "mysession_oneboard"
            userName = "ricky"
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
            //                    Timber.tag("Session").d("hostname : $sessionHostName")

            startActivity(
                Intent(requireContext(), SessionActivity::class.java).apply {
                    putExtra("name", mySelf.userName)
                    putExtra("sessionName", sessionName)
                    putExtra("renderType", BaseSessionActivity.RENDER_TYPE_ZOOMRENDERER)
                }
            )
        } ?: run {
            ToastUtil.shortToast(requireContext(), "세션 생성에 실패했습니다.")
        }
    }

    private fun getSafeArgs() {
        arguments?.let {
            it.getParcelable<Lesson>("lesson")?.let { lesson ->
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
        zoom.removeListener(listener)
    }
}