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
import kr.khs.oneboard.adapters.LessonListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentLessonListBinding
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.TYPE_STUDENT
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.LessonListViewModel

@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding, LessonListViewModel>() {
    override val viewModel: LessonListViewModel by viewModels()

    private lateinit var lessonListAdapter: LessonListAdapter

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

        viewModel.lessonList.observe(viewLifecycleOwner) {
            lessonListAdapter.submitList(it)
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLessonListBinding = FragmentLessonListBinding.inflate(inflater, container, false)

    override fun init() {
        initRecyclerView()
        initFab()
    }

    private fun initFab() {
        if (UserInfoUtil.type == TYPE_STUDENT) {
            binding.fab.visibility = View.GONE
            return
        }

        binding.rvLessonList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0)
                    binding.fab.show()
                else
                    binding.fab.hide()
            }
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(LessonListFragmentDirections.actionLessonListFragmentToLessonWriteFragment())
        }
    }

    private fun initRecyclerView() {
        with(binding.rvLessonList) {
            lessonListAdapter = LessonListAdapter().apply {
                itemClickListener = { item ->
                    findNavController().navigate(
                        LessonListFragmentDirections.actionLessonListFragmentToLessonDetailFragment(
                            item
                        )
                    )
                }
                optionClickListener = { item ->
                    DialogUtil.createDialog(
                        requireContext(),
                        "수정, 삭제 선택해주세요.\n(취소 : 외부 클릭)",
                        "수정",
                        "삭제",
                        {
                            findNavController().navigate(
                                LessonListFragmentDirections.actionLessonListFragmentToLessonWriteFragment()
                                    .apply {
                                        this.item = item.toLessonUpdateRequestDto()
                                        this.lessonId = item.lessonId
                                    })
                        },
                        { viewModel.deleteItem(parentViewModel.getLecture().id, item.lessonId) }
                    )

                }
            }
            adapter = lessonListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        viewModel.getLessonList(parentViewModel.getLecture().id)
    }
}