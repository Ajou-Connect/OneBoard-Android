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
import kr.khs.oneboard.databinding.ActivityMainBinding
import kr.khs.oneboard.databinding.DrawerHeaderBinding
import kr.khs.oneboard.utils.ToastUtil
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
    private lateinit var navController: NavController
    private val BACK_PRESSED = 2000L
    private var curBackPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDrawer()
        initNavigationView()

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
            // todo 유저 정보 저장 후, 수정
//            drawerEmail.text = UserInfoUtil.email
//            drawerMajor.text = UserInfoUtil.major
//            drawerName.text = UserInfoUtil.name
//            drawerStudentId.text = UserInfoUtil.studentId
            drawerEmail.text = "ks96ks@ajou.ac.kr"
            drawerMajor.text = "사이버 보안학과"
            drawerName.text = "김희승"
            drawerStudentId.text = "201520930"
        }
    }

    private fun initNavigationView() {
        inflateLectureMenu()

        // sample code
        // navController.navigate(R.id.lectureDetailFragment, bundleOf("lectureInfo" to Lecture(1, "1", "1", "1")))

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
                }
                R.id.lecture_menu_plan -> {
                    navController.navigate(R.id.lecturePlanFragment)
                }
                R.id.lecture_menu_notice -> {
                    navController.navigate(R.id.noticeFragment)
                }
                R.id.lecture_menu_lesson -> {
                }
                R.id.lecture_menu_attendance -> {
                }
                R.id.lecture_menu_assignment -> {
                    navController.navigate(R.id.assignmentFragment)
                }
                R.id.lecture_menu_grade -> {
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
}