package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.LectureListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentLectureListBinding
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.LectureListViewModel

@AndroidEntryPoint
class LectureListFragment : BaseFragment<FragmentLectureListBinding, LectureListViewModel>() {
    override val viewModel: LectureListViewModel by viewModels()

    private lateinit var lectureListAdapter: LectureListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lectures.observe(viewLifecycleOwner) { list ->
            lectureListAdapter.submitList(list)
        }
    }

    private fun initLectureListRecyclerView() {
        with(binding.rvLectures) {
            lectureListAdapter = LectureListAdapter().apply {
                lectureClickListener = { item ->
                    ToastUtil.shortToast(requireContext(), "${item.title} - ${item.semester}")
                }
            }
            adapter = lectureListAdapter
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

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLectureListBinding = FragmentLectureListBinding.inflate(inflater, container, false)

    override fun init() {
        initLectureListRecyclerView()
    }
}