package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.databinding.ListItemAttendanceStudentBinding
import kr.khs.oneboard.utils.collapse
import kr.khs.oneboard.utils.expand
import timber.log.Timber

class AttendanceListAdapter :
    ListAdapter<AttendanceStudent, RecyclerView.ViewHolder>(AttendanceDiffUtil()) {
    class AttendanceViewHolder(private val binding: ListItemAttendanceStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding.attendanceList) {
                adapter = AttendanceLessonListAdapter()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            }

            binding.attendanceExpandButton.setOnClickListener {
                binding.item?.let { item ->
                    Timber.tag("AttendanceExpand").d("list size : ${item.lessonList.size}")
                    if (item.isExpand)
                        expandLess()
                    else
                        expandMore()
                    item.isExpand = !item.isExpand
                }
            }
        }

        private fun expandMore() {
            Timber.tag("AttendanceExpand").d("1")
            binding.attendanceExpandButton.animate().rotation(180f)
            binding.attendanceList.expand()
        }

        private fun expandLess() {
            Timber.tag("AttendanceExpand").d("2")
            binding.attendanceExpandButton.animate().rotation(0f)
            binding.attendanceList.collapse()
        }

        fun bind(item: AttendanceStudent) {
            binding.item = item
            (binding.attendanceList.adapter as AttendanceLessonListAdapter).submitList(item.lessonList)
            binding.executePendingBindings()
            if (item.isExpand) expandLess()
        }

        companion object {
            fun from(parent: ViewGroup): AttendanceViewHolder {
                return AttendanceViewHolder(
                    ListItemAttendanceStudentBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AttendanceViewHolder).bind(getItem(position))
    }
}

class AttendanceDiffUtil : DiffUtil.ItemCallback<AttendanceStudent>() {
    override fun areItemsTheSame(oldItem: AttendanceStudent, newItem: AttendanceStudent): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(
        oldItem: AttendanceStudent,
        newItem: AttendanceStudent
    ): Boolean {
        return oldItem == newItem
    }

}