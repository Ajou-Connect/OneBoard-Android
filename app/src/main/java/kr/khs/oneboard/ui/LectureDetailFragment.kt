package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.databinding.FragmentLectureDetailBinding
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.viewmodels.LectureDetailViewModel
import timber.log.Timber

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

        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLectureDetailBinding =
        FragmentLectureDetailBinding.inflate(inflater, container, false)

    override fun init() {
        getSafeArgs()
        inflateMenu(true)
    }

    private fun getSafeArgs() {
        arguments?.let {
            it.getParcelable<Lecture>("lectureInfo")?.let { lecture ->
                Timber.tag("lectureInfo").d("$lecture")
                viewModel.setLectureInfo(lecture)
            } ?: run {
                DialogUtil.createDialog(
                    context = requireActivity(),
                    message = "강의 정보를 불러오는데 실패했습니다.",
                    positiveText = "뒤로가기",
                    positiveAction = { requireActivity().onBackPressed() }
                )
            }
        } ?: run {
            DialogUtil.createDialog(
                context = requireActivity(),
                message = "강의 정보를 불러오는데 실패했습니다.",
                positiveText = "뒤로가기",
                positiveAction = { requireActivity().onBackPressed() }
            )
        }
    }

}