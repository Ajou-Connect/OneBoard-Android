package kr.khs.oneboard.core.zoom

import android.app.Service
import android.content.Intent
import android.os.IBinder
import us.zoom.sdk.ZoomVideoSDK

class NotificationService : Service() {
    override fun onCreate() {
        super.onCreate()
        val notification = NotificationMgr.getConfNotification()
        startForeground(NotificationMgr.PT_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        ZoomVideoSDK.getInstance().shareHelper.stopShare()
        ZoomVideoSDK.getInstance().leaveSession(false)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent) {
        NotificationMgr.removeConfNotification()
        stopSelf()
        ZoomVideoSDK.getInstance().shareHelper.stopShare()
        ZoomVideoSDK.getInstance().leaveSession(false)
    }
}