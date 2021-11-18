package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.adapters.AttendanceLessonListAdapter
import kr.khs.oneboard.adapters.AttendanceListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentAttendanceBinding
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.AttendanceViewModel
import timber.log.Timber

@AndroidEntryPoint
class AttendanceFragment : BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>() {
    override val viewModel: AttendanceViewModel by viewModels()
    private lateinit var attendanceListAdapter: AttendanceListAdapter
    private lateinit var myAttendanceListAdapter: AttendanceLessonListAdapter

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.option_menu_in_attendance, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            when (item.itemId) {
                R.id.attendance_menu_reset -> {
                    viewModel.resetAttendanceList(parentViewModel.getLecture().id)
                }
                R.id.attendance_menu_save -> {
                    viewModel.saveAttendanceList(parentViewModel.getLecture().id)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAttendanceBinding = FragmentAttendanceBinding.inflate(inflater, container, false)

    override fun init() {
        initAttendanceList()
        initData()
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

}