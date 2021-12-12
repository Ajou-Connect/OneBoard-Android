package kr.khs.oneboard.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.core.BaseFragment
import kr.khs.oneboard.databinding.FragmentAnalysisBinding
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.createOnBoardingDialog
import kr.khs.oneboard.utils.getOnBoardingSpf
import kr.khs.oneboard.viewmodels.AnalysisViewModel
import kr.khs.oneboard.views.LineView
import kr.khs.oneboard.views.PieHelper

@AndroidEntryPoint
class AnalysisFragment : BaseFragment<FragmentAnalysisBinding, AnalysisViewModel>() {
    override val viewModel: AnalysisViewModel by viewModels()

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnalysisBinding = FragmentAnalysisBinding.inflate(layoutInflater)

    override fun init() {
        binding.viewTitle.root.text = "분석 정보 확인"

        initViews()
    }

    private fun initViews() {
        initPlot()
        initPie()
    }

    private fun initPie() {
        val pieHelperList = ArrayList<PieHelper>()
        pieHelperList.add(PieHelper(80f, "O", Color.BLUE))
        pieHelperList.add(PieHelper(20f, "X", Color.RED))

        with(binding.analysisPie) {
            setDate(pieHelperList)
            setOnPieClickListener {
                DialogUtil.createDialog(
                    requireContext(),
                    "${pieHelperList[it].title} : ${pieHelperList[it].percentStr}",
                    positiveText = "확인",
                    positiveAction = { }
                )
            }
        }
    }

    private fun initPlot() {
        val bottomTextList = ArrayList<String>()
        val sampleDataList = ArrayList<ArrayList<Int>>()
        val sample1 = ArrayList<Int>()
        val sample2 = ArrayList<Int>()
        val random = Math.random() * 9 + 1

        for (i in 0 until 10) {
            bottomTextList.add(i.toString())
            sample1.add((Math.random() * random).toInt())
            sample2.add((Math.random() * random).toInt())
        }

        sampleDataList.add(sample1)
        sampleDataList.add(sample2)

        with(binding.analysisPlot) {
            setDrawDotLine(true)
            setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY)
            setBottomTextList(bottomTextList)
            setColorArray(intArrayOf(Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN))
            setDataList(sampleDataList)
        }
    }

    override fun initOnBoarding() {
        if (getOnBoardingSpf(this.javaClass.simpleName).not()) {
            createOnBoardingDialog()
        }
    }
}