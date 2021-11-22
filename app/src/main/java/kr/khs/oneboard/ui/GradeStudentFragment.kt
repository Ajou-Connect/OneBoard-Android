package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.AttendanceLessonListAdapter
import kr.khs.oneboard.adapters.SubmitGradeListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.databinding.FragmentGradeStudentBinding
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.utils.collapse
import kr.khs.oneboard.utils.expand
import kr.khs.oneboard.viewmodels.GradeStudentViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class GradeStudentFragment : BaseFragment<FragmentGradeStudentBinding, GradeStudentViewModel>() {
    override val viewModel: GradeStudentViewModel by viewModels()
    private var studentId by Delegates.notNull<Int>()
    private var submitExpanded = false
    private var attendanceExpanded = false

    private lateinit var attendanceLessonListAdapter: AttendanceLessonListAdapter
    private lateinit var submitGradeListAdapter: SubmitGradeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gradeInfo.observe(viewLifecycleOwner) {
            setViewItem(it)
            submitGradeListAdapter.submitList(it.submitList?.toMutableList())
            attendanceLessonListAdapter.submitList(it.attendanceList?.toMutableList())
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeStudentBinding = FragmentGradeStudentBinding.inflate(layoutInflater)

    override fun init() {
        initArguments()
        initSubmitGradeList()
        initAttendanceGradeList()
        initData()
        initExpandCollapseButton()
    }

    private fun setViewItem(item: GradeStudent) {
        binding.gradeStudentName.text = item.userName
        binding.gradeStudentNumber.text = item.studentNumber
        binding.gradeStudentSubmitScore.text = item.submitScore.toString()
        binding.gradeStudentAttendanceScore.text = item.attendScore.toString()
    }

    private fun initExpandCollapseButton() {
        binding.gradeStudentSubmitBtn.setOnClickListener {
            if (submitExpanded) {
                binding.gradeStudentSubmitList.collapse()
                it.animate().rotation(0f)
            } else {
                binding.gradeStudentSubmitList.expand()
                it.animate().rotation(180f)
            }
            submitExpanded = !submitExpanded
        }

        binding.gradeStudentAttendanceBtn.setOnClickListener {
            if (attendanceExpanded) {
                binding.gradeStudentAttendanceList.collapse()
                it.animate().rotation(0f)
            } else {
                binding.gradeStudentAttendanceList.expand()
                it.animate().rotation(180f)
            }
            attendanceExpanded = !attendanceExpanded
        }
    }

    private fun initArguments() {
        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            arguments?.getInt("studentId")?.let {
                studentId = it
            } ?: goBackWhenError()
        }
    }

    private fun initData() {
        binding.gradeStudentSubmitList.collapse(0L)
        binding.gradeStudentAttendanceList.collapse(0L)
        viewModel.getGradeInfo(
            parentViewModel.getLecture().id,
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                studentId
            else
                null
        )
    }

    private fun initSubmitGradeList() {
        with(binding.gradeStudentSubmitList) {
            submitGradeListAdapter = SubmitGradeListAdapter()
            adapter = submitGradeListAdapter

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }

    private fun initAttendanceGradeList() {
        with(binding.gradeStudentAttendanceList) {
            attendanceLessonListAdapter = AttendanceLessonListAdapter().apply {
                onLessonStatusChange = { }
            }
            adapter = attendanceLessonListAdapter

            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }
}