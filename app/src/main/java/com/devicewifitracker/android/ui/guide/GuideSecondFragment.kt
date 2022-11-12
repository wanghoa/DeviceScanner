package com.devicewifitracker.finder.ui.guide

import android.os.Bundle
import com.devicewifitrack.android.ui.guide.GuideCallback
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseFragment
import com.devicewifitracker.android.databinding.FragmentGuideSecondBinding


class GuideSecondFragment(private val callback : GuideCallback) : BaseFragment<FragmentGuideSecondBinding>() {
    override fun layoutId()= R.layout.fragment_guide_second

    override fun lazyLoadData() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.guideTwoIV.setOnClickListener {
            callback.callback(1)
        }
    }

}