package kr.khs.oneboard.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kr.khs.oneboard.R
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.databinding.ActivitySplashBinding
import kr.khs.oneboard.utils.DialogUtil
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(
            layoutInflater
        )
    }

    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val text1Anim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_textview)
        binding.splashTitle1.startAnimation(text1Anim)
        val text2Anim = AnimationUtils.loadAnimation(this, R.anim.anim_splash_textview)
        binding.splashTitle2.startAnimation(text2Anim)

        text2Anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                networkCheck()
                serverCheck()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })
    }

    // health 체크 후 로그인 체크
    private fun serverCheck() {
        runBlocking {
            val healthCheck: Deferred<Boolean> = async(Dispatchers.IO) {
//                apiService.healthCheck()
                // TODO: 2021/10/19 test data
                true
            }

            if (healthCheck.await()) {
                startActivity(
                    Intent(
                        applicationContext,
//                    if (apiService.loginCheck())
//                        MainActivity::class.java
//                    else
                        LoginActivity::class.java
                    )
                )
                overridePendingTransition(
                    R.anim.anim_activity_in_down,
                    R.anim.anim_activity_out_top
                )
            } else {
                DialogUtil.createDialog(
                    context = this@SplashActivity,
                    message = "서버의 상태가 불안정합니다.",
                    positiveText = "앱 재시작",
                    negativeText = "앱 종료",
                    positiveAction = { restart() },
                    negativeAction = { killMyApp() }
                )
            }
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
                negativeAction = { killMyApp() }
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

    private fun killMyApp() = android.os.Process.killProcess(
        android.os.Process.myPid()
    )

}