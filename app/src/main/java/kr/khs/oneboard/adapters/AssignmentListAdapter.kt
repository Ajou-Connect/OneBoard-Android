package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.databinding.ListItemAssignmentBinding

class AssignmentListAdapter :
    ListAdapter<Assignment, RecyclerView.ViewHolder>(AssignmentDiffUtil()) {

    lateinit var listItemClickListener: (Assignment) -> Unit

    class AssignmentViewHolder(
        private val binding: ListItemAssignmentBinding,
        private val listItemClickListener: (Assignment) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.item?.let {
                    listItemClickListener.invoke(it)
                }
            }
        }

        fun bind(item: Assignment) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listItemClickListener: (Assignment) -> Unit
            ): AssignmentViewHolder {
                return AssignmentViewHolder(
                    ListItemAssignmentBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ),
                        parent,
                        false
                    ),
                    listItemClickListener
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AssignmentViewHolder.from(parent, listItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AssignmentViewHolder).bind(getItem(position))
    }
}

class AssignmentDiffUtil : DiffUtil.ItemCallback<Assignment>() {
    override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
        return oldItem == newItem
    }

}

