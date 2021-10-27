package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.NoticeListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentNoticeBinding
import kr.khs.oneboard.viewmodels.NoticeViewModel

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding, NoticeViewModel>() {

    override val viewModel: NoticeViewModel by viewModels()

    private lateinit var listAdapter: NoticeListAdapter

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
    ): FragmentNoticeBinding =
        FragmentNoticeBinding.inflate(layoutInflater, container, false)

    override fun init() {
        getSafeArgs()
        initRecyclerView()
    }

    private fun getSafeArgs() {
        viewModel.getList(parentViewModel.getLecture().id)
    }

    private fun initRecyclerView() {
        with(binding.rvNotices) {
            listAdapter = NoticeListAdapter().apply {
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