package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.SubmitListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.FragmentContentDetailBinding
import kr.khs.oneboard.utils.TYPE_ASSIGNMENT
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.viewmodels.ContentDetailViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class ContentDetailFragment : BaseFragment<FragmentContentDetailBinding, ContentDetailViewModel>() {
    override val viewModel: ContentDetailViewModel by viewModels()
    private var type by Delegates.notNull<Boolean>()
    private lateinit var notice: Notice
    private lateinit var assignment: Assignment

    private lateinit var submitListAdapter: SubmitListAdapter

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

        viewModel.assignmentList.observe(viewLifecycleOwner) {
            submitListAdapter.submitList(it)
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContentDetailBinding =
        FragmentContentDetailBinding.inflate(inflater, container, false)

    override fun init() {
        arguments?.let {
            it.getParcelable<Notice>("notice")?.let { notice ->
                this.notice = notice
            }
            it.getParcelable<Assignment>("assignment")?.let { assignment ->
                this.assignment = assignment
            }
            when {
                this::notice.isInitialized -> type = TYPE_NOTICE
                this::assignment.isInitialized -> type = TYPE_ASSIGNMENT
                else -> goBackWhenError()
            }
        } ?: goBackWhenError()

        initViews()
    }

    private fun initViews() {
        if (type == TYPE_NOTICE) {
            binding.contentDetailTitle.text = notice.title
            binding.contentDetailContent.text = notice.content
            binding.contentDetailWriter.text = "작성자"
            binding.contentDetailDate.text = notice.exposeDt
        } else {
            binding.contentDetailTitle.text = assignment.title
            binding.contentDetailContent.text = assignment.content
            binding.contentDetailWriter.text = "작성자"
            binding.contentDetailDate.text = assignment.exposeDt
            binding.contentDetailAssignmentList.visibility = View.VISIBLE
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        with(binding.contentDetailAssignmentList) {
            submitListAdapter = SubmitListAdapter().apply {
                listItemClickListener = { item ->
                    findNavController().navigate(
                        ContentDetailFragmentDirections.actionContentDetailFragmentToSubmitDetailFragment(
                            item
                        )
                    )
                }
            }
            adapter = submitListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        viewModel.getSubmitList(assignment.id)
    }
}