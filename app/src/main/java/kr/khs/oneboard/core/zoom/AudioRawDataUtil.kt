package kr.khs.oneboard.core.zoom

import android.content.Context
import timber.log.Timber
import us.zoom.sdk.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class AudioRawDataUtil(context: Context) {
    private val map = hashMapOf<String, FileChannel>()
    private val applicationContext = context.applicationContext

    private fun createFileChannel(name: String): FileChannel? {
        val path = applicationContext.externalCacheDir!!.absolutePath + "/audiorawdata/"
        val dir = File(path)
        if (!dir.exists())
            dir.mkdirs()

        val fileName = "$path$name.pcm"
        val file = File(fileName)
        try {
            if (file.exists())
                file.delete()

            val fileChannel = FileOutputStream(file, true).channel

            return fileChannel
        } catch (e: Exception) {
            Timber.e(e)
        }

        return null
    }

    private val dataDelegate = object : ZoomVideoSDKDelegate {
        override fun onSessionJoin() {}

        override fun onSessionLeave() {}

        override fun onError(p0: Int) {}

        override fun onUserJoin(p0: ZoomVideoSDKUserHelper?, p1: MutableList<ZoomVideoSDKUser>?) {}

        override fun onUserLeave(p0: ZoomVideoSDKUserHelper?, p1: MutableList<ZoomVideoSDKUser>?) {}

        override fun onUserVideoStatusChanged(
            p0: ZoomVideoSDKVideoHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
        }

        override fun onUserAudioStatusChanged(
            p0: ZoomVideoSDKAudioHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
        }

        override fun onUserShareStatusChanged(
            p0: ZoomVideoSDKShareHelper?,
            p1: ZoomVideoSDKUser?,
            p2: ZoomVideoSDKShareStatus?
        ) {
        }

        override fun onLiveStreamStatusChanged(
            p0: ZoomVideoSDKLiveStreamHelper?,
            p1: ZoomVideoSDKLiveStreamStatus?
        ) {
        }

        override fun onChatNewMessageNotify(
            p0: ZoomVideoSDKChatHelper?,
            p1: ZoomVideoSDKChatMessage?
        ) {
        }

        override fun onUserHostChanged(p0: ZoomVideoSDKUserHelper?, p1: ZoomVideoSDKUser?) {}

        override fun onUserManagerChanged(p0: ZoomVideoSDKUser?) {}

        override fun onUserNameChanged(p0: ZoomVideoSDKUser?) {}

        override fun onUserActiveAudioChanged(
            p0: ZoomVideoSDKAudioHelper?,
            p1: MutableList<ZoomVideoSDKUser>?
        ) {
        }

        override fun onSessionNeedPassword(p0: ZoomVideoSDKPasswordHandler?) {}

        override fun onSessionPasswordWrong(p0: ZoomVideoSDKPasswordHandler?) {}

        override fun onMixedAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData) {
            Timber.d("onMixedAudioRawDataReceived : $rawData")

            saveAudioRawData(rawData, ZoomVideoSDK.getInstance().session.mySelf.userName)
        }

        override fun onOneWayAudioRawDataReceived(
            rawData: ZoomVideoSDKAudioRawData,
            user: ZoomVideoSDKUser
        ) {
            Timber.d("onOneWayAudioRawDataReceived : $rawData")
            saveAudioRawData(rawData, user.userName)
        }

        override fun onShareAudioRawDataReceived(rawData: ZoomVideoSDKAudioRawData) {
            Timber.d("onShareAudioRawDataReceived : $rawData")
            saveAudioRawData(rawData, "share")
        }

    }

    fun saveAudioRawData(rawData: ZoomVideoSDKAudioRawData, fileName: String) {
        try {
            var fileChannel = map[fileName]
            if (fileChannel == null) {
                fileChannel = createFileChannel(fileName)
                map[fileName] = fileChannel!!
            }

            fileChannel.write(rawData.buffer, rawData.bufferLen.toLong())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun subscribeAudio() {
        ZoomVideoSDK.getInstance().audioHelper.subscribe()
        ZoomVideoSDK.getInstance().addListener(dataDelegate)
    }

    fun unSubscribe() {
        ZoomVideoSDK.getInstance().removeListener(dataDelegate)

        for (fileChannel in map.values) {
            try {
                fileChannel.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        ZoomVideoSDK.getInstance().audioHelper.unSubscribe()
    }
}