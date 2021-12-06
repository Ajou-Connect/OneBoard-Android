package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.adapters.AttendanceLessonListAdapter
import kr.khs.oneboard.adapters.SubmitGradeListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.databinding.FragmentGradeStudentBinding
import kr.khs.oneboard.databinding.ViewSelectGradeBinding
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.GradeStudentViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class GradeStudentFragment : BaseFragment<FragmentGradeStudentBinding, GradeStudentViewModel>() {
    override val viewModel: GradeStudentViewModel by viewModels()
    private var studentId by Delegates.notNull<Int>()
    private var submitExpanded = false
    private var attendanceExpanded = false

    private var dialogBinding: ViewSelectGradeBinding? = null

    private lateinit var attendanceLessonListAdapter: AttendanceLessonListAdapter
    private lateinit var submitGradeListAdapter: SubmitGradeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.gradeInfo.observe(viewLifecycleOwner) {
            setViewItem(it)
            submitGradeListAdapter.submitList(it.submitList?.toMutableList())
            attendanceLessonListAdapter.submitList(it.attendanceList?.toMutableList())
        }

        viewModel.gradeResult.observe(viewLifecycleOwner) {
            ToastUtil.shortToast(
                requireContext(),
                if (it)
                    "학점이 변경되었습니다."
                else
                    "오류가 발생했습니다..다시 시도해주세요."
            )
            dialogBinding = null
        }

        if (UserInfoUtil.type == TYPE_PROFESSOR)
            viewModel.grade.observe(viewLifecycleOwner) {
                binding.gradeStudentResult.text = it
            }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeStudentBinding = FragmentGradeStudentBinding.inflate(layoutInflater)

    override fun init() {
        binding.viewTitle.root.text = "학생 성적 목록"
        initArguments()
        initResultChange()
        initSubmitGradeList()
        initAttendanceGradeList()
        initData()
        initExpandCollapseButton()
    }

    private fun initResultChange() {
        if (UserInfoUtil.type == TYPE_STUDENT)
            return

        binding.gradeStudentResult.setOnClickListener {
            createGradeResultDialog()
        }
    }

    private fun createGradeResultDialog() {
        dialogBinding = ViewSelectGradeBinding.inflate(layoutInflater)
        dialogBinding ?: return

        when (viewModel.grade.value!!) {
            "A+" -> dialogBinding!!.setSelectColor(R.id.selectGradeAplus)
            "A" -> dialogBinding!!.setSelectColor(R.id.selectGradeA)
            "B+" -> dialogBinding!!.setSelectColor(R.id.selectGradeBplus)
            "B" -> dialogBinding!!.setSelectColor(R.id.selectGradeB)
            "C+" -> dialogBinding!!.setSelectColor(R.id.selectGradeCplus)
            "C" -> dialogBinding!!.setSelectColor(R.id.selectGradeC)
            "D" -> dialogBinding!!.setSelectColor(R.id.selectGradeD)
            "F" -> dialogBinding!!.setSelectColor(R.id.selectGradeF)
        }

        dialogBinding!!.selectGradeAplus.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeA.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeBplus.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeB.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeCplus.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeC.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeD.setOnClickListener { selectGradeView(it) }
        dialogBinding!!.selectGradeF.setOnClickListener { selectGradeView(it) }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding!!.root)
            .setPositiveButton("저장") { dialog, _ ->
                viewModel.postGradeInfo(
                    parentViewModel.getLecture().id,
                    studentId,
                    viewModel.grade.value!!
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .setOnCancelListener {
                viewModel.resetGrade()
            }
            .show()
    }

    private fun selectGradeView(view: View) {
        viewModel.setGrade(
            when (view.id) {
                R.id.selectGradeAplus -> {
                    viewModel.setGrade("A+")
                    dialogBinding!!.setSelectColor(R.id.selectGradeAplus)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeA -> {
                    viewModel.setGrade("A")
                    dialogBinding!!.setSelectColor(R.id.selectGradeA)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeBplus -> {
                    viewModel.setGrade("B+")
                    dialogBinding!!.setSelectColor(R.id.selectGradeBplus)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeB -> {
                    viewModel.setGrade("B")
                    dialogBinding!!.setSelectColor(R.id.selectGradeB)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeCplus -> {
                    viewModel.setGrade("C+")
                    dialogBinding!!.setSelectColor(R.id.selectGradeCplus)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeC -> {
                    viewModel.setGrade("C")
                    dialogBinding!!.setSelectColor(R.id.selectGradeC)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeD -> {
                    viewModel.setGrade("D")
                    dialogBinding!!.setSelectColor(R.id.selectGradeD)
                    (view as TextView).text.toString()
                }
                R.id.selectGradeF -> {
                    viewModel.setGrade("F")
                    dialogBinding!!.setSelectColor(R.id.selectGradeF)
                    (view as TextView).text.toString()
                }
                else -> {
                    ""
                }
            }
        )
    }

    private fun setViewItem(item: GradeStudent) {
        binding.gradeStudentName.text = item.userName
        binding.gradeStudentNumber.text = item.studentNumber
        binding.gradeStudentSubmitScore.text = item.submitScore.toString()
        binding.gradeStudentResult.text = item.result
        binding.gradeStudentTotal.text = item.totalScore.toString()
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