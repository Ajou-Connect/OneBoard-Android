package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.data.User
import kr.khs.oneboard.databinding.ActivityMainBinding
import kr.khs.oneboard.databinding.DrawerHeaderBinding
import kr.khs.oneboard.extensions.restart
import kr.khs.oneboard.utils.DialogUtil
import kr.khs.oneboard.utils.TYPE_PROFESSOR
import kr.khs.oneboard.utils.TYPE_STUDENT
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.MainViewModel
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels()

    private lateinit var drawerHeaderBinding: DrawerHeaderBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDrawer()
        initNavigationView()

        Timber.tag("JWT").d(UserInfoUtil.getToken(applicationContext))

        viewModel.isLoading.observe(this) {
            if (it) {
                DialogUtil.onLoadingDialog(this)
            } else {
                DialogUtil.offLoadingDialog()
            }
        }

        viewModel.user.observe(this) { user ->
            user?.let {
                user.setInfo()

                drawerHeaderBinding.drawerEmail.text = UserInfoUtil.email
                drawerHeaderBinding.drawerMajor.text = UserInfoUtil.major
                drawerHeaderBinding.drawerName.text = UserInfoUtil.name
                drawerHeaderBinding.drawerStudentId.text = UserInfoUtil.studentId
            } ?: run {
                DialogUtil.createDialog(
                    context = this,
                    message = "유저 정보를 올바르게 불러오지 못했습니다.",
                    positiveText = "다시 시도",
                    negativeText = "앱 재시작",
                    positiveAction = { viewModel.getUserInfo() },
                    negativeAction = { restart() }
                )
            }
        }
    }

    private fun initDrawer() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.app_name, R.string.app_name)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarDrawerToggle.syncState()

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)

        drawerHeaderBinding = DrawerHeaderBinding.bind(binding.navigationView.getHeaderView(0))

        initDrawerHeader()
    }

    private fun initDrawerHeader() {
        with(drawerHeaderBinding) {
            drawerEmail.text = ""
            drawerMajor.text = ""
            drawerName.text = ""
            drawerStudentId.text = ""
        }
    }

    private fun initNavigationView() {
        // todo 교수, 학생일 경우 다른 메뉴 inflate
        binding.navigationView.inflateMenu(
//            if(UserInfoUtil.type)
            R.menu.drawer_menu_professor
//            else
//                R.menu.drawer_menu_student
        )

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        actionBarDrawerToggle.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    private fun User.setInfo() {
        UserInfoUtil.email = email
        UserInfoUtil.major = major
        UserInfoUtil.name = name
        UserInfoUtil.studentId = studentNumber
        UserInfoUtil.type = if (userType == "T") TYPE_PROFESSOR else TYPE_STUDENT
        UserInfoUtil.univ = university
    }
}