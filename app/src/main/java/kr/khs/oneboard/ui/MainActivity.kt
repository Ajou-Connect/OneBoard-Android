package kr.khs.oneboard.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ActivityMainBinding
import kr.khs.oneboard.databinding.DrawerHeaderBinding
import kr.khs.oneboard.utils.UserInfoUtil
import kr.khs.oneboard.viewmodels.MainViewModel

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
            drawerEmail.text = UserInfoUtil.email
            drawerMajor.text = UserInfoUtil.major
            drawerName.text = UserInfoUtil.name
        }
    }

    private fun initNavigationView() {
        // todo 교수, 학생일 경우 다른 메뉴 inflate
        binding.navigationView.inflateMenu(R.menu.drawer_menu_professor)

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
}