package kr.khs.oneboard.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Submit
import kr.khs.oneboard.databinding.ListItemSubmitBinding
import kr.khs.oneboard.utils.getFileUrl

class SubmitListAdapter : ListAdapter<Submit, RecyclerView.ViewHolder>(SubmitDiffUtil()) {

    lateinit var listItemClickListener: (Submit) -> Unit

    class SubmitViewHolder(
        private val binding: ListItemSubmitBinding,
        private val listItemClickListener: (Submit) -> Unit,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    listItemClickListener.invoke(item)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Submit) {
            binding.item = item
            item.fileUrl?.let { fileUrl ->
                binding.submitFileUrl.setOnClickListener {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl.getFileUrl()))
                    )
                }
            }
                ?: run { binding.submitFileUrl.visibility = View.GONE }
            item.score?.let { binding.submitScore.text = String.format("* 점수 : %.1f", it) }
                ?: run { binding.submitScore.visibility = View.GONE }
            item.feedback?.let { binding.submitFeedback.text = "* 피드백\n$it" }
                ?: run { binding.submitFeedback.visibility = View.GONE }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listItemClickListener: (Submit) -> Unit,
                context: Context
            ): SubmitViewHolder {
                return SubmitViewHolder(
                    ListItemSubmitBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listItemClickListener,
                    context
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SubmitListAdapter.SubmitViewHolder.from(
            parent,
            listItemClickListener,
            parent.context
        )
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