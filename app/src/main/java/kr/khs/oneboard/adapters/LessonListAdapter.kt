package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.Lesson
import kr.khs.oneboard.databinding.ListItemLessonBinding
import kr.khs.oneboard.utils.TYPE_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_NON_FACE_TO_FACE
import kr.khs.oneboard.utils.TYPE_RECORDING

class LessonListAdapter : ListAdapter<Lesson, RecyclerView.ViewHolder>(LessonDiffUtil()) {

    lateinit var itemClickListener: (Lesson) -> Unit

    class LessonViewHolder(
        private val binding: ListItemLessonBinding,
        private val itemClickListener: (Lesson) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lesson) {
            binding.item = item
            item.note?.let {
                binding.lessonNote.visibility = View.VISIBLE
                binding.lessonNote.text = it
            }
            binding.lessonInfo.text = when (item.type) {
                TYPE_NON_FACE_TO_FACE -> item.meetingId ?: "아직 주소가 없습니다."
                TYPE_FACE_TO_FACE -> item.room ?: "아직 강의실이 정해지지 않았습니다."
                TYPE_RECORDING -> item.videoUrl ?: "아직 녹화 강의 주소가 올라오지 않았습니다."
                else -> {
                    binding.lessonInfo.visibility = View.GONE
                    ""
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, itemClickListener: (Lesson) -> Unit): LessonViewHolder {
                return LessonViewHolder(
                    ListItemLessonBinding.inflate(
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
        return LessonViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LessonViewHolder).bind(getItem(position))
    }
}

class LessonDiffUtil : DiffUtil.ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem == newItem
    }

}