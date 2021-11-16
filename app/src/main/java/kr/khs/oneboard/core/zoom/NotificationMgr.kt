package kr.khs.oneboard.core.zoom

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import kr.khs.oneboard.OneBoardApp
import kr.khs.oneboard.R

class NotificationMgr {
    companion object {
        const val PT_NOTIFICATION_ID = 4
        private const val ZOOM_NOTIFICATION_CHANNEL_ID = "Video_sdk_notification_channel_id"

        fun hasNotification(notificationId: Int): Boolean {
            try {
                val context = OneBoardApp.getInstance()
                val notificationMgr =
                    context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
                val statusBarNotifications = notificationMgr.getActiveNotifications()
                for (notification in statusBarNotifications) {
                    if (notification.id == notificationId)
                        return true
                }
            } catch (e: Exception) {
                return false
            }
            return false
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getConfNotification(): Notification {
            val context = OneBoardApp.getInstance()

            val clickIntent = Intent(context, IntegrationActivity::class.java).apply {
                action = IntegrationActivity.ACTION_RETURN_TO_CONF
            }
            val contentIntent = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val contentTitle = "VideoSDK"
            val contentText = "Meeting in progress"
            val smallIcon = R.mipmap.ic_launcher
            val color = context.resources.getColor(R.color.zm_notification_icon_bg, null)

            return NotificationMgr.getNotificationCompatBuilder(context.applicationContext, false)
                .apply {
                    setWhen(0)
                    setAutoCancel(false)
                    setOngoing(true)
                    setSmallIcon(smallIcon)
                    setColor(color)
                    setContentTitle(contentTitle)
                    setContentText(contentText)
                    setOnlyAlertOnce(true)
                    setContentIntent(contentIntent)
                    val largeIcon =
                        BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
                    setLargeIcon(largeIcon)
                }.build()
        }

        fun removeConfNotification() {
            val context = OneBoardApp.getInstance()
            val notificationMgr =
                context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
            notificationMgr.cancel(NotificationMgr.PT_NOTIFICATION_ID)
        }

        private fun getNotificationCompatBuilder(
            context: Context,
            isHighImportance: Boolean
        ): NotificationCompat.Builder {
            lateinit var builder: NotificationCompat.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationMgr = context
                    .getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
                var notificationChannel =
                    notificationMgr!!.getNotificationChannel(NotificationMgr.getNotificationChannelId())
                if (notificationChannel == null) {
                    notificationChannel = NotificationChannel(
                        NotificationMgr.getNotificationChannelId(),
                        "Zoom Notification",
                        if (isHighImportance) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationChannel.enableVibration(true)
                    if (notificationChannel.canShowBadge()) notificationChannel.setShowBadge(false)
                }
                if (notificationMgr != null) {
                    notificationMgr.createNotificationChannel(notificationChannel)
                }
                builder =
                    NotificationCompat.Builder(context, NotificationMgr.getNotificationChannelId())
            } else {
                builder = NotificationCompat.Builder(context)
            }
            return builder
        }

        fun getNotificationChannelId() = ZOOM_NOTIFICATION_CHANNEL_ID
    }
}