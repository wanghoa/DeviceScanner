package com.devicewifitracker.android

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.devicewifitracker.android.room.dao.OrganizationDao
import com.devicewifitracker.android.room.entity.Organization
import com.devicewifitracker.android.util.ReadLocalTxt
import com.devicewifitracker.android.util.RegularExpressionUtil
import com.devicewifitracker.android.util.RoomGetDao
import com.devicewifitracker.android.util.SubscribeManager
import kotlin.concurrent.thread

class App : Application() {
    private var organiaztionDao: OrganizationDao? = null
    companion object{
        lateinit var context: Context
        lateinit var app: App

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        app = this

        organiaztionDao = RoomGetDao.getOrganizationDao()
        // 将 存进集合中的数据添加到数据库
        if (TextUtils.isEmpty(
                SPUtils.getInstance()
                    .getString("MAC_ADRESS")
            )
        ) {
            thread {


                val map = ReadLocalTxt.readFromAssetsToMap(context,"dic_mac_company_more.txt")
//            val map = ReadLocalTxt.readFromAssetsToMap(App.context,"dic_mac_company_change.txt")
                map.forEach { i, s ->
//                    val content =    RegularExpressionUtil.reuglar(s.toString())// 正则去掉行号
                    val content =  s.toString()// 从新添加的文件没有行号
                    if (TextUtils.isEmpty(content)) {
                        return@forEach
                    }
                    if (content.length > 9) {// 一分二十秒
                        // 截取前8位 使用":"替换“-”后转成小写
                        val macAddress =  content.substring(0, 9).replace("-", ":").trim().toLowerCase()
                        val company =  content.substring(content.substring(0, 9).length, content.length)
//                        LogUtils.d("数据库======${macAddress}~${company}")
                        organiaztionDao?.insertOrganization(Organization(null,macAddress,company))


                    }
                }
                SPUtils.getInstance()
                    .put("MAC_ADRESS","macAddress")
            }
        }
        Utils.init(this)
        CrashUtils.init()
        SubscribeManager.instance.initBilling(this)
    }

}