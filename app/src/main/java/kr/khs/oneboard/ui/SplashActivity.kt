package kr.khs.oneboard.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ActivitySplashBinding
import kr.khs.oneboard.extensions.killMyApp
import kr.khs.oneboard.extensions.restart
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.SplashViewModel

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initMotionLayout()

        networkCheck()
        viewModel.checkHealth()
        viewModel.checkValidToken(UserInfoUtil.getToken(applicationContext))

        viewModel.animationCheck.observe(this) { animationCheck ->
            viewModel.loginCheck.value?.let { loginCheck ->
                viewModel.healthCheck.value?.let { healthCheck ->
                    checkNextStep(healthCheck, loginCheck, animationCheck)
                }
            }
        }

        viewModel.healthCheck.observe(this) { healthCheck ->
            viewModel.loginCheck.value?.let { loginCheck ->
                viewModel.animationCheck.value?.let { animationCheck ->
                    checkNextStep(healthCheck, loginCheck, animationCheck)
                }
            }
        }
        viewModel.loginCheck.observe(this) { loginCheck ->
            viewModel.healthCheck.value?.let { healthCheck ->
                viewModel.animationCheck.value?.let { animationCheck ->
                    checkNextStep(healthCheck, loginCheck, animationCheck)
                }
            }
        }
    }

    private fun initMotionLayout() {
        binding.splashMotionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (motionLayout?.currentState == R.id.splashMotionEnd) {
                    viewModel.checkAnimation()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })
    }

    private fun checkNextStep(healthCheck: Boolean, loginCheck: Boolean, animationCheck: Boolean) {
        if (animationCheck.not())
            return

        if (healthCheck) {
            startActivity(
                Intent(
                    applicationContext,
                    if (loginCheck)
                        MainActivity::class.java
                    else
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


}