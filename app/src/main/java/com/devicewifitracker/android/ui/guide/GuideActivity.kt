
package com.devicewifitracker.android.ui.guide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

import com.devicewifitrack.android.ui.guide.GuideCallback
import com.devicewifitracker.android.MainActivity
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGuideBinding
import com.devicewifitracker.finder.ui.guide.GuideFirstFragment
import com.devicewifitracker.finder.ui.guide.GuidePageAdapter
import com.devicewifitracker.finder.ui.guide.GuideSecondFragment
import com.devicewifitracker.finder.ui.guide.GuideThirdFragment
import com.google.android.material.tabs.TabLayoutMediator



class GuideActivity : BaseActivity<ActivityGuideBinding>() {
    private lateinit var adapter : GuidePageAdapter
    override fun getLayoutId() = R.layout.activity_guide

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val fragments: MutableList<Fragment> = ArrayList()
        fragments.add(GuideFirstFragment(guideCallback))
        fragments.add(GuideSecondFragment(guideCallback))
        fragments.add(GuideThirdFragment(guideCallback))
        adapter = GuidePageAdapter(this,fragments)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                if(position==2){
//                    showRateUs()
//                }
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.view
        }.attach()
    }



    private var guideCallback =  object : GuideCallback {
        override fun callback(index: Int) {
            var currentIndex = binding.viewPager.currentItem
            if(index == 2){
                startActivity(Intent(this@GuideActivity, MainActivity::class.java))
                finish()
            }else{
                binding.viewPager.setCurrentItem(index+1,true)
            }
        }

    }

}