package com.devicewifitracker.android.room.dao

import androidx.room.*
import com.devicewifitracker.android.room.entity.Router

@Dao
interface RouterDao {
    @Insert
    fun insertRouter(router: Router):Long
    @Update
    fun updateRouter(newRouter: Router)
    @Query("select * from Router")
    fun loadAllRouters():List<Router>
//    @Query("select * from Router where ip")
    @Delete
    fun deleteRouter(router: Router)
    @Query("delete from Router where ip = :ip")
    fun deleteRouterByLastName(ip:String):Int
    @Query("select * from Router where ip=:ip")
    fun query(vararg ip:String):Int

}