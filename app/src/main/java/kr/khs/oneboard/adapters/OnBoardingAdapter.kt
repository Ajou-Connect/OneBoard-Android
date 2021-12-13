package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ViewOnboardingBinding
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil

class OnBoardingAdapter : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {
    private val items = arrayListOf(
        R.drawable.onboarding_lecture_list,
        R.drawable.onboarding_lecture_detail,
        R.drawable.onboarding_navigation_menu,
        R.drawable.onboarding_lecture_plan,
    )

    init {
        if (UserInfoUtil.type == TYPE_PROFESSOR)
            items.addAll(
                arrayOf(
                    R.drawable.onboarding_notice_list_professor,
                    R.drawable.onboarding_notice_write
                )
            )
        else
            items.add(R.drawable.onboarding_notice_list_student)

        if (UserInfoUtil.type == TYPE_PROFESSOR)
            items.addAll(
                arrayOf(
                    R.drawable.onboarding_lesson_list_professor,
                    R.drawable.onboarding_lesson_write
                )
            )
        else
            items.add(R.drawable.onboarding_lesson_list)

        items.add(R.drawable.onboarding_lesson_detail)

        if (UserInfoUtil.type == TYPE_PROFESSOR)
            items.add(R.drawable.onboarding_attendance_professor)
        else
            items.add(R.drawable.onboarding_attendance_student)

        if (UserInfoUtil.type == TYPE_PROFESSOR)
            items.add(R.drawable.onboarding_grade_professor)
        items.add(R.drawable.onboarding_grade_student)
    }

    class OnBoardingViewHolder(private val binding: ViewOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resId: Int) {
            Glide.with(binding.root).load(resId).into(binding.root)
        }

        companion object {
            fun from(parent: ViewGroup): OnBoardingViewHolder = OnBoardingViewHolder(
                ViewOnboardingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}