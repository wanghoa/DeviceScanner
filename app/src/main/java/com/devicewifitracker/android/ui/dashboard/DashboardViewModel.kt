package com.devicewifitracker.android.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "点击下方位置按钮，进入到对应的地点位置检测"
    }
    val text: LiveData<String> = _text
}