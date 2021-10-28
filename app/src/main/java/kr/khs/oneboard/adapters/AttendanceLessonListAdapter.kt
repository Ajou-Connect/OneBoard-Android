package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.databinding.ListItemAttendanceLessonBinding
import timber.log.Timber

class AttendanceLessonListAdapter :
    ListAdapter<AttendanceLesson, RecyclerView.ViewHolder>(AttendanceLessonDiffUtil()) {

    class AttendanceLessonViewHolder(private val binding: ListItemAttendanceLessonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }

        fun bind(item: AttendanceLesson) {
            binding.item = item
            Timber.tag("AttendanceLesson Bind").d("$item")
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AttendanceLessonViewHolder {
                return AttendanceLessonViewHolder(
                    ListItemAttendanceLessonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceLessonViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AttendanceLessonViewHolder).bind(getItem(position))
    }

}

class AttendanceLessonDiffUtil : DiffUtil.ItemCallback<AttendanceLesson>() {
    override fun areItemsTheSame(oldItem: AttendanceLesson, newItem: AttendanceLesson): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AttendanceLesson, newItem: AttendanceLesson): Boolean {
        return oldItem == newItem
    }

}