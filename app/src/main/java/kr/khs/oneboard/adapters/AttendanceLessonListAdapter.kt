package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.databinding.ListItemAttendanceLessonBinding

class AttendanceLessonListAdapter :
    ListAdapter<AttendanceLesson, RecyclerView.ViewHolder>(AttendanceLessonDiffUtil()) {

    lateinit var onStateChange: (AttendanceLesson) -> Unit

    class AttendanceLessonViewHolder(
        private val binding: ListItemAttendanceLessonBinding,
        private val onStateChange: (AttendanceLesson) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.attendanceLessonCheckbox.onStateChanged = { threeStateCheckBox, state ->
                binding.item?.let {
                    onStateChange.invoke(it.apply { status = state })
                }
            }
        }

        fun bind(item: AttendanceLesson) {
            binding.item = item
            binding.attendanceLessonCheckbox.state = item.status
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onStateChange: (AttendanceLesson) -> Unit
            ): AttendanceLessonViewHolder {
                return AttendanceLessonViewHolder(
                    ListItemAttendanceLessonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onStateChange
                )
            }
        }
    }

    override fun submitList(list: MutableList<AttendanceLesson>?) {
        super.submitList(list?.let { it.map { item -> item.copy() } })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceLessonListAdapter.AttendanceLessonViewHolder.from(parent, onStateChange)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AttendanceLessonListAdapter.AttendanceLessonViewHolder).bind(getItem(position))
    }

}

class AttendanceLessonDiffUtil : DiffUtil.ItemCallback<AttendanceLesson>() {
    override fun areItemsTheSame(oldItem: AttendanceLesson, newItem: AttendanceLesson): Boolean {
        return oldItem.lessonId == newItem.lessonId
    }

    override fun areContentsTheSame(oldItem: AttendanceLesson, newItem: AttendanceLesson): Boolean {
        return oldItem.status == newItem.status
    }

}