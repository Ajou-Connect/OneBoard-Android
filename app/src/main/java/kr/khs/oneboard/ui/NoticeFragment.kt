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
import kr.khs.oneboard.utils.*
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
            listAdapter.submitList(
                if (UserInfoUtil.type == TYPE_PROFESSOR)
                    it
                else
                    it.filter { PatternUtil.convertTimeToLong(it.exposeDt) <= System.currentTimeMillis() }
            )
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNoticeBinding =
        FragmentNoticeBinding.inflate(layoutInflater, container, false)

    override fun init() {
        binding.viewTitle.root.text = "공지 사항 목록"
        getSafeArgs()
        initRecyclerView()
        initFAB()
    }

    private fun initFAB() {
        binding.fab.visibility = if (UserInfoUtil.type) View.VISIBLE else View.GONE

        if (UserInfoUtil.type == TYPE_PROFESSOR) {
            binding.rvNotices.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy <= 0)
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
                    findNavController().navigate(
                        if (UserInfoUtil.type == TYPE_PROFESSOR) {
                            NoticeFragmentDirections.actionNoticeFragmentToLectureWriteFragment(
                                TYPE_NOTICE
                            ).apply {
                                isEdit = true
                                notice = item
                            }
                        } else {
                            NoticeFragmentDirections.actionNoticeFragmentToLectureReadFragment(
                                TYPE_NOTICE
                            ).apply {
                                notice = item
                            }
                        }
                    )
                }
                listItemDeleteListener = { item ->
                    DialogUtil.createDialog(
                        requireContext(),
                        "삭제하시겠습니까?",
                        "네",
                        "아니오",
                        { viewModel.deleteItem(parentViewModel.getLecture().id, item.id) },
                        { }
                    )
                }
            }
            adapter = listAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.rvRefreshLayout.setOnRefreshListener {
            viewModel.getList(parentViewModel.getLecture().id)

            binding.rvRefreshLayout.isRefreshing = false
        }
    }

}