package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.databinding.FragmentLessonDetailBinding
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.LessonDetailViewModel
import timber.log.Timber
import us.zoom.sdk.*
import javax.inject.Inject

@AndroidEntryPoint
class LessonDetailFragment : BaseFragment<FragmentLessonDetailBinding, LessonDetailViewModel>() {
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

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonDetailBinding = FragmentLessonDetailBinding.inflate(inflater, container, false)

    override fun init() {
        getSafeArgs()

        initViews()
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

        // 같은 상황
        // https://devforum.zoom.us/t/cannot-join-web-meeting/58071/8
        binding.lessonDetailBtn.setOnClickListener {
            if (UserInfoUtil.type == TYPE_PROFESSOR) {
                val sessionContext = ZoomVideoSDKSessionContext().apply {
                    sessionName = "mysession_oneboard"
                    userName = "ricky"
                    token = createJWT(sessionName, userName)
                    audioOption = ZoomVideoSDKAudioOption().apply {
                        connect = true
                        mute = true
                    }
                    videoOption = ZoomVideoSDKVideoOption().apply {
                        localVideoOn = false
                    }
                }
                zoom.joinSession(sessionContext)
                zoom.session?.run {
                    Timber.tag("Session").d("name : $sessionName")
                    Timber.tag("Session").d("password: $sessionPassword")
                    Timber.tag("Session").d("host : $sessionHost")
                    Timber.tag("Session").d("mySelf : ${mySelf.userName}")
//                    Timber.tag("Session").d("hostname : $sessionHostName")
                }
            }
        }
    }

    private fun getSafeArgs() {
        arguments?.let {
            it.getParcelable<Lesson>("lesson")?.let { lesson ->
                viewModel.setLesson(lesson)
            } ?: goBackWhenError()
        } ?: goBackWhenError()
    }

}