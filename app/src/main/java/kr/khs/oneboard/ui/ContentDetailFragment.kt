package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.SubmitListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.databinding.FragmentContentDetailBinding
import kr.khs.oneboard.utils.getFileUrl
import kr.khs.oneboard.viewmodels.ContentDetailViewModel

@AndroidEntryPoint
class ContentDetailFragment : BaseFragment<FragmentContentDetailBinding, ContentDetailViewModel>() {
    override val viewModel: ContentDetailViewModel by viewModels()
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
        binding.viewTitle.root.text = "과제 출제 상세"
        arguments?.let {
            it.getParcelable<Assignment>("assignment")?.let { assignment ->
                this.assignment = assignment
            }
        } ?: goBackWhenError()

        initViews()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.contentDetailTitle.text = assignment.title
        binding.contentDetailContent.text =
            Html.fromHtml(assignment.content, Html.FROM_HTML_MODE_LEGACY)
        binding.contentDetailDate.text = assignment.exposeDt
        binding.contentDetailAssignmentList.visibility = View.VISIBLE
        assignment.fileUrl?.let { fileUrl ->
            binding.contentDetailFileUrl.visibility = View.VISIBLE
            binding.contentDetailFileUrl.text = "${fileUrl.split("/").last()} 과제 파일 확인하기"

            binding.contentDetailFileUrl.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl.getFileUrl()))
                )
            }
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        with(binding.contentDetailAssignmentList) {
            submitListAdapter = SubmitListAdapter().apply {
                listItemClickListener = { item ->
                    // TODO: 2021/11/22 추후에 피드백 작성 기능 추가
//                    findNavController().navigate(
//                        ContentDetailFragmentDirections.actionContentDetailFragmentToSubmitDetailFragment(
//                            item
//                        )
//                    )
                }
            }
            adapter = submitListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        viewModel.getSubmitList(parentViewModel.getLecture().id, assignment.id)
    }
}