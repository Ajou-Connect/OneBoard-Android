package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Assignment
import kr.khs.oneboard.data.LectureBase
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.ListItemAssignmentBinding
import kr.khs.oneboard.databinding.ListItemNoticeBinding

private const val TYPE_ASSIGNMENT = 1
private const val TYPE_NOTICE = 2

class LectureBaseListAdapter :
    ListAdapter<LectureBase, RecyclerView.ViewHolder>(LectureBaseDiffUtil()) {

    lateinit var listItemClickListener: (LectureBase) -> Unit

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

    class NoticeViewHolder(
        private val binding: ListItemNoticeBinding,
        private val listItemClickListener: (Notice) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.item?.let {
                    listItemClickListener.invoke(it)
                }
            }
        }

        fun bind(item: Notice) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listItemClickListener: (Notice) -> Unit
            ): NoticeViewHolder {
                return NoticeViewHolder(
                    ListItemNoticeBinding.inflate(
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
        return when (viewType) {
            TYPE_ASSIGNMENT -> AssignmentViewHolder.from(parent, listItemClickListener)
            TYPE_NOTICE -> NoticeViewHolder.from(parent, listItemClickListener)
            else -> {
                throw Exception("Unknown Type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AssignmentViewHolder -> holder.bind(getItem(position) as Assignment)
            is NoticeViewHolder -> holder.bind(getItem(position) as Notice)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Assignment -> TYPE_ASSIGNMENT
            is Notice -> TYPE_NOTICE
            else -> {
                throw Exception("Unknown Type")
            }
        }
    }
}

class LectureBaseDiffUtil : DiffUtil.ItemCallback<LectureBase>() {
    override fun areItemsTheSame(oldItem: LectureBase, newItem: LectureBase): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LectureBase, newItem: LectureBase): Boolean {
        return if (oldItem is Assignment && newItem is Assignment)
            oldItem == newItem
        else
            oldItem as Notice == newItem as Notice
    }

}

