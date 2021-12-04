package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.request.LessonUpdateRequestDto
import kr.khs.oneboard.databinding.FragmentLessonWriteBinding
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.LessonWriteViewModel
import okhttp3.MultipartBody
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
    private var lessonRoom: String? = null
    private lateinit var lessonDate: String
    private lateinit var lessonTime: String
    private var isEdit = false
    private var lessonId = -1

    private var noteFile: MultipartBody.Part? = null

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

    @SuppressLint("SetTextI18n")
    private val fileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                noteFile =
                    data?.data?.asMultipart("file", requireContext().contentResolver)

                binding.lessonWriteFileDescription.text =
                    "${data?.data?.getFileName(requireContext().contentResolver)}"
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
                    binding.lessonWriteShowText.setText(lessonRoom)
                }
                TYPE_NON_FACE_TO_FACE -> {
                    binding.lessonWriteShowText.visibility = View.GONE
                    binding.lessonWriteShowButton.visibility = View.GONE
                }
                TYPE_RECORDING -> {
                    binding.lessonWriteShowText.visibility = View.GONE
                    binding.lessonWriteShowButton.visibility = View.GONE
                }
            }
        }

        viewModel.updateLesson.observe(viewLifecycleOwner) {
            if (it)
                requireActivity().onBackPressed()
        }

        viewModel.defaultInfo.observe(viewLifecycleOwner) {
            it.defaultDateTime?.split(" ")?.let { datetime ->
                binding.lessonWriteDate.text = datetime[0]
                binding.lessonWriteTime.text = datetime[1]
            }

            binding.lessonWriteTitle.setText(it.defaultTitle ?: "강의실 미정")

            lessonRoom = it.defaultRoom
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonWriteBinding = FragmentLessonWriteBinding.inflate(inflater, container, false)

    override fun init() {
        getDefaultLectureValue()
        initTitle()
        initTimeDate()
        initWriteLessonButton()
        initLessonTypeSpinner()
        initShowButton()
        initNoteAdd()
        initDefaultData()

        if (isEdit.not())
            viewModel.getDefaultLessonInfo(parentViewModel.getLecture().id)
    }

    private fun initDefaultData() {
        val editData = arguments?.getParcelable<LessonUpdateRequestDto>("item") ?: return
        isEdit = true
        with(editData) {
            binding.lessonWriteTitle.setText(title)
            val dateTime = date.split(" ")
            binding.lessonWriteDate.text = dateTime[0]
            binding.lessonWriteTime.text = dateTime[1]
            binding.lessonWriteSpinner.setSelection(type)
            if (type == TYPE_FACE_TO_FACE)
                binding.lessonWriteShowText.setText(room)

            DialogUtil.createDialog(
                requireContext(),
                "강의 노트는 다시 업로드를 해주어야 합니다.",
                positiveText = "알겠습니다.",
                positiveAction = { }
            )
        }
        lessonId = arguments?.getInt("lessonId")!!
    }

    private fun initNoteAdd() {
        binding.lessonWriteFileAddButton.setOnClickListener {
            fileResultLauncher.launch(
                Intent(Intent.ACTION_GET_CONTENT).setType("application/pdf")
            )
        }
    }

    private fun getDefaultLectureValue() {
        binding.lessonWriteCheckbox.isSelected = true
        lessonDate = "날짜 선택"
        lessonTime = "시간 선택"

        lessonRoom = "강의실 미정"
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

        binding.lessonWriteCheckbox.text = "수정하기"
        setDateTimeEnabled(false)
        binding.lessonWriteCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setDateTimeEnabled(true)
            } else {
                binding.lessonWriteDate.text = lessonDate
                binding.lessonWriteTime.text = lessonTime
                setDateTimeEnabled(false)
            }
        }
        binding.lessonWriteDate.text = lessonDate
        binding.lessonWriteTime.text = lessonTime

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
        binding.lessonWriteTitle.hint = parentViewModel.getLecture().title
    }

    private fun initShowButton() {
        binding.lessonWriteShowButton.setOnClickListener { }
    }

    private fun initLessonTypeSpinner() {
        with(binding.lessonWriteSpinner) {
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
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
            if (binding.lessonWriteTitle.text.toString()
                    .isEmpty() || binding.lessonWriteDate.text == "날짜 선택" || binding.lessonWriteTime.text == "시간 선택"
            ) {
                viewModel.setErrorMessage("날짜, 시간을 선택해주세요.")
            }
            if (isEdit) {
                viewModel.editLesson(
                    parentViewModel.getLecture().id,
                    lessonId,
                    binding.lessonWriteTitle.text.toString(),
                    binding.lessonWriteDate.text.toString() + " " + binding.lessonWriteTime.text.toString(),
                    noteFile,
                    if (viewModel.lessonType.value!! == TYPE_FACE_TO_FACE) binding.lessonWriteShowText.text.toString() else null,
                    null,
                    null
                )
            } else {
                viewModel.writeLesson(
                    parentViewModel.getLecture().id,
                    binding.lessonWriteTitle.text.toString(),
                    binding.lessonWriteDate.text.toString() + " " + binding.lessonWriteTime.text.toString(),
                    noteFile,
                    if (viewModel.lessonType.value!! == TYPE_FACE_TO_FACE) binding.lessonWriteShowText.text.toString() else null,
                    null,
                    null
                )
            }
        }
    }
}