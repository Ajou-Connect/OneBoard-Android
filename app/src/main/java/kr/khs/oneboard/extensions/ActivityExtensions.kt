package kr.khs.oneboard.extensions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.restart() {
    val packageManager: PackageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    val componentName = intent.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun killMyApp() = android.os.Process.killProcess(
    android.os.Process.myPid()
)