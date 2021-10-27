package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.ListItemNoticeBinding

class NoticeListAdapter :
    ListAdapter<Notice, RecyclerView.ViewHolder>(NoticeDiffUtil()) {

    lateinit var listItemClickListener: (Notice) -> Unit

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
        return NoticeViewHolder.from(parent, listItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoticeViewHolder).bind(getItem(position))
    }
}

class NoticeDiffUtil : DiffUtil.ItemCallback<Notice>() {
    override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
        return oldItem == newItem
    }

}
