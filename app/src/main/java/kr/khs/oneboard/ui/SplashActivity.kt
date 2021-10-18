package kr.khs.oneboard.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.khs.oneboard.databinding.ActivitySplashBinding
import kr.khs.oneboard.utils.DialogUtil

class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        networkCheck()
        loginCheck()

        // todo 필요할 경우 미리 데이터 로딩
        Runnable {
            Thread.sleep(2000L)
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }.run()
    }

    private fun loginCheck() {
        // todo 로그인 체크
        if (false) {
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
            )
        }
    }

    private fun networkCheck() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork

        if (currentNetwork == null) {
            DialogUtil.createDialog(
                context = this,
                message = "네트워크 상태가 불안정합니다.",
                positiveText = "앱 재시작",
                negativeText = "앱 종료",
                positiveAction = { restart() },
                negativeAction = {
                    val pid = android.os.Process.myPid()
                    android.os.Process.killProcess(pid)
                }
            )
        }
    }

    private fun restart() {
        val packageManager: PackageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        val componentName = intent.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}