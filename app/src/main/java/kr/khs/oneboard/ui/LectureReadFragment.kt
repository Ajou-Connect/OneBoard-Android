package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.FragmentLectureReadBinding
import kr.khs.oneboard.utils.TYPE_NOTICE
import kr.khs.oneboard.viewmodels.LectureReadViewModel
import kotlin.properties.Delegates

@AndroidEntryPoint
class LectureReadFragment : BaseFragment<FragmentLectureReadBinding, LectureReadViewModel>() {
    override val viewModel: LectureReadViewModel by viewModels()
    private var type by Delegates.notNull<Boolean>()
    private var notice: Notice? = null
    private var assignment: Assignment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
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
            binding.readContent.text = item.content
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
            binding.readContent.text = item.content
        }
    }
}