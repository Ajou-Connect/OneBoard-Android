package kr.khs.oneboard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.adapters.OnBoardingAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentOnBoardingBinding
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.OnBoardingViewModel

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding, OnBoardingViewModel>() {
    override val viewModel: OnBoardingViewModel by viewModels()

    private val onBoardingAdapter by lazy { OnBoardingAdapter() }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardingBinding = FragmentOnBoardingBinding.inflate(layoutInflater)

    override fun init() {
        initViewPager()

        UserInfoUtil.setOnBoarding(requireContext())

        binding.onboardingSkip.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initViewPager() {
        with(binding.onboardingViewpager) {
            offscreenPageLimit = 1
            adapter = onBoardingAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })

        }

        setupIndicators(onBoardingAdapter.itemCount)
    }

    private fun setupIndicators(count: Int) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 8
            marginEnd = 8
        }

        val indicators = Array(count) {
            ImageView(requireContext()).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_indicator_inactive
                    )
                )
                layoutParams = params
            }
        }

        for (indicator in indicators)
            binding.layoutIndicators.addView(indicator)

        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = binding.layoutIndicators.childCount

        for (i in 0 until childCount) {
            val iv = (binding.layoutIndicators.getChildAt(i)) as ImageView

            iv.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) {
                        R.drawable.bg_indicator_active
                    } else {
                        R.drawable.bg_indicator_inactive
                    }
                )
            )
        }
    }
}