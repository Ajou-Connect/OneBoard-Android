package kr.khs.oneboard.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kr.khs.oneboard.utils.DialogUtil

abstract class BaseFragment<T : ViewBinding, VM : BaseViewModel> : Fragment() {
    private var _binding: T? = null
    protected val binding: T
        get() = _binding!!

    abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentViewBinding(inflater, container)

        init()

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
    }

    abstract fun getFragmentViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun init()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}