package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.AssignmentListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentAssignmentBinding
import kr.khs.oneboard.viewmodels.AssignmentViewModel

@AndroidEntryPoint
class AssignmentFragment : BaseFragment<FragmentAssignmentBinding, AssignmentViewModel>() {

    override val viewModel: AssignmentViewModel by viewModels()

    private lateinit var listAdapter: AssignmentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.list.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAssignmentBinding =
        FragmentAssignmentBinding.inflate(layoutInflater, container, false)

    override fun init() {
        getSafeArgs()
        initRecyclerView()
    }

    private fun getSafeArgs() {
        viewModel.getList(parentViewModel.getLecture().id)
    }

    private fun initRecyclerView() {
        with(binding.rvAssignments) {
            listAdapter = AssignmentListAdapter().apply {
                listItemClickListener = { item ->
                    // todo item click listener
                }
            }
            adapter = listAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

}