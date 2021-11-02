package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.AssignmentListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentAssignmentBinding
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
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
        initFAB()
    }

    private fun initFAB() {
        binding.fab.visibility = if (UserInfoUtil.type) View.VISIBLE else View.GONE

        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            binding.rvAssignments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy <= 0)
                        binding.fab.show()
                    else if (dy > 0)
                        binding.fab.hide()
                }
            })
            binding.fab.setOnClickListener {
                findNavController().navigate(
                    AssignmentFragmentDirections.actionAssignmentFragmentToLectureWriteFragment(
                        TYPE_ASSIGNMENT
                    )
                )
            }
        }
    }

    private fun getSafeArgs() {
        viewModel.getList(parentViewModel.getLecture().id)
    }

    private fun initRecyclerView() {
        with(binding.rvAssignments) {
            listAdapter = AssignmentListAdapter().apply {
                listItemClickListener = { item ->
                    findNavController().navigate(
                        AssignmentFragmentDirections.actionAssignmentFragmentToContentDetailFragment()
                            .apply {
                                assignment = item
                            }
                    )
                }
                listItemDeleteListener = { item ->
                    DialogUtil.createDialog(
                        requireContext(),
                        "삭제하시겠습니까?",
                        "네",
                        "아니오",
                        { viewModel.deleteItem(item) },
                        { }
                    )
                }
            }
            adapter = listAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

}