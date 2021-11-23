package kr.khs.oneboard.utils

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewbinding.ViewBinding
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ViewSelectGradeBinding

fun View.expand(duration: Long = 300L) {
    measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )

    slideView(measuredHeight, duration)
}

fun View.collapse(duration: Long = 300L) {
    slideView(0, duration)
}

private fun View.slideView(newHeight: Int, duration: Long = 300L) {
    ValueAnimator.ofInt(height, newHeight).setDuration(duration).also {
        it.addUpdateListener { animation ->
            val currentHeight = animation.animatedValue as Int
            layoutParams.height = currentHeight
            requestLayout()
        }
        it.interpolator = AccelerateDecelerateInterpolator()
    }.start()
}

fun ViewBinding.setSelectColor(resId: Int) {
    val dialogBinding = (this as ViewSelectGradeBinding)
    dialogBinding.selectGradeAplus.setBackgroundColor(
        if (resId == R.id.selectGradeAplus)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeA.setBackgroundColor(
        if (resId == R.id.selectGradeA)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeBplus.setBackgroundColor(
        if (resId == R.id.selectGradeBplus)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeB.setBackgroundColor(
        if (resId == R.id.selectGradeB)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeCplus.setBackgroundColor(
        if (resId == R.id.selectGradeCplus)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeC.setBackgroundColor(
        if (resId == R.id.selectGradeC)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeD.setBackgroundColor(
        if (resId == R.id.selectGradeD)
            Color.LTGRAY
        else
            Color.WHITE
    )

    dialogBinding.selectGradeF.setBackgroundColor(
        if (resId == R.id.selectGradeF)
            Color.LTGRAY
        else
            Color.WHITE
    )
}