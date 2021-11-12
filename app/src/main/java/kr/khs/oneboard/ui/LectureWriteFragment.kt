package kr.khs.oneboard.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.request.NoticeUpdateRequestDto
import kr.khs.oneboard.databinding.FragmentLectureWriteBinding
import kr.khs.oneboard.extensions.toDateTime
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.LectureWriteViewModel
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

    // TODO: 2021/11/11 과제 관련 기능
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
            if (type == TYPE_NOTICE)
                notice = it.getParcelable("notice")
            else
                assignment = it.getParcelable("assignment")
        } ?: goBackWhenError()

        initExposeTime()
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
    }

    private fun initExposeTime() {
        binding.writeExposeTimeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.writeExposeTimeTextView.visibility =
                if (isChecked) View.INVISIBLE else View.VISIBLE
        }

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
                            "%4d-%2d-%2d %2d:%2d:%2d",
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
                    if (type == TYPE_NOTICE)
                        NoticeUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}"
                        )
                    else
                        null
                )
            } else {
                viewModel.writeContent(
                    parentViewModel.getLecture().id,
                    type,
                    if (type == TYPE_NOTICE)
                        NoticeUpdateRequestDto(
                            binding.writeTitleEditText.text.toString(),
                            binding.writeContentEditText.text.toString(),
                            if (binding.writeExposeTimeCheckBox.isChecked)
                                System.currentTimeMillis().toDateTime()
                            else
                                "${binding.writeExposeTimeTextView.text}:00"
                        )
                    else
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
                    // todo : File Add, if success -> binding.writeFileDescription update
                }
            }
        }
    }

}