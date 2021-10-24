package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Lecture
import kr.khs.oneboard.databinding.ListItemLectureBinding

class LectureListAdapter : ListAdapter<Lecture, RecyclerView.ViewHolder>(LectureDiffUtil()) {
    lateinit var lectureClickListener: ListItemClickListener<Lecture>

    class LectureViewHolder(
        private val binding: ListItemLectureBinding,
        private val itemClickListener: ListItemClickListener<Lecture>
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.item?.let { item ->
                    itemClickListener.onItemClick(item)
                }
            }
        }

        fun bind(item: Lecture) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: ListItemClickListener<Lecture>
            ): LectureViewHolder {
                return LectureViewHolder(
                    ListItemLectureBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    itemClickListener
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LectureViewHolder.from(parent, lectureClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LectureViewHolder).bind(getItem(position))
    }
}

class LectureDiffUtil : DiffUtil.ItemCallback<Lecture>() {
    override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
        return oldItem == newItem
    }

}