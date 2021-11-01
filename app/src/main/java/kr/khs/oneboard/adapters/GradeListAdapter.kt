package kr.khs.oneboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import kr.khs.oneboard.data.GradeStudent
import kr.khs.oneboard.databinding.ListItemGradeBinding
import kr.khs.oneboard.utils.collapse
import kr.khs.oneboard.utils.expand

class GradeListAdapter : ListAdapter<GradeStudent, RecyclerView.ViewHolder>(GradeDiffCallBack()) {

    private val isClicked = hashMapOf<GradeStudent, Boolean>()

    private val itemClickListener: (GradeStudent) -> Unit = { item ->
        isClicked[item] = !isClicked[item]!!
    }

    private val isItemClicked: (GradeStudent) -> Boolean = { item ->
        isClicked[item]!!
    }

    class GradeViewHolder(
        private val binding: ListItemGradeBinding,
        private val itemClickListener: (GradeStudent) -> Unit,
        private val isItemClicked: (GradeStudent) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding.gradeList) {
                adapter = GradeDetailListAdapter()
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            }
            binding.root.setOnClickListener {
                binding.item?.let { item ->
                    itemClickListener.invoke(item)
                    if (isItemClicked.invoke(item))
                        expandLess()
                    else
                        expandMore()
                }
            }
            binding.gradeExpandButton.setOnClickListener {
                binding.root.performClick()
            }
        }

        private fun expandMore() {
            binding.gradeExpandButton.animate().rotation(180f)
            binding.gradeList.expand()
        }

        private fun expandLess() {
            binding.gradeExpandButton.animate().rotation(0f)
            binding.gradeList.collapse()
        }

        fun bind(item: GradeStudent) {
            binding.item = item
            (binding.gradeList.adapter as GradeDetailListAdapter).submitList(item.assignmentList)
            with(binding.gradeScore) {
                visibility = if (item.score != null) View.VISIBLE else View.GONE
                item.score ?: return@with
                text = item.score.toString()
            }
            if (isItemClicked(item)) expandMore() else expandLess()
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: (GradeStudent) -> Unit,
                isItemClicked: (GradeStudent) -> Boolean
            ): GradeViewHolder {
                return GradeViewHolder(
                    ListItemGradeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    itemClickListener,
                    isItemClicked
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GradeViewHolder.from(parent, itemClickListener, isItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GradeViewHolder).bind(getItem(position))
    }

    override fun submitList(list: MutableList<GradeStudent>?) {
        list?.map { if (isClicked[it] != true) isClicked[it] = false }
        super.submitList(list?.map { it.copy() })
    }

}

class GradeDiffCallBack : DiffUtil.ItemCallback<GradeStudent>() {
    override fun areItemsTheSame(oldItem: GradeStudent, newItem: GradeStudent): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GradeStudent, newItem: GradeStudent): Boolean {
        TODO("Not yet implemented")
    }

}
