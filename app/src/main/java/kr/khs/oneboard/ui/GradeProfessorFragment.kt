package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.adapters.GradeListAdapter
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentGradeProfessorBinding
import kr.khs.oneboard.utils.TYPE_STUDENT
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.GradeProfessorViewModel
import timber.log.Timber

@AndroidEntryPoint
class GradeProfessorFragment :
    BaseFragment<FragmentGradeProfessorBinding, GradeProfessorViewModel>() {
    override val viewModel: GradeProfessorViewModel by viewModels()

    private lateinit var gradeListAdapter: GradeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.aRatio.observe(viewLifecycleOwner) {
            it?.let { a ->
                Timber.tag("GradeRatio").d("a : $a")
                viewModel.bRatio.value?.let { b ->
                    if (a == "")
                        viewModel.setRatio(a = 0)
                    else if (a.toInt() > 100 || a.toInt() > b.toInt())
                        viewModel.setRatio(a = a.toInt() / 10)
                }
            }
        }
        viewModel.bRatio.observe(viewLifecycleOwner) {
            it?.let { b ->
                Timber.tag("GradeRatio").d("b : $b")
                viewModel.aRatio.value?.let { a ->
                    when {
                        b == "" -> viewModel.setRatio(b = 0)
                        b < a -> viewModel.setRatio(b = a.toInt())
                        b.toInt() > 100 -> viewModel.setRatio(b = b.toInt() / 10)
                    }

                    binding.gradeElseRatio.text =
                        if (b == "") "100" else (100 - b.toInt()).toString()
                }
            }
        }

        viewModel.gradeList.observe(viewLifecycleOwner) {
            gradeListAdapter.submitList(it.toMutableList())
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeProfessorBinding =
        FragmentGradeProfessorBinding.inflate(inflater, container, false)

    override fun init() {
        initRatioLayout()
        initGradeListRecyclerView()
    }

    private fun initGradeListRecyclerView() {
        with(binding.gradeStudentList) {
            gradeListAdapter = GradeListAdapter()
            adapter = gradeListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        viewModel.getGradeList(parentViewModel.getLecture().id)
    }

    private fun initRatioLayout() {
        if (UserInfoUtil.type == TYPE_STUDENT) {
            binding.gradeRatioLayout.visibility = View.VISIBLE
            return
        }

        viewModel.getRatio(parentViewModel.getLecture().id)
    }
}