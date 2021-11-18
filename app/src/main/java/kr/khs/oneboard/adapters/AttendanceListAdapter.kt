package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import kr.khs.oneboard.data.AttendanceLesson
import kr.khs.oneboard.data.AttendanceStudent
import kr.khs.oneboard.databinding.ListItemAttendanceStudentBinding
import kr.khs.oneboard.utils.collapse
import kr.khs.oneboard.utils.countRatio
import kr.khs.oneboard.utils.expand
import timber.log.Timber

class AttendanceListAdapter :
    ListAdapter<AttendanceStudent, RecyclerView.ViewHolder>(AttendanceDiffUtil()) {

    lateinit var onStateChange: (AttendanceLesson) -> Unit

    class AttendanceViewHolder(
        private val binding: ListItemAttendanceStudentBinding,
        private val onStateChange: (AttendanceLesson) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding.attendanceList) {
                adapter = AttendanceLessonListAdapter().apply {
                    onStateChange =
                        this@AttendanceViewHolder.onStateChange
                }
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            }

            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    Timber.tag("AttendanceExpand").d("list size : ${item.attendanceList.size}")
                    if (item.isExpanded)
                        expandLess()
                    else
                        expandMore()
                    item.isExpanded = !item.isExpanded
                }
            }

            binding.attendanceExpandButton.setOnClickListener {
                binding.item?.let { item ->
                    Timber.tag("AttendanceExpand").d("list size : ${item.attendanceList.size}")
                    if (item.isExpanded)
                        expandLess()
                    else
                        expandMore()
                    item.isExpanded = !item.isExpanded
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
            Timber.tag("Lesson${item.studentId}").d("$item")
            binding.item = item
            (binding.attendanceList.adapter as AttendanceLessonListAdapter).submitList(item.attendanceList.toMutableList())
            binding.attendanceRatio.text = item.attendanceList.countRatio()
            binding.executePendingBindings()
            if (item.isExpanded) expandMore() else expandLess()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onStateChange: (AttendanceLesson) -> Unit
            ): AttendanceViewHolder {
                return AttendanceViewHolder(
                    ListItemAttendanceStudentBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    ),
                    onStateChange
                )
            }
        }
    }

    override fun submitList(list: MutableList<AttendanceStudent>?) {
        super.submitList(list?.let { it.map { item -> item.copy() } })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendanceViewHolder.from(parent, onStateChange)
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
