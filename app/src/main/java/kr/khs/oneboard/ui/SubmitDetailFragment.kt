package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.databinding.FragmentSubmitDetailBinding
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.SubmitDetailViewModel

@AndroidEntryPoint
class SubmitDetailFragment : BaseFragment<FragmentSubmitDetailBinding, SubmitDetailViewModel>() {
    override val viewModel: SubmitDetailViewModel by viewModels()

    lateinit var submit: Submit

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

        viewModel.saveResponse.observe(viewLifecycleOwner) {
            if (it)
                (requireActivity() as MainActivity).onBackPressed()
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSubmitDetailBinding = FragmentSubmitDetailBinding.inflate(inflater, container, false)

    override fun init() {
        arguments?.let {
            it.getParcelable<Submit>("submit")?.let {
                this.submit = it
            } ?: goBackWhenError()
        } ?: goBackWhenError()

        initViews()
    }

    private fun initViews() {
        with(binding.submitDetailContent) {
            submitFeedback.visibility = View.GONE
            submitScore.visibility = View.GONE
            submit.fileUrl?.let { submitFileUrl.text = it }
                ?: run { submitFileUrl.visibility = View.GONE }
            item = submit
        }

        submit.score?.let { binding.submitDetailEditScore.setText(it.toString()) }
        submit.feedBack?.let { binding.submitDetailEditFeedBack.setText(it) }

        binding.submitDetailSaveButton.setOnClickListener {
            if (binding.submitDetailEditScore.text.toString().isEmpty()) {
                ToastUtil.shortToast(requireContext(), "점수는 필수 항목입니다.")
                return@setOnClickListener
            }

            viewModel.saveScoreFeedback(submit.apply {
                score = binding.submitDetailEditScore.text.toString().toInt()
                feedBack = if (binding.submitDetailEditFeedBack.text.toString()
                        .isEmpty()
                ) null else binding.submitDetailEditFeedBack.text.toString()
            })
        }
    }

}