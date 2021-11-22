package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.databinding.ListItemSubmitBinding

class SubmitListAdapter : ListAdapter<Submit, RecyclerView.ViewHolder>(SubmitDiffUtil()) {

    lateinit var listItemClickListener: (Submit) -> Unit

    class SubmitViewHolder(
        private val binding: ListItemSubmitBinding,
        private val listItemClickListener: (Submit) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    listItemClickListener.invoke(item)
                }
            }
        }

        fun bind(item: Submit) {
            binding.item = item
            item.fileUrl?.let { binding.submitFileUrl.text = it }
                ?: run { binding.submitFileUrl.visibility = View.GONE }
            item.score?.let { binding.submitScore.text = it.toString() }
                ?: run { binding.submitScore.visibility = View.GONE }
            item.feedback?.let { binding.submitFeedback.text = it }
                ?: run { binding.submitFeedback.visibility = View.GONE }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listItemClickListener: (Submit) -> Unit
            ): SubmitViewHolder {
                return SubmitViewHolder(
                    ListItemSubmitBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listItemClickListener
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SubmitListAdapter.SubmitViewHolder.from(parent, listItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SubmitListAdapter.SubmitViewHolder).bind(getItem(position))
    }
}

class SubmitDiffUtil : DiffUtil.ItemCallback<Submit>() {
    override fun areItemsTheSame(oldItem: Submit, newItem: Submit): Boolean {
        return oldItem.assignmentId == newItem.assignmentId
    }

    override fun areContentsTheSame(oldItem: Submit, newItem: Submit): Boolean {
        return oldItem == newItem
    }

}