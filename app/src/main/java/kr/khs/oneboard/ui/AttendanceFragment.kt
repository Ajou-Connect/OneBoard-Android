package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.AttendanceListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentAttendanceBinding
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.AttendanceViewModel

@AndroidEntryPoint
class AttendanceFragment : BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>() {
    override val viewModel: AttendanceViewModel by viewModels()
    private lateinit var attendanceListAdapter: AttendanceListAdapter

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

        viewModel.attendanceList.observe(viewLifecycleOwner) {
            attendanceListAdapter.submitList(it)
        }
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
        viewModel.getAttendanceList(parentViewModel.getLecture().id)
    }

    private fun initAttendanceList() {
        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            with(binding.listAttendance) {
                attendanceListAdapter = AttendanceListAdapter().apply {

                }
                adapter = attendanceListAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
        } else {

        }
    }

}