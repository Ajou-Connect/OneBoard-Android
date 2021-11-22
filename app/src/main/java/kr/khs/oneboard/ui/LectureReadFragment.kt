package kr.khs.oneboard.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.databinding.FragmentLectureReadBinding
import kr.khs.oneboard.databinding.ViewAssignmentDetailBinding
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.viewmodels.LectureReadViewModel
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class LectureReadFragment : BaseFragment<FragmentLectureReadBinding, LectureReadViewModel>() {
    override val viewModel: LectureReadViewModel by viewModels()
    private var type by Delegates.notNull<Boolean>()
    private var notice: Notice? = null
    private var assignment: Assignment? = null
    private val assignmentBinding: ViewAssignmentDetailBinding by lazy {
        binding.readSubmitAssignmentView
    }

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

        viewModel.isSubmit.observe(viewLifecycleOwner) {
            binding.readNoSubmitData.visibility = if (it) View.GONE else View.VISIBLE
            if (it.not())
                binding.readNoSubmitData.text = "과제를 제출하지 않았습니다."
        }

        viewModel.assignmentData.observe(viewLifecycleOwner) {
            Timber.tag("AssignmentData").d("$it")
            it?.let { submitData ->
                assignmentBinding.root.visibility = View.VISIBLE
                setAssignmentData(submitData)
            } ?: run {
                assignmentBinding.root.visibility = View.GONE
            }
        }
    }

    private fun setAssignmentData(item: Submit) {
        with(assignmentBinding) {
            if (item.score != null) {
                assignmentDetailScore.text =
                    String.format(getString(R.string.assignment_submit_score), item.score)
                assignmentDetailFeedback.text = item.feedback
            } else {
                assignmentDetailScore.visibility = View.GONE
                assignmentDetailFeedback.visibility = View.GONE
            }

            assignmentDetailContent.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_LEGACY)

            item.fileUrl?.let {
                assignmentDetailFileUrl.text = it
            } ?: run { assignmentDetailFileUrl.visibility = View.GONE }
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLectureReadBinding = FragmentLectureReadBinding.inflate(inflater, container, false)

    override fun init() {
        arguments?.let {
            type = it.getBoolean("type")
            if (type == TYPE_NOTICE)
                notice = it.getParcelable("notice")
            else
                assignment = it.getParcelable("assignment")
        } ?: goBackWhenError()

        initView()
    }

    private fun initView() {
        notice?.let { item ->
            binding.readTitle.text = item.title
            binding.readTime.text = item.exposeDt
            binding.readContent.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_LEGACY)
        } ?: assignment?.let { item ->
            binding.readTitle.text = item.title
            binding.readTime.text = item.exposeDt
            binding.readStartDT.visibility = View.VISIBLE
            binding.readStartDT.text = item.startDt
            binding.readEndDT.visibility = View.VISIBLE
            binding.readEndDT.text = item.endDt
            if (item.fileUrl != "") {
                binding.readFileUrl.visibility = View.VISIBLE
                binding.readFileUrl.text = item.fileUrl
            }
            binding.readContent.text = Html.fromHtml(item.content, Html.FROM_HTML_MODE_LEGACY)

            viewModel.getAssignmentSubmitInfo(parentViewModel.getLecture().id, item.id)
        }
    }
}