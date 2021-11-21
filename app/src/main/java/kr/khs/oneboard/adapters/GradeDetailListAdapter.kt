package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.databinding.ListItemGradeDetailBinding

class GradeDetailListAdapter :
    ListAdapter<Pair<String, Int>, RecyclerView.ViewHolder>(GradeDetailDiffUtil()) {

    class GradeDetailViewHolder(private val binding: ListItemGradeDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, Int>) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): GradeDetailViewHolder {
                return GradeDetailViewHolder(
                    ListItemGradeDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GradeDetailViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GradeDetailViewHolder).bind(getItem(position))
    }
}

class GradeDetailDiffUtil : DiffUtil.ItemCallback<Pair<String, Int>>() {
    override fun areItemsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>): Boolean {
        return oldItem.first == newItem.first
    }

    override fun areContentsTheSame(
        oldItem: Pair<String, Int>,
        newItem: Pair<String, Int>
    ): Boolean {
        return oldItem == newItem
    }

}