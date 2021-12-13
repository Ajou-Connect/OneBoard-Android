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

fun Fragment.createOnBoardingDialog(type: Boolean? = null) {
    val binding = DialogOnboardingBinding.inflate(layoutInflater)

    val builder = MaterialAlertDialogBuilder(requireContext())
        .setView(binding.root)
        .setCancelable(false)

    val drawableRes = when (this.javaClass.simpleName) {
        "AnalysisFragment" -> R.drawable.onboarding_sample1
        "AssignmentFragment" -> {
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                R.drawable.onboarding_assignment_list_professor
            else
                R.drawable.onboarding_assignment_list_student
        }
        "AttendanceFragment" -> {
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                R.drawable.onboarding_attendance_professor
            else
                R.drawable.onboarding_attendance_student
        }
        "GradeProfessorFragment" -> R.drawable.onboarding_grade_professor
        "GradeStudentFragment" -> R.drawable.onboarding_grade_student
        "LectureDetailFragment" -> R.drawable.onboarding_lecture_detail
        "LecturePlanFragment" -> R.drawable.onboarding_lecture_plan
        "LectureWriteFragment" -> {
            if (type == TYPE_NOTICE)
                R.drawable.onboarding_notice_write
            else
                R.drawable.onboarding_assignment_write
        }
        "LessonDetailFragment" -> R.drawable.onboarding_lesson_detail
        "LessonListFragment" -> {
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                R.drawable.onboarding_lesson_list_professor
            else
                R.drawable.onboarding_lesson_list
        }
        "LessonWriteFragment" -> R.drawable.onboarding_lesson_write
        "NoticeFragment" -> {
            if (UserInfoUtil.type == TYPE_PROFESSOR)
                R.drawable.onboarding_notice_list_professor
            else
                R.drawable.onboarding_notice_list_student
        }
        "SubmitDetailFragment" -> R.drawable.onboarding_assignment_detail
        else -> R.drawable.onboarding_sample1
    }
    Glide.with(this)
        .load(drawableRes)
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