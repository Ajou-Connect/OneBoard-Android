package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.databinding.ListItemSubmitGradeBinding

class SubmitGradeListAdapter :
    ListAdapter<GradeStudent.GradeSubmit, RecyclerView.ViewHolder>(SubmitGradeDiffUtil()) {
    class SubmitGradeViewHolder(
        private val binding: ListItemSubmitGradeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GradeStudent.GradeSubmit) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): SubmitGradeViewHolder {
                return SubmitGradeViewHolder(
                    ListItemSubmitGradeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SubmitGradeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SubmitGradeViewHolder).bind(getItem(position))
    }
}

class SubmitGradeDiffUtil : DiffUtil.ItemCallback<GradeStudent.GradeSubmit>() {
    override fun areItemsTheSame(
        oldItem: GradeStudent.GradeSubmit,
        newItem: GradeStudent.GradeSubmit
    ): Boolean {
        return oldItem.submitId == newItem.submitId
    }

    override fun areContentsTheSame(
        oldItem: GradeStudent.GradeSubmit,
        newItem: GradeStudent.GradeSubmit
    ): Boolean {
        return oldItem == newItem
    }

}