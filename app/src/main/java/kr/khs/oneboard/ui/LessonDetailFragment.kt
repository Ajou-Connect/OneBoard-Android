package kr.khs.oneboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.databinding.FragmentLessonDetailBinding
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_RECORDING
import kr.khs.oneboard.viewmodels.LessonDetailViewModel

@AndroidEntryPoint
class LessonDetailFragment : BaseFragment<FragmentLessonDetailBinding, LessonDetailViewModel>() {
    private lateinit var item: Lesson
    override val viewModel: LessonDetailViewModel by viewModels()

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonDetailBinding = FragmentLessonDetailBinding.inflate(layoutInflater)

    override fun init() {
        initData()
        initView()
    }

    private fun initData() {
        arguments?.let {
            it.getParcelable<Lesson>("item")?.let { item ->
                this.item = item
            } ?: goBackWhenError()
        } ?: goBackWhenError()
    }

    private fun initView() {
        binding.lessonDetailTitle.text = item.title
        binding.lessonDetailDate.text = item.date
        binding.lessonDetailInfo.text = when (item.type) {
            TYPE_FACE_TO_FACE -> {
                binding.lessonDetailLessonBtn.visibility = View.INVISIBLE
                "대면 강의, 강의실 : ${item.room}"
            }
            TYPE_NON_FACE_TO_FACE -> {
                binding.lessonDetailLessonBtn.text = "수업 입장"
                "비대면 강의"
            }
            TYPE_RECORDING -> {
                binding.lessonDetailLessonBtn.text = "녹화 강의 시청"
                "녹화강의 : ${item.videoUrl}"
            }
            else -> ""
        }

        with(binding.lessonDetailWebView) {
            val url =
                "https://docs.google.com/gview?embedded=true&url=http://115.85.182.194:8080/lecture/${parentViewModel.getLecture().id}/lesson/${item.id}"
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
    }
}