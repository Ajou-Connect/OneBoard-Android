package kr.khs.oneboard.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Notice
import kr.khs.oneboard.databinding.ListItemNoticeBinding
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.UserInfoUtil

class NoticeListAdapter :
    ListAdapter<Notice, RecyclerView.ViewHolder>(NoticeDiffUtil()) {

    lateinit var listItemClickListener: (Notice) -> Unit
    lateinit var listItemDeleteListener: (Notice) -> Unit

    class NoticeViewHolder(
        private val binding: ListItemNoticeBinding,
        private val listItemClickListener: (Notice) -> Unit,
        private val listItemDeleteListener: (Notice) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.item?.let {
                    listItemClickListener.invoke(it)
                }
            }

            if (UserInfoUtil.type == TYPE_PROFESSOR) {
                binding.listItemNoticeDelete.visibility = View.VISIBLE
                binding.listItemNoticeDelete.setOnClickListener {
                    binding.item?.let {
                        listItemDeleteListener.invoke(it)
                    }
                }
            } else {
                binding.listItemNoticeDelete.visibility = View.GONE
            }
        }

        fun bind(item: Notice) {
            binding.item = item
            binding.listItemNoticeContent.text =
                Html.fromHtml(item.content, Html.FROM_HTML_MODE_LEGACY)
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                listItemClickListener: (Notice) -> Unit,
                listItemDeleteListener: (Notice) -> Unit
            ): NoticeViewHolder {
                return NoticeViewHolder(
                    ListItemNoticeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listItemClickListener,
                    listItemDeleteListener
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoticeViewHolder.from(parent, listItemClickListener, listItemDeleteListener)
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
