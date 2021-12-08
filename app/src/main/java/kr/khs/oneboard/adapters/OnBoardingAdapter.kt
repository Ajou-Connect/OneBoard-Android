package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ViewOnboardingBinding

class OnBoardingAdapter : RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {
    private val items = listOf(
        R.drawable.onboarding_sample1,
        R.drawable.onboarding_sample2,
        R.drawable.onboarding_sample3
    )

    class ViewHolder(private val binding: ViewOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resId: Int) {
            Glide.with(binding.root).load(resId).into(binding.root)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder = ViewHolder(
                ViewOnboardingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}