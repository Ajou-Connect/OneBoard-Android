package kr.khs.oneboard.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.DialogOnboardingBinding

private const val SPF_NAME = "OnBoarding"

fun Fragment.getOnBoardingSpf(fragmentName: String): Boolean =
    getSPF().getBoolean(fragmentName, false)

fun Fragment.setOnBoardingSpf(fragmentName: String, value: Boolean = true) {
    val editor = getSPF().edit()
    editor.putBoolean(fragmentName, value)
    editor.apply()
}

private fun Fragment.getSPF(): SharedPreferences =
    requireContext().getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)

fun Fragment.createOnBoardingDialog() {
    val binding = DialogOnboardingBinding.inflate(layoutInflater)

    val builder = MaterialAlertDialogBuilder(requireContext())
        .setView(binding.root)

    Glide.with(this)
        .load(R.drawable.onboarding_sample1)
        .into(binding.dialogOnBoardingImageView)

    val dialog = builder.create()

    binding.dialogOnBoardingOKButton.setOnClickListener {
        dialog.dismiss()
        setOnBoardingSpf(this.javaClass.simpleName)
    }

    val layoutParams = WindowManager.LayoutParams().apply {
        copyFrom(dialog.window?.attributes)
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    }

    dialog.window?.attributes = layoutParams

    dialog.show()
}