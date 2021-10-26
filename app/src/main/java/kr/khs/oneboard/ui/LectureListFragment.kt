package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.LectureListAdapter
import kr.khs.oneboard.databinding.FragmentLectureListBinding
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.LectureListViewModel

// todo BaseFragment 생성
@AndroidEntryPoint
class LectureListFragment : Fragment() {
    private var _binding: FragmentLectureListBinding? = null
    private val binding: FragmentLectureListBinding
        get() = _binding!!
    private val viewModel: LectureListViewModel by viewModels()

    private lateinit var lectureListAdapter: LectureListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLectureListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLectureListRecyclerView()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}