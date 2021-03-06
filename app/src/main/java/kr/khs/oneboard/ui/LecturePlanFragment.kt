package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentLecturePlanBinding
import kr.khs.oneboard.utils.API_URL
import kr.khs.oneboard.utils.createOnBoardingDialog
import kr.khs.oneboard.utils.getOnBoardingSpf
import kr.khs.oneboard.viewmodels.LecturePlanViewModel
import timber.log.Timber

@AndroidEntryPoint
class LecturePlanFragment : BaseFragment<FragmentLecturePlanBinding, LecturePlanViewModel>() {
    override val viewModel: LecturePlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLecturePlanBinding = FragmentLecturePlanBinding.inflate(inflater, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        binding.viewTitle.root.text = "강의 계획서"
        val url =
            "https://docs.google.com/gview?embedded=true&url=${API_URL}lecture/${parentViewModel.getLecture().id}/plan"
        Timber.tag("PlanUrl").d(url)

        if (url == "") goBackWhenError()

        with(binding.lecturePlanWebView) {
            webViewClient = WebViewClient() // 클릭 시 새창 안뜨게
            with(this.settings) {
                javaScriptEnabled = true
                setSupportMultipleWindows(false)
                javaScriptCanOpenWindowsAutomatically = false
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(false)
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
            }

            loadUrl(url)
        }
    }

    override fun initOnBoarding() {
        if (getOnBoardingSpf(this.javaClass.simpleName).not()) {
            createOnBoardingDialog()
        }
    }
}