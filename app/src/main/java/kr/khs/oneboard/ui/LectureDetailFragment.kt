package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentLectureDetailBinding
import kr.khs.oneboard.viewmodels.LectureDetailViewModel

@AndroidEntryPoint
class LectureDetailFragment : BaseFragment<FragmentLectureDetailBinding, LectureDetailViewModel>() {
    override val viewModel: LectureDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lectureInfo.observe(viewLifecycleOwner) {
            requireActivity().title = it.title
            binding.lectureDetailTitle.text = it.title
            binding.lectureDetailSemester.text =
                String.format(getString(R.string.semester), it.semester)
            binding.lectureDetailProfessor.text = it.professor
        }

        viewModel.latestNotice.observe(viewLifecycleOwner) {
            binding.lectureDetailNoticeTitle.text = it.title
            binding.lectureDetailNotice.text = Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY)
        }

        viewModel.latestLesson.observe(viewLifecycleOwner) {
            binding.lectureDetailLesson.text = "${it.title}\n\n${it.date}"
        }

        viewModel.latestAssignment.observe(viewLifecycleOwner) {
            binding.lectureDetailAssignmentTitle.text = it.title
            binding.lectureDetailAssignment.text =
                Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLectureDetailBinding =
        FragmentLectureDetailBinding.inflate(inflater, container, false)

    override fun init() {
        binding.viewTitle.root.text = "과목 상세"
        getSafeArgs()
        inflateMenu(true)

    }

    private fun getSafeArgs() {
        viewModel.setLectureInfo(parentViewModel.getLecture())
    }

}