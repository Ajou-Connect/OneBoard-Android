package kr.khs.oneboard.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.databinding.FragmentLessonDetailBinding
import kr.khs.oneboard.utils.API_URL
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_RECORDING
import kr.khs.oneboard.viewmodels.LessonDetailViewModel
import timber.log.Timber

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
                "대면 강의, 강의실 : ${item.room ?: "미정"}"
            }
            TYPE_NON_FACE_TO_FACE -> {
                binding.lessonDetailLessonBtn.text = "수업 입장"
                "비대면 강의"
            }
            TYPE_RECORDING -> {
                binding.lessonDetailLessonBtn.text = "녹화 강의 시청"
                "녹화강의 : ${item.videoUrl ?: "아직 영상이 올라오지 않았습니다."}"
            }
            else -> ""
        }

        if (item.noteUrl == null) {
            binding.lessonDetailWebView.visibility = View.GONE
            binding.lessonDetailNoteDownloadBtn.visibility = View.GONE
            binding.lessonDetailPlanUrl.text = "강의노트가 아직 등록되지 않았습니다."
            return
        }

        val url =
            "https://docs.google.com/gview?embedded=true&url=${API_URL}lecture/${parentViewModel.getLecture().id}/lesson/${item.id}/note"

        Timber.tag("NoteUrl").d(url)

        with(binding.lessonDetailWebView) {
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
            "${API_URL}lecture/${parentViewModel.getLecture().id}/lesson/${item.id}/note"
        Timber.tag("NoteUrl").d(downloadUrl)

        binding.lessonDetailNoteDownloadBtn.setOnClickListener {
            val fileName = "${item.title} 강의노트.pdf"
            fileDownload(downloadUrl, fileName)
        }
    }

    private fun fileDownload(downloadUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("$fileName 다운로드 ")
            .setDescription("강의노트 다운로드")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        (requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
            request
        )
    }
}