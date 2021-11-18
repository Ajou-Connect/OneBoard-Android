package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.databinding.ListItemAttendanceLessonBinding

class AttendanceLessonListAdapter(private val clickable: Boolean = false) :
    ListAdapter<AttendanceLesson, RecyclerView.ViewHolder>(AttendanceLessonDiffUtil()) {

    lateinit var onLessonStatusChange: (AttendanceLesson) -> Unit

    class AttendanceLessonViewHolder(
        private val binding: ListItemAttendanceLessonBinding,
        private val onLessonStatusChange: (AttendanceLesson) -> Unit,
        private val clickable: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.attendanceLessonCheckbox.onStateChanged = { _, state ->
                binding.item?.let { item ->
                    onLessonStatusChange.invoke(item.apply { status = state })
                }
            }
            binding.attendanceLessonCheckbox.isClickable = clickable
        }

        fun bind(item: AttendanceLesson) {
            binding.item = item
            binding.attendanceLessonCheckbox.state = item.status
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onLessonStatusChange: (AttendanceLesson) -> Unit,
                clickable: Boolean
            ): AttendanceLessonViewHolder {
                return AttendanceLessonViewHolder(
                    ListItemAttendanceLessonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onLessonStatusChange,
                    clickable
                )
            }
        }
    }

    override fun submitList(list: MutableList<AttendanceLesson>?) {
        super.submitList(list?.let { it.map { item -> item.copy() } })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceLessonListAdapter.AttendanceLessonViewHolder.from(
            parent,
            onLessonStatusChange,
            clickable
        )
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
        return oldItem == newItem
    }

}