//package com.xl.mvvm.ext
//
//import com.google.gson.Gson
//import com.tencent.mmkv.MMKV
//
////  映射ID
//const val MMAPID = "MMKV"
//
////  登录信息
//const val IM_ACCID = "IM_ACCID"
//const val IM_TOKEN = "IM_TOKEN"
//const val USER_ID = "USER_ID"
//const val USER_TOKEN = "USER_TOKEN"
//
////  图模本地列表
//const val LIST_DRAW = "LIST_DRAW"
//const val LIST_MODEL = "LIST_MODEL"
//
///**
// * 获取MMKV
// */
//val mmkv: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//    MMKV.mmkvWithID(MMAPID)
//}
//
///**
// * 存放数组
// */
//fun <T> MMKV.encodeArray(key: String, list: List<T>){
//    clearArray(key)
//    mmkv.encode(key,list.size)
//    for (index in list.indices) {
//        mmkv.encode(key + index, Gson().toJson(list[index]))
//    }
//}
//
///**
// * 清除数组
// */
//fun MMKV.clearArray(key: String){
//    val size = mmkv.decodeInt(key, 0)
//    for (index in 0 until size) {
//        mmkv.remove(key + index)
//    }
//}
//
///**
// * 获取数组
// */
//fun <T> MMKV.decodeArray(key: String, javaClass: Class<T>): MutableList<T>{
//    val list = mutableListOf<T>()
//    val size = mmkv.decodeInt(key, 0)
//    for (index in 0 until size) {
//        val entityStr = mmkv.decodeString(key + index)
//        if (!entityStr.isNullOrEmpty()) {
//            list.add(Gson().fromJson(entityStr,javaClass))
//        }
//    }
//    return list
//}
//
///**
// * 添加记录
// */
//fun <T> MMKV.add(key: String,t: T) {
//    var size = mmkv.decodeInt(key, 0)
//    mmkv.encode(key,++size)
//    mmkv.encode(key + size, Gson().toJson(t))
//}
//
///**
// * 更新某条记录
// */
//fun <T> MMKV.update(key: String, t: T, javaClass: Class<T>) {
//    val list = decodeArray(key,javaClass)
//    val iterator = list.iterator()
//    while (iterator.hasNext()) {
//        val item = iterator.next()
//        item?.let {
//            if (it == t) {
//                iterator.remove()
//            }
//        }
//    }
//    list.add(t)
//    encodeArray(key,list)
//}
//
///**
// * 删除某条记录
// */
//fun <T> MMKV.remove(key: String, t: T, javaClass: Class<T>) {
//    val list = decodeArray(key,javaClass)
//    val iterator = list.iterator()
//    while (iterator.hasNext()) {
//        val item = iterator.next()
//        if (item.toString() == t.toString()) {
//            iterator.remove()
//        }
//    }
//    encodeArray(key,list)
//}
//
//
//
//
