package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.databinding.ListItemGradeBinding

class GradeListAdapter : ListAdapter<GradeStudent, RecyclerView.ViewHolder>(GradeDiffCallBack()) {

    lateinit var itemClickListener: (GradeStudent) -> Unit

    class GradeViewHolder(
        private val binding: ListItemGradeBinding,
        private val itemClickListener: (GradeStudent) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    itemClickListener.invoke(item)
                }
            }
            binding.gradeDetailButton.setOnClickListener {
                binding.root.performClick()
            }
        }


        fun bind(item: GradeStudent) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: (GradeStudent) -> Unit
            ): GradeViewHolder {
                return GradeViewHolder(
                    ListItemGradeBinding.inflate(
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
        return GradeViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GradeViewHolder).bind(getItem(position))
    }

    override fun submitList(list: MutableList<GradeStudent>?) {
        super.submitList(list?.map { it.copy() })
    }

}

class GradeDiffCallBack : DiffUtil.ItemCallback<GradeStudent>() {
    override fun areItemsTheSame(oldItem: GradeStudent, newItem: GradeStudent): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: GradeStudent, newItem: GradeStudent): Boolean {
        return oldItem == newItem
    }

}
