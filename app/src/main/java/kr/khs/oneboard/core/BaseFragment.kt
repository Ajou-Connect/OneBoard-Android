package kr.khs.oneboard.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import kr.khs.oneboard.ui.MainActivity
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.ToastUtil
import kr.khs.oneboard.viewmodels.MainViewModel

abstract class BaseFragment<T : ViewBinding, VM : BaseViewModel> : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    protected val parentViewModel: MainViewModel by activityViewModels()

    abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = getFragmentViewBinding(inflater, container)

        init()
        initOnBoarding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                DialogUtil.onLoadingDialog(requireActivity())
            } else {
                DialogUtil.offLoadingDialog()
            }
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            if (it != "") {
                ToastUtil.shortToast(requireContext(), it)
                viewModel.setErrorMessage()
            }
        }
    }

    abstract fun getFragmentViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun init()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun inflateMenu(isDetail: Boolean = true) {
        (requireActivity() as MainActivity).inflateLectureMenu(isDetail)
    }

    fun goBackWhenError() {
        DialogUtil.createDialog(
            context = requireActivity(),
            message = "강의 정보를 불러오는데 실패했습니다.",
            positiveText = "뒤로가기",
            positiveAction = { requireActivity().onBackPressed() }
        )
    }

    abstract fun initOnBoarding()
}