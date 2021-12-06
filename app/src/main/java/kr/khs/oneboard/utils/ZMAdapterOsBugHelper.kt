package kr.khs.oneboard.utils

import android.app.AppOpsManager
import android.app.AppOpsManager.OnOpChangedListener
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi

object ZMAdapterOsBugHelper {
    private var canDraw = false
    private var onOpChangedListener: OnOpChangedListener? = null
    val isNeedListenOverlayPermissionChanged: Boolean
        get() = (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O + 1)

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startListenOverlayPermissionChange(context: Context) {
        val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        canDraw = Settings.canDrawOverlays(context)
        val myPackageName = context.packageName
        if (TextUtils.isEmpty(myPackageName)) return
        onOpChangedListener = OnOpChangedListener { op, packageName ->
            if (myPackageName == packageName && AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW == op) {
                canDraw = !canDraw
            }
        }
        opsManager.startWatchingMode(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
            null, onOpChangedListener!!
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun stopListenOverlayPermissionChange(context: Context) {
        if (onOpChangedListener != null) {
            val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            opsManager.stopWatchingMode(onOpChangedListener!!)
            onOpChangedListener = null
        }
    }

    fun ismCanDraw(): Boolean {
        return canDraw
    }

}