package com.devicewifitracker.android

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.devicewifitracker.android.databinding.ActivityBottomNavigationBinding
import com.devicewifitracker.android.navigation.CustomNavigator
import com.devicewifitracker.android.navigation.KeepStateNavigation
import com.devicewifitracker.android.ui.home.HomeFragment
import com.devicewifitracker.android.ui.notifications.NotificationsFragment

class BottomNavigationActivity : AppCompatActivity() {
    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null
    private lateinit var binding: ActivityBottomNavigationBinding
    var home: HomeFragment? = null
    var notification: NotificationsFragment? = null
    var currentFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, getColor(R.color.black))// 更改状态拦颜色值
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView


//        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navigation)

        val navHostFragment =
//            getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment
//        val  navigator =  KeepStateNavigation(this, navHostFragment.getChildFragmentManager(), R.id.nav_host_fragment_activity_bottom_navigation);
//        val  navigator =  CustomNavigator(this, supportFragmentManager, R.id.nav_host_fragment_activity_bottom_navigation);
//        navController.getNavigatorProvider().addNavigator(navigator);
//        navController.setGraph(R.navigation.mobile_navigation);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)// 对底部 展示不影响
        initFragment(savedInstanceState)
        navView.setOnNavigationItemSelectedListener(listener)

    }

    fun initFragment(savedInstanceState: Bundle?) {
        //判断activity是否重建，如果不是，则不需要重新建立fragment.
        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager?.beginTransaction();
            if (home == null) {
                home = HomeFragment();
            }
            currentFragment = home
            fragmentTransaction?.replace(R.id.container, home!!)?.commit();//fragment parent layout id
        }
    }

    val listener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (home == null) {
                        home = HomeFragment()
                    }
                    switchContent(currentFragment, home);
                    return true;
                }

                R.id.navigation_notifications -> {
                    if (notification == null) {
                        notification = NotificationsFragment()
                    }
                    switchContent(currentFragment, notification);
                    return true;
                }
              else  ->
                return false
            }
        }
    }

    fun switchContent(fromFragment: Fragment?, toFragment: Fragment?) {
        if (fromFragment == null || toFragment == null) return;

        if (currentFragment != toFragment) {
            currentFragment = toFragment;
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager?.beginTransaction();
            if (!toFragment.isAdded()) {
                //fragment parent layout id
                fragmentTransaction?.hide(fromFragment)?.add(R.id.container, toFragment)?.commit();
            } else {
                fragmentTransaction?.hide(fromFragment)?.show(toFragment)?.commit();
            }
        }


    }
}


