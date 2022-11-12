package com.devicewifitracker.finder.ui.guide

import android.os.Bundle
import com.devicewifitrack.android.ui.guide.GuideCallback
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseFragment
import com.devicewifitracker.android.databinding.FragmentGuideFirstBinding



class GuideFirstFragment(private val callback : GuideCallback) : BaseFragment<FragmentGuideFirstBinding>() {
    override fun layoutId()= R.layout.fragment_guide_first

    override fun lazyLoadData() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.next.setOnClickListener {
            callback.callback(0)
        }
    }

}