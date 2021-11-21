package kr.khs.oneboard.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.data.User
import kr.khs.oneboard.databinding.ActivityMainBinding
import kr.khs.oneboard.databinding.DrawerHeaderBinding
import kr.khs.oneboard.extensions.restart
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.MainViewModel
import timber.log.Timber

// todo : return value 간단하게 할 수 있는 것은 = 사용해서 바꿔주기
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels()

    private lateinit var drawerHeaderBinding: DrawerHeaderBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private val BACK_PRESSED = 2000L
    private var curBackPressed = 0L

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

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else {
            when {
                navController.backStack.size > 2 -> {
                    navController.popBackStack()
                }
                System.currentTimeMillis() - curBackPressed <= BACK_PRESSED -> {
                    super.onBackPressed()
                }
                else -> {
                    ToastUtil.shortToast(applicationContext, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.")
                    curBackPressed = System.currentTimeMillis()
                }
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
        navController =
            (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController
        with(drawerHeaderBinding) {
            drawerEmail.text = ""
            drawerMajor.text = ""
            drawerName.text = ""
            drawerStudentId.text = ""
        }
    }

    private fun initNavigationView() {
        inflateLectureMenu()

        // sample code
        // navController.navigate(R.id.lectureDetailFragment, bundleOf("lectureInfo" to Lecture(1, "1", "1", "1")))

        // todo : 같은 destination일 때 처리
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            Timber.tag("currentDestination").d("${navController.currentDestination}")
            when (menuItem.itemId) {
                R.id.list_menu_attendance -> {
                }
                R.id.list_menu_grade -> {
                }
                R.id.list_menu_lecture -> {
                }
                R.id.list_menu_logout -> {
                    UserInfoUtil.setToken(this, "")
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                }
                R.id.lecture_menu_plan -> {
                    navController.navigate(R.id.lecturePlanFragment)
                }
                R.id.lecture_menu_notice -> {
                    navController.navigate(R.id.noticeFragment)
                }
                R.id.lecture_menu_lesson -> {
                    navController.navigate(R.id.lessonListFragment)
                }
                R.id.lecture_menu_attendance -> {
                    navController.navigate(R.id.attendanceFragment)
                }
                R.id.lecture_menu_assignment -> {
                    navController.navigate(R.id.assignmentFragment)
                }
                R.id.lecture_menu_grade -> {
                    navController.navigate(R.id.gradeFragment)
                }
                R.id.lecture_menu_understanding -> {
                }
                R.id.lecture_menu_go_list -> {
                    navController.navigate(
                        R.id.lectureListFragment,
                        null,
                        NavOptions.Builder().setPopUpTo(R.id.lectureListFragment, true).build()
                    )
                }
                R.id.lecture_menu_logout -> {
                    UserInfoUtil.setToken(this, "")
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        actionBarDrawerToggle.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    fun inflateLectureMenu(isDetail: Boolean = false) {
        binding.navigationView.menu.clear()
        binding.navigationView.inflateMenu(
            if (isDetail)
                R.menu.drawer_menu_in_lecture
            else
                R.menu.drawer_menu_in_list
        )
    }

    @JvmName("getViewModel1")
    fun getViewModel() = viewModel

    private fun User.setInfo() {
        UserInfoUtil.email = email
        UserInfoUtil.major = major
        UserInfoUtil.name = name
        UserInfoUtil.studentId = studentNumber
        UserInfoUtil.type = if (userType == "T") TYPE_PROFESSOR else TYPE_STUDENT
        UserInfoUtil.univ = university
    }
}

// TODO: 2021/11/19 학생, 강의자 모든 화면 구분해주기
// TODO: 2021/11/19 수업 관련
// TODO: 2021/11/19 HTML 
// TODO: 2021/11/19 pdf 뷰어, 파일 다운로드 찾아보기 