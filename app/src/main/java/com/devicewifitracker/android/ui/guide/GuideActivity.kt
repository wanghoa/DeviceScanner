
package com.devicewifitracker.android.ui.guide

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.devicewifitrack.android.ui.guide.GuideCallback
import com.devicewifitracker.android.BottomNavigationActivity
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityGuideBinding
import com.devicewifitracker.android.ui.subscribe.SubscribeActivity
import com.devicewifitracker.android.ui.subscribe.SubscribeActivityNew
import com.devicewifitracker.android.util.Constant.GUIDE_USER_KEY
import com.devicewifitracker.android.util.SubscribeManager
import com.devicewifitracker.finder.ui.guide.GuideFirstFragment
import com.devicewifitracker.finder.ui.guide.GuidePageAdapter
import com.devicewifitracker.finder.ui.guide.GuideSecondFragment
import com.devicewifitracker.finder.ui.guide.GuideThirdFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.stx.xhb.xbanner.XBanner
import com.stx.xhb.xbanner.entity.SimpleBannerInfo
import java.io.Serializable

/**
 * 现在使用XBanner 展示引导页
 * 另一种方式使用3个Fragment 布局与类中代码都注释了

 */

class GuideActivity : BaseActivity<ActivityGuideBinding>() {
    private lateinit var adapter : GuidePageAdapter
    override fun getLayoutId() = R.layout.activity_guide
    private val banner: XBanner? = null
    //java  int[] pics =  java 写法

       private val pics = intArrayOf(
       R.drawable.guide_one_old,
       R.drawable.guide_two_old,
       R.drawable.guide_three_old,

   )

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
//        initBanner()
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

    private fun initBanner() {
      /*  val size: Int = pics.size
        binding.banner.loadImage { banner, model, view, position ->
            val bean = model as PicBean
            val img = view as ImageView
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this@GuideActivity)
                .load(bean.picPath)
                .centerCrop()
                .dontAnimate()
                .error(bean.picPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(img)
        }
        binding.banner.setOnItemClickListener { banner, model, view, position ->
            if (position == size - 1) {
                startActivity(Intent(this@GuideActivity, BottomNavigationActivity::class.java))
                finish()
            }
        }
        val list: MutableList<PicBean> = java.util.ArrayList<PicBean>()
        //  for (int i=0;i<size;i++){
        for (i in 0 until size) {
            list.add(PicBean(pics.get(i)))
        }
        binding.banner.setBannerData(list)*/
    }

    private var guideCallback =  object : GuideCallback {
        override fun callback(index: Int) {
            var currentIndex = binding.viewPager.currentItem
            if(index == 2){
                if (!SubscribeManager.instance.isSubscribe()) {
//                    SubscribeActivity.actionOpenAct(this@GuideActivity,GUIDE_USER_KEY)
                    SubscribeActivityNew.actionOpenAct(this@GuideActivity,GUIDE_USER_KEY)
                } else {
                    startActivity(Intent(this@GuideActivity, BottomNavigationActivity::class.java))
                }
                finish()
            }else{
                binding.viewPager.setCurrentItem(index+1,true)
            }
        }

    }
  class PicBean(id: Int) : SimpleBannerInfo(), Serializable {
      var picPath: Int
      @JvmName("getPicPath1")
      fun getPicPath(): Int {
          return picPath
      }
      override fun getXBannerUrl(): Any? {
          return null
      }
      init {
          picPath = id
      }
  }
}