package com.devicewifitracker.android.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {

    private val handler = Handler(Looper.getMainLooper())

    lateinit var binding: DB

    lateinit var mActivity: AppCompatActivity
    var isFirstLoad = true
    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            layoutId(), container, false
        )

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beforeInit()
        initView(savedInstanceState)
        initData()
    }

    open fun initView(savedInstanceState: Bundle?){

    }

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    override fun onResume() {
        super.onResume()
        if(isFirstLoad){
            isFirstLoad = false
            lazyLoadData()
        }
    }

    open fun initData() {}

    open fun beforeInit() {}

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}