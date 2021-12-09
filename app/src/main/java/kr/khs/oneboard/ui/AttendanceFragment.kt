package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.adapters.AttendanceLessonListAdapter
import kr.khs.oneboard.adapters.AttendanceListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.DialogAttendanceListOptionMenuBinding
import kr.khs.oneboard.databinding.DialogAttendanceSpinnerBinding
import kr.khs.oneboard.databinding.FragmentAttendanceBinding
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.AttendanceViewModel
import timber.log.Timber

@AndroidEntryPoint
class AttendanceFragment : BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>() {
    override val viewModel: AttendanceViewModel by viewModels()
    private lateinit var attendanceListAdapter: AttendanceListAdapter
    private lateinit var myAttendanceListAdapter: AttendanceLessonListAdapter
    private val lectureId by lazy {
        parentViewModel.getLecture().id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attendanceList.observe(viewLifecycleOwner) {
            Timber.tag("ChangeAttendance").d("$it")
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                attendanceListAdapter.submitList(it.toMutableList())
            else
                myAttendanceListAdapter.submitList(it[0].attendanceList.toMutableList())
        }
    }


    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAttendanceBinding = FragmentAttendanceBinding.inflate(inflater, container, false)

    override fun init() {
        binding.viewTitle.root.text = "출석 목록"
        initAttendanceList()
        initData()

        if (UserInfoUtil.type == TYPE_PROFESSOR)
            initOptionMenu()
    }

    private fun initOptionMenu() {
        binding.attendanceProfessorLayout.visibility = View.VISIBLE

        binding.attendanceReset.setOnClickListener {
            viewModel.resetAttendanceList(parentViewModel.getLecture().id)
        }

        binding.attendanceSave.setOnClickListener {
            viewModel.saveAttendanceList(parentViewModel.getLecture().id)
        }

        binding.attendanceChange.setOnClickListener {
            createOptionDialog()
        }
    }

    private fun initData() {
        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            viewModel.getAttendanceList(parentViewModel.getLecture().id)
        } else {
            viewModel.getMyAttendance(parentViewModel.getLecture().id)
        }
    }

    private fun initAttendanceList() {
        with(binding.listAttendance) {
            if (UserInfoUtil.type == TYPE_PROFESSOR) {
                attendanceListAdapter = AttendanceListAdapter().apply {
                    onStudentStatusChange = { attendanceStudent ->
                        viewModel.updateAttendance(attendanceStudent)
                    }
                }
                adapter = attendanceListAdapter
            } else {
                myAttendanceListAdapter = AttendanceLessonListAdapter().apply {
                    onLessonStatusChange = { }
                }
                adapter = myAttendanceListAdapter
            }
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

    private fun createOptionDialog() {
        val dialogBinding = DialogAttendanceListOptionMenuBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.dialogOptionAll.setOnClickListener {
            createChangeAllAttendanceDialog()
            dialog.dismiss()
        }

        dialogBinding.dialogOptionLesson.setOnClickListener {
            createChangeLessonAttendanceDialog()
            dialog.dismiss()
        }

        dialogBinding.dialogOptionStudent.setOnClickListener {
            createChangeStudentAttendanceDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createChangeAllAttendanceDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("모든 학생의 출석 여부를 변경합니다.\n출석 여부를 선택해주세요.\n(취소 : 외부 클릭)")
            .setCancelable(true)
            .setPositiveButton("출석") { dialog, _ ->
                dialog.dismiss()
                DialogUtil.createDialog(
                    requireContext(),
                    "모든 학생의 출결을 '출석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    { viewModel.changeAllAttendance(lectureId, true) },
                    { }
                )
            }
            .setNegativeButton("결석") { _, _ ->
                DialogUtil.createDialog(
                    requireContext(),
                    "모든 학생의 출결을 '결석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    { viewModel.changeAllAttendance(lectureId, false) },
                    { }
                )
            }
            .show()
    }

    private fun createChangeLessonAttendanceDialog() {
        val dialogBinding = DialogAttendanceSpinnerBinding.inflate(layoutInflater)

        // select lesson id
        var lessonId = 0

        with(dialogBinding.dialogAttendanceSpinner) {
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                viewModel.lessonList.value!!.map { it.title }
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    lessonId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .setPositiveButton("출석") { _, _ ->
                DialogUtil.createDialog(
                    requireContext(),
                    "해당 수업의 모든 학생의 출결을 '출석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    {
                        viewModel.changeAllAttendance(lectureId, true, lessonIdx = lessonId)
                    },
                    { }
                )
            }
            .setNegativeButton("결석") { _, _ ->
                DialogUtil.createDialog(
                    requireContext(),
                    "해당 수업의 모든 학생의 출결을 '결석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    { viewModel.changeAllAttendance(lectureId, false, lessonIdx = lessonId) },
                    { }
                )
            }
            .show()
    }


    private fun createChangeStudentAttendanceDialog() {
        val dialogBinding = DialogAttendanceSpinnerBinding.inflate(layoutInflater)

        // select student Id
        var studentId = 0

        with(dialogBinding.dialogAttendanceSpinner) {
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                viewModel.attendanceList.value!!.map { it.studentName }
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    studentId = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .setPositiveButton("출석") { _, _ ->
                DialogUtil.createDialog(
                    requireContext(),
                    "해당 학생의 모든 출결을 '출석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    { viewModel.changeAllAttendance(lectureId, true, studentIdx = studentId) },
                    { }
                )
            }
            .setNegativeButton("결석") { _, _ ->
                DialogUtil.createDialog(
                    requireContext(),
                    "해당 학생의 모든 출결을 '결석'으로 변경하시겠습니까?",
                    "확인",
                    "취소",
                    { viewModel.changeAllAttendance(lectureId, false, studentIdx = studentId) },
                    { }
                )
            }
            .show()
    }
}