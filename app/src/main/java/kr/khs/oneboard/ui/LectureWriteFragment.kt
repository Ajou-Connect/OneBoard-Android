package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.FragmentLectureWriteBinding
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.LectureWriteViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class LectureWriteFragment : BaseFragment<FragmentLectureWriteBinding, LectureWriteViewModel>() {
    override val viewModel: LectureWriteViewModel by viewModels()
    private var type by Delegates.notNull<Boolean>()
    private var exposerRightNow by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.status.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (status) {
                    ToastUtil.shortToast(requireContext(), "작성되었습니다.")
                    (requireActivity() as MainActivity).onBackPressed()
                } else {
                    ToastUtil.shortToast(requireContext(), "작성하는데 오류가 발생했습니다.\n다시 시도해주세요.")
                }
            }
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLectureWriteBinding =
        FragmentLectureWriteBinding.inflate(layoutInflater, container, false)

    override fun init() {
        arguments?.let {
            type = it.getBoolean("type")
        } ?: goBackWhenError()

        initExposeTime()
        initFileAddButton()
        initWriteArticleButton()
    }

    private fun initExposeTime() {
        binding.writeExposeTimeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.writeExposeTimeTextView.visibility =
                if (isChecked) View.INVISIBLE else View.VISIBLE
        }

        binding.writeExposeTimeTextView.setOnClickListener {
            // todo 달력 다이얼로그 띄워서 날짜 / 시간 지정할 수 있도록 설정
        }
    }

    private fun initWriteArticleButton() {
        binding.writeButton.setOnClickListener {
            viewModel.writeContent(
                type,
                if (type == TYPE_ASSIGNMENT)
                    Assignment(1, 1, "1", "1", "1", 1L, 1L, "", "", "")
                else
                    Notice(1, 1, "1", "1", "1", 1L, 1L)
            )
        }
    }

    private fun initFileAddButton() {
        when (type) {
            TYPE_NOTICE -> {
                binding.writeFileLayout.visibility = View.GONE
            }
            TYPE_ASSIGNMENT -> {
                binding.writeFileLayout.visibility = View.VISIBLE
                binding.writeFileAddButton.setOnClickListener {
                    // todo : File Add, if success -> binding.writeFileDescription update
                }
            }
        }
    }

}