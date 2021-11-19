package kr.khs.oneboard.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

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