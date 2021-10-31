package kr.khs.oneboard.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentGradeBinding
import kr.khs.oneboard.utils.TYPE_STUDENT
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.GradeViewModel
import timber.log.Timber

@AndroidEntryPoint
class GradeFragment : BaseFragment<FragmentGradeBinding, GradeViewModel>() {
    override val viewModel: GradeViewModel by viewModels()

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

        viewModel.aRatio.observe(viewLifecycleOwner) {
            if (it > 100)
                binding.gradeARatio.setText((it / 10).toString())
        }
        viewModel.bRatio.observe(viewLifecycleOwner) {
            if (it <= 100) {
                binding.gradeElseRatio.text = (100 - it).toString()
            } else {
                binding.gradeBRatio.setText((it / 10).toString())
            }
        }
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGradeBinding = FragmentGradeBinding.inflate(inflater, container, false)

    override fun init() {
        initRatioLayout()
    }

    private fun initRatioLayout() {
        if (UserInfoUtil.type == TYPE_STUDENT) {
            binding.gradeRatioLayout.visibility = View.VISIBLE
            return
        }

        viewModel.getRatio(parentViewModel.getLecture().id) { a, b ->
            binding.gradeARatio.setText(a.toString())
            binding.gradeBRatio.setText(b.toString())
        }

        binding.gradeARatio.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()) {
                        viewModel.setRatio(a = s.toString().toInt())
                    }
                }

            })
        binding.gradeBRatio.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    Timber.tag("TextWatcher").d("Before : ${s.toString()}, $start, $count, $after")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Timber.tag("TextWatcher").d("On : ${s.toString()}, $start, $count")

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()) {
                        viewModel.setRatio(b = s.toString().toInt())
                    }
                }

            })
    }
}