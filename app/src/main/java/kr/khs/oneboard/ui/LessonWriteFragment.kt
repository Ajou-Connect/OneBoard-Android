package kr.khs.oneboard.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class LessonWriteFragment : BaseFragment<FragmentLessonWriteBinding, LessonWriteViewModel>() {
    override val viewModel: LessonWriteViewModel by viewModels()
    private var year by Delegates.notNull<Int>()
    private var month by Delegates.notNull<Int>()
    private var day by Delegates.notNull<Int>()
    private var hour by Delegates.notNull<Int>()
    private var minute by Delegates.notNull<Int>()

    private val onDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            this.year = year
            this.month = month
            this.day = dayOfMonth
            binding.lessonWriteDate.text =
                String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        }
    }

    private val onTimeSetListener by lazy {
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            this.hour = hourOfDay
            this.minute = minute
            binding.lessonWriteTime.text = String.format("%02d:%02d", hour, minute)
        }
    }

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
        initTimeDate()
        initWriteLessonButton()
        initLessonTypeSpinner()
        initShowButton()
    }

    private fun initTimeDate() {
        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        val time = SimpleDateFormat("hh:mm").format(c.time).split(":")
        hour = time[0].toInt()
        minute = time[1].toInt()

        val minDate = Calendar.getInstance().apply {
            set(year, month, day)
        }

        // 1안 : 이전 수업과 동일 체크박스
        // 이전 수업과 동일로 할 경우 날짜는 어떻게?
        binding.lessonWriteCheckbox.text = "이전 수업과 동일"
        binding.lessonWriteCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.lessonWriteDate.text = "2021-11-04"
                binding.lessonWriteTime.text = "16:30"
                setDateTimeEnabled(false)
            } else {
                setDateTimeEnabled(true)
            }
        }
        binding.lessonWriteDate.text = "2021-11-04"
        binding.lessonWriteTime.text = "16:30"

        // 2안 : 가장 가까운 날짜 디폴트로, 체크박스를 통해 수정 가능
        binding.lessonWriteCheckbox.text = "수정하기"
        setDateTimeEnabled(false)
        binding.lessonWriteCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setDateTimeEnabled(true)
            } else {
                binding.lessonWriteDate.text = "2021-11-04"
                binding.lessonWriteTime.text = "16:30"
                setDateTimeEnabled(false)
            }
        }
        binding.lessonWriteDate.text = "2021-11-04"
        binding.lessonWriteTime.text = "16:30"

        // 1, 2안 끝
        binding.lessonWriteDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                onDateSetListener,
                year,
                month,
                day
            ).apply {
                datePicker.minDate = minDate.time.time
            }.show()
        }
        binding.lessonWriteTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                onTimeSetListener,
                hour,
                minute,
                true
            ).show()
        }
    }

    private fun setDateTimeEnabled(enabled: Boolean) {
        binding.lessonWriteDate.isEnabled = enabled
        binding.lessonWriteTime.isEnabled = enabled
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