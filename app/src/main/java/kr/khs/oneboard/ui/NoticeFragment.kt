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
import kr.khs.oneboard.adapters.NoticeListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentNoticeBinding
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil
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
        initFAB()
    }

    private fun initFAB() {
        binding.fab.visibility = if (UserInfoUtil.type) View.VISIBLE else View.GONE

        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            binding.rvNotices.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0)
                        binding.fab.show()
                    else if (dy > 0)
                        binding.fab.hide()
                }
            })
            binding.fab.setOnClickListener {
                findNavController().navigate(
                    NoticeFragmentDirections.actionNoticeFragmentToLectureWriteFragment(
                        TYPE_NOTICE
                    )
                )
            }
        }
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