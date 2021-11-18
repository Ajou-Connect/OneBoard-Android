package kr.khs.oneboard.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.request.AssignmentUpdateRequestDto
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.databinding.FragmentLectureWriteBinding
import kr.khs.oneboard.extensions.toDateTime
import kr.khs.oneboard.extensions.toTimeInMillis
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.utils.asMultipart
import kr.khs.oneboard.viewmodels.LectureWriteViewModel
import okhttp3.MultipartBody
import timber.log.Timber
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class LectureWriteFragment : BaseFragment<FragmentLectureWriteBinding, LectureWriteViewModel>() {
    override val viewModel: LectureWriteViewModel by viewModels()
    private var type by Delegates.notNull<Boolean>()
    private var isEdit = false
    private var notice: Notice? = null
    private var assignment: Assignment? = null
    private var assignmentFile: MultipartBody.Part? = null

    private val fileResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                assignmentFile = data?.data?.asMultipart("filename", requireContext().contentResolver)
                binding.writeFileDescription.text = data?.data?.lastPathSegment
            }
        }


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
            isEdit = it.getBoolean("isEdit")
            if (isEdit && type == TYPE_NOTICE)
                notice = it.getParcelable("notice")
            else if (isEdit && type == TYPE_ASSIGNMENT)
                assignment = it.getParcelable("assignment")
        } ?: goBackWhenError()

        initExposeTime()
        initStartEndDtLayout()
        initFileAddButton()
        initWriteArticleButton()
        initDefaultData()
    }

    private fun initDefaultData() {
        if (isEdit.not()) return

        binding.writeTitleEditText.setText(
            if (type == TYPE_NOTICE)
                notice?.title
            else
                assignment?.title
        )

        binding.writeContentEditText.setText(
            if (type == TYPE_NOTICE)
                notice?.content
            else
                assignment?.content
        )

        binding.writeExposeTimeCheckBox.isChecked = false
        binding.writeExposeTimeTextView.text =
            if (type == TYPE_NOTICE)
                notice?.exposeDt
            else
                assignment?.exposeDt

        if (type == TYPE_ASSIGNMENT) {
            binding.writeStartDt.text = assignment?.startDt
            binding.writeEndDt.text = assignment?.endDt
            binding.writeAssignmentScore.setText(assignment?.score.toString())
        }
    }

    private fun initExposeTime() {
        binding.writeExposeTimeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.writeExposeTimeTextView.visibility =
                if (isChecked) View.INVISIBLE else View.VISIBLE
        }

        binding.writeExposeTimeTextView.text = System.currentTimeMillis().toDateTime()

        binding.writeExposeTimeTextView.setOnClickListener { textview ->
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
                        (textview as TextView).text = String.format(
                            "%04d-%02d-%02d %02d:%02d:%02d",
                            year,
                            monthNumber + 1,
                            day,
                            hour24,
                            min,
                            sec
                        )
                    }
                }).apply {
                set24HourFormat(true)
                setMaxMinDisplayDate(
                    minDate = Calendar.getInstance().timeInMillis
                )
                setDate(Calendar.getInstance())
            }.showDialog()
        }
    }

    private fun initWriteArticleButton() {
        Timber.tag("WriteArticle").d(if (TYPE_NOTICE) "notice" else "assingment")
        binding.writeButton.setOnClickListener {
            if (isEdit) {
                viewModel.editContent(
                    parentViewModel.getLecture().id,
                    if (type == TYPE_NOTICE) notice?.id ?: -1 else assignment?.id ?: -1,
                    type,
                    if (type == TYPE_NOTICE) {
                        NoticeUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}"
                        )
                    } else
                        null,
                    if (type == TYPE_ASSIGNMENT) {
                        if (binding.writeStartDt.text.toString()
                                .toTimeInMillis() >= binding.writeEndDt.text.toString()
                                .toTimeInMillis()
                        ) {
                            viewModel.setErrorMessage("시작 시간이 마감 시간보다 크거나 같을 수 없습니다.")
                            return@setOnClickListener
                        }
                        AssignmentUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            "",
                            binding.writeStartDt.text.toString(),
                            binding.writeEndDt.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}",
                            binding.writeAssignmentScore.text.toString().toFloat()
                        )
                    } else
                        null
                )
            } else {
                viewModel.writeContent(
                    parentViewModel.getLecture().id,
                    type,
                    if (type == TYPE_NOTICE) {
                        NoticeUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}"
                        )
                    } else
                        null,
                    if (type == TYPE_ASSIGNMENT) {
                        if (binding.writeStartDt.text.toString()
                                .toTimeInMillis() >= binding.writeEndDt.text.toString()
                                .toTimeInMillis()
                        ) {
                            viewModel.setErrorMessage("시작 시간이 마감 시간보다 크거나 같을 수 없습니다.")
                            return@setOnClickListener
                        }
                        AssignmentUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            "",
                            binding.writeStartDt.text.toString(),
                            binding.writeEndDt.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}",
                            binding.writeAssignmentScore.text.toString().toFloat()
                        )
                    } else
                        null
                )
            }
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
                    fileResultLauncher.launch(
                        Intent(Intent.ACTION_GET_CONTENT).setType("*/*")
                    )
                }
            }
        }
    }

    private fun initStartEndDtLayout() {
        when (type) {
            TYPE_NOTICE -> {
                binding.writeStartEndDT.visibility = View.GONE
            }
            TYPE_ASSIGNMENT -> {
                binding.writeStartEndDT.visibility = View.VISIBLE
                binding.writeStartDt.text = System.currentTimeMillis().toDateTime()
                binding.writeEndDt.text = System.currentTimeMillis().toDateTime()

                binding.writeStartDt.setOnClickListener { textview ->
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
                                (textview as TextView).text = String.format(
                                    "%04d-%02d-%02d %02d:%02d:%02d",
                                    year,
                                    monthNumber + 1,
                                    day,
                                    hour24,
                                    min,
                                    sec
                                )
                            }
                        }).apply {
                        set24HourFormat(true)
                        setMaxMinDisplayDate(
                            minDate = Calendar.getInstance().timeInMillis
                        )
                        setDate(Calendar.getInstance())
                    }.showDialog()
                }
                binding.writeEndDt.setOnClickListener { textview ->
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
                                (textview as TextView).text = String.format(
                                    "%04d-%02d-%02d %02d:%02d:%02d",
                                    year,
                                    monthNumber + 1,
                                    day,
                                    hour24,
                                    min,
                                    sec
                                )
                            }
                        }).apply {
                        set24HourFormat(true)
                        setMaxMinDisplayDate(
                            minDate = Calendar.getInstance().timeInMillis
                        )
                        setDate(Calendar.getInstance())
                    }.showDialog()
                }
            }
        }
    }
}