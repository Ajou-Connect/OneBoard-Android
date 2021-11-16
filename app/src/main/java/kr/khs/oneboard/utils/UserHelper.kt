package kr.khs.oneboard.utils

import us.zoom.sdk.ZoomVideoSDK
import us.zoom.sdk.ZoomVideoSDKUser

object UserHelper {
    fun getAllUsers(): List<ZoomVideoSDKUser> {
        val userList = mutableListOf<ZoomVideoSDKUser>()
        val sdkSession = ZoomVideoSDK.getInstance().session
        sdkSession ?: return userList

        sdkSession.mySelf?.let {
            userList.add(it)
        }

        userList.addAll(sdkSession.remoteUsers)

        return userList
    }
}