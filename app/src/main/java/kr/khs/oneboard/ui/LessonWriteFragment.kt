package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.request.LessonUpdateRequestDto
import kr.khs.oneboard.databinding.FragmentLessonWriteBinding
import kr.khs.oneboard.extensions.toTimeInMillisWithoutSec
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.LessonWriteViewModel
import okhttp3.MultipartBody
import java.text.ParseException
import java.util.*

@AndroidEntryPoint
class LessonWriteFragment : BaseFragment<FragmentLessonWriteBinding, LessonWriteViewModel>() {
    override val viewModel: LessonWriteViewModel by viewModels()

    private var lessonRoom: String? = null
    private lateinit var lessonDateTime: String
    private var isEdit = false
    private var lessonId = -1

    private var noteFile: MultipartBody.Part? = null


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
            lessonDateTime = it.defaultDateTime ?: "날짜, 시간 선택"
            binding.lessonWriteDateTime.text = lessonDateTime

            binding.lessonWriteTitle.setText(it.defaultTitle ?: "강의실 미정")

            lessonRoom = it.defaultRoom
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonWriteBinding = FragmentLessonWriteBinding.inflate(inflater, container, false)

    override fun init() {
        binding.viewTitle.root.text = "수업 생성"
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
            binding.lessonWriteDateTime.text = date
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
        lessonDateTime = "날짜, 시간 선택"

        lessonRoom = "강의실 미정"
    }

    private fun initTimeDate() {
        binding.lessonWriteCheckbox.text = "수정하기"
        setDateTimeEnabled(false)

        binding.lessonWriteCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setDateTimeEnabled(true)
            } else {
                binding.lessonWriteDateTime.text = lessonDateTime
                setDateTimeEnabled(false)
            }
        }
        binding.lessonWriteDateTime.text = lessonDateTime

        binding.lessonWriteDateTime.setOnClickListener { textView ->
            CustomDateTimePicker(
                requireActivity(),
                object : CustomDateTimePicker.ICustomDateTimeListener {
                    override fun onCancel() {}

                    override fun onSet(
                        dialog: Dialog,
                        calendarSelected: Calendar,
                        dateSelected: Date,
                        year: Int,
                        monthFullName: String,
                        monthShortName: String,
                        monthNumber: Int,
                        day: Int,
                        weekDayFullName: String,
                        weekDayShortName: String,
                        hour24: Int,
                        hour12: Int,
                        min: Int,
                        sec: Int,
                        AM_PM: String
                    ) {
                        (textView as TextView).text = String.format(
                            "%04d-%02d-%02d %02d:%02d",
                            year,
                            monthNumber + 1,
                            day,
                            hour24,
                            min
                        )
                    }
                }
            ).apply {
                set24HourFormat(true)
                setMaxMinDisplayDate(minDate = Calendar.getInstance().timeInMillis)
                setDate(
                    try {
                        Date(binding.lessonWriteDateTime.text.toString().toTimeInMillisWithoutSec())
                    } catch (e: ParseException) {
                        Date(System.currentTimeMillis())
                    }
                )
            }.showDialog()
        }
    }

    private fun setDateTimeEnabled(enabled: Boolean) {
        binding.lessonWriteDateTime.isEnabled = enabled
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
                    .isEmpty() || binding.lessonWriteDateTime.text == "날짜, 시간 선택"
            ) {
                viewModel.setErrorMessage("날짜, 시간을 선택해주세요.")
            }
            if (isEdit) {
                viewModel.editLesson(
                    parentViewModel.getLecture().id,
                    lessonId,
                    binding.lessonWriteTitle.text.toString(),
                    binding.lessonWriteDateTime.text.toString() + ":00",
                    noteFile,
                    if (viewModel.lessonType.value!! == TYPE_FACE_TO_FACE) binding.lessonWriteShowText.text.toString() else null,
                    null,
                    null
                )
            } else {
                viewModel.writeLesson(
                    parentViewModel.getLecture().id,
                    binding.lessonWriteTitle.text.toString(),
                    binding.lessonWriteDateTime.text.toString() + ":00",
                    noteFile,
                    if (viewModel.lessonType.value!! == TYPE_FACE_TO_FACE) binding.lessonWriteShowText.text.toString() else null,
                    null,
                    null
                )
            }
        }
    }
}