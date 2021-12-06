package kr.khs.oneboard.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.khs.oneboard.databinding.ItemChatMsgBinding
import us.zoom.sdk.ZoomVideoSDKChatMessage

class ChatMsgAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<ChatMsgAdapter.MsgHolder>() {
    interface ItemClickListener {
        fun onItemClick()
    }

    private val list = mutableListOf<CharSequence>()

    inner class MsgHolder(val binding: ItemChatMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun onReceive(item: ZoomVideoSDKChatMessage) {
        val senderName = "${item.senderUser.userName} : "
        val content = item.content

        val builder = SpannableStringBuilder().apply {
            append(senderName).append(content)
            setSpan(
                ForegroundColorSpan(Color.parseColor("#BABACC")),
                0,
                senderName.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(Color.parseColor("#FFFFFF")),
                senderName.length,
                length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }

        list.add(builder)

        notifyItemInserted(list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMsgAdapter.MsgHolder {
        return MsgHolder(
            ItemChatMsgBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatMsgAdapter.MsgHolder, position: Int) {
        holder.binding.chatMsgText.text = list[position]
    }

    override fun getItemCount() = list.size
}