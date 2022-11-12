package com.devicewifitracker.android.room.dao

import androidx.room.*
import com.devicewifitracker.android.room.entity.Organization
@Dao
interface OrganizationDao {
    @Insert
    fun insertOrganization(mac: Organization):Long
    @Update
    fun updateOrganization(newMacAddress: Organization)
    @Query("select * from Organization")
    fun loadAllOrganization():List<Organization>
    //    @Query("select * from Router where ip")
//    @Delete
//    fun deleteOrganization(router: Organization)
//    @Query("delete from Organization where organization = :organization")
//    fun deleteOrganizationByLastName(organization:String):Int
    @Query("select company from Organization where mac=:mac")
    fun query(vararg mac:String):String
}