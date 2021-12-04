package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lectureInfo.observe(viewLifecycleOwner) {
            requireActivity().title = it.title
            binding.lectureDetailTitle.text = it.title
            binding.lectureDetailSemester.text = it.semester
            binding.lectureDetailProfessor.text = it.professor
            binding.lectureDetailNoticeTitle.text = "중간 발표 공지사항"
            binding.lectureDetailNotice.text = "11월 22일 발표!!"
            binding.lectureDetailLesson.text = "2021-11-22 16:30\n내일 오후 4시 30분"
            binding.lectureDetailAssignment.text = "과제가 없습니다."
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
        viewModel.setLectureInfo(parentViewModel.getLecture().id)
    }

}