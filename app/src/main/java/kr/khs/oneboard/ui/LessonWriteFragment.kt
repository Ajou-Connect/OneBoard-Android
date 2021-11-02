package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentLessonWriteBinding
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_RECORDING
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.LessonWriteViewModel

@AndroidEntryPoint
class LessonWriteFragment : BaseFragment<FragmentLessonWriteBinding, LessonWriteViewModel>() {
    override val viewModel: LessonWriteViewModel by viewModels()

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

        viewModel.lessonType.observe(viewLifecycleOwner) {
            when (it) {
                TYPE_FACE_TO_FACE -> {
                    binding.lessonWriteShowText.visibility = View.VISIBLE
                    binding.lessonWriteShowButton.visibility = View.GONE
                    binding.lessonWriteShowText.text = "팔달관 311호"
                }
                TYPE_NON_FACE_TO_FACE -> {
                    binding.lessonWriteShowText.visibility = View.VISIBLE
                    binding.lessonWriteShowButton.visibility = View.VISIBLE
                    binding.lessonWriteShowText.text = ""
                    binding.lessonWriteShowButton.text = "수업 링크 생성"
                }
                TYPE_RECORDING -> {
                    binding.lessonWriteShowText.visibility = View.GONE
                    binding.lessonWriteShowButton.visibility = View.GONE
                }
            }
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonWriteBinding = FragmentLessonWriteBinding.inflate(inflater, container, false)

    override fun init() {
        initTitle()
        initWriteLessonButton()
        initLessonTypeSpinner()
        initShowButton()
    }

    private fun initTitle() {
        binding.lessonwriteTitle.text =
            "${parentViewModel.getLecture().title} - ${(1..32).random()}수업"
    }

    private fun initShowButton() {
        binding.lessonWriteShowButton.setOnClickListener {
            if (viewModel.lessonType.value!! == TYPE_NON_FACE_TO_FACE)
                binding.lessonWriteShowText.text =
                    String.format("zoom.us/%06d", (0 until 1_000_000).random())
        }
    }

    private fun initLessonTypeSpinner() {
        with(binding.lessonWriteSpinner) {
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.spinner_type_array)
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setLessonType(
                        when (position) {
                            TYPE_FACE_TO_FACE -> TYPE_FACE_TO_FACE
                            TYPE_NON_FACE_TO_FACE -> TYPE_NON_FACE_TO_FACE
                            TYPE_RECORDING -> TYPE_RECORDING
                            else -> TYPE_FACE_TO_FACE
                        }
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }
    }

    private fun initWriteLessonButton() {
        binding.lessonWriteButton.setOnClickListener {
            if (viewModel.writeLesson("1"))
                requireActivity().onBackPressed()
            else
                ToastUtil.shortToast(requireContext(), "수업을 생성하는데 실패하였습니다.\n다시 시도해 주세요.")
        }
    }
}