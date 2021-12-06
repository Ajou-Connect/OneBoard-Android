package kr.khs.oneboard.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kr.khs.oneboard.R
import kr.khs.oneboard.core.zoom.BaseSessionActivity
import kr.khs.oneboard.databinding.ItemUserVideoBinding
import kr.khs.oneboard.utils.UserHelper
import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoAspect
import us.zoom.sdk.ZoomVideoSDKVideoResolution

class UserVideoAdapter(
    private val listener: ItemTapListener,
    private val renderType: Int
) : RecyclerView.Adapter<UserVideoAdapter.BaseHolder>() {
    interface ItemTapListener {
        fun onSingleTap(user: ZoomVideoSDKUser)
    }

    private val userList = mutableListOf<ZoomVideoSDKUser>()
    private var selectedVideoUser: ZoomVideoSDKUser? = null
    private var activeAudioList: List<ZoomVideoSDKUser>? = null

    open class BaseHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    inner class VideoHolder(val binding: ItemUserVideoBinding) : BaseHolder(binding) {
        lateinit var user: ZoomVideoSDKUser

        init {
            if (renderType == BaseSessionActivity.RENDER_TYPE_ZOOMRENDERER) {
                binding.videoRenderer.visibility = View.VISIBLE
                binding.videoRenderer.setZOrderMediaOverlay(true)
            } else {
                (binding.videoRawDataRenderer.parent as ViewGroup).visibility = View.VISIBLE
                binding.videoRawDataRenderer.visibility = View.VISIBLE
                binding.videoRawDataRenderer.setZOrderMediaOverlay(true)
            }

            binding.root.setOnClickListener {
                if (selectedVideoUser == user)
                    return@setOnClickListener
                listener.onSingleTap(user)
                selectedVideoUser = user
                notifyItemRangeChanged(0, itemCount, "active")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVideoAdapter.BaseHolder {
        return VideoHolder(
            ItemUserVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserVideoAdapter.BaseHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int, payloads: MutableList<Any>) {
        val user = userList[position]
        val viewHolder = holder as VideoHolder
        if (payloads.isEmpty() || payloads.contains("video"))
            subscribeVideo(user, viewHolder)
        viewHolder.user = user

        if (user.videoStatus.isOn.not()) {
            viewHolder.binding.videoOffContain.visibility = View.VISIBLE
            viewHolder.binding.videoOffTips.setImageResource(R.drawable.zm_conf_no_avatar)
        } else {
            viewHolder.binding.videoOffContain.visibility = View.INVISIBLE
        }
        viewHolder.binding.itemUserName.text = user.userName

        viewHolder.binding.root.setBackgroundResource(
            if (selectedVideoUser == user) R.drawable.video_active_item_bg
            else R.drawable.video_item_bg
        )

        viewHolder.binding.itemAudioStatus.visibility =
            if (activeAudioList != null && activeAudioList!!.contains(user))
                View.VISIBLE
            else
                View.GONE
    }

    private fun subscribeVideo(user: ZoomVideoSDKUser, viewHolder: VideoHolder) {
        if (renderType == BaseSessionActivity.RENDER_TYPE_ZOOMRENDERER) {
            user.videoCanvas.unSubscribe(viewHolder.binding.videoRenderer)
            user.videoCanvas.subscribe(
                viewHolder.binding.videoRenderer,
                ZoomVideoSDKVideoAspect.ZoomVideoSDKVideoAspect_PanAndScan
            )
        } else {
            viewHolder.binding.videoRawDataRenderer.unSubscribe()
            user.videoPipe.subscribe(
                ZoomVideoSDKVideoResolution.VideoResolution_90P,
                viewHolder.binding.videoRawDataRenderer
            )
        }
    }

    override fun onViewRecycled(_holder: BaseHolder) {
        super.onViewRecycled(_holder)

        val holder = _holder as VideoHolder
        if (renderType == BaseSessionActivity.RENDER_TYPE_ZOOMRENDERER)
            holder.user.videoCanvas.unSubscribe(holder.binding.videoRenderer)
        else
            holder.user.videoPipe.unSubscribe(holder.binding.videoRawDataRenderer)
    }

    override fun getItemCount() = userList.size

    fun getSelectedVideoUser() = selectedVideoUser

    fun updateSelectedVideoUser(user: ZoomVideoSDKUser) {
        if (userList.indexOf(user) >= 0) {
            selectedVideoUser = user
            notifyItemRangeChanged(0, userList.size, "active")
        }
    }

    fun getIndexByUser(user: ZoomVideoSDKUser) = userList.indexOf(user)

    @SuppressLint("NotifyDataSetChanged")
    fun clear(resetSelect: Boolean) {
        userList.clear()
        if (resetSelect)
            selectedVideoUser = null

        notifyDataSetChanged()
    }

    fun onUserVideoStatusChanged(changeList: List<ZoomVideoSDKUser>) {
        changeList.forEach { user ->
            val index = userList.indexOf(user)
            if (index >= 0)
                notifyItemChanged(index, "avar")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll() {
        userList.clear()
        val all = UserHelper.getAllUsers()
        userList.addAll(all)
        notifyDataSetChanged()
    }

    fun onUserJoin(joinList: List<ZoomVideoSDKUser>) {
        joinList.forEach { user ->
            if (userList.contains(user).not()) {
                userList.add(user)
                notifyItemInserted(userList.size)
            }
        }
        checkUserList()
    }

    private fun checkUserList() {
        val all = UserHelper.getAllUsers()
        if (all.size != userList.size) {
            userList.clear()
            for (userInfo in all) {
                userList.add(userInfo)
            }
            notifyDataSetChanged()
        }
    }

    fun onUserLeave(leaveList: List<ZoomVideoSDKUser>) {
        var refreshActive = false

        selectedVideoUser?.let { user ->
            if (leaveList.contains(user)) {
                selectedVideoUser = ZoomVideoSDK.getInstance().session.mySelf
                refreshActive = true
            }
        }
        for (user in leaveList) {
            val index = userList.indexOf(user)
            if (index >= 0) {
                userList.removeAt(index)
                notifyItemRemoved(index)
            }
        }
        if (refreshActive) {
            notifyItemRangeChanged(0, userList.size, "active")
        }

        checkUserList()
    }

    fun onUserActiveAudioChanged(list: List<ZoomVideoSDKUser>, userVideoList: RecyclerView) {
        activeAudioList = list
        val childCount = userVideoList.childCount

        for (i in 0 until childCount) {
            val view = userVideoList.getChildAt(i)
            val position = userVideoList.getChildAdapterPosition(view)
            if (position >= 0 && position < userList.size) {
                val userId = userList[position]
                (userVideoList.findViewHolderForAdapterPosition(position) as VideoHolder).let { holder ->
                    holder.binding.itemAudioStatus.visibility =
                        if (activeAudioList != null && activeAudioList!!.contains(userId))
                            View.VISIBLE
                        else
                            View.GONE
                }
            }
        }
    }
}