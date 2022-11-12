package com.devicewifitracker.android.util

import com.devicewifitracker.android.App
import com.devicewifitracker.android.room.dao.OrganizationDao
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.database.AppDatabase

object RoomGetDao {
    fun getOrganizationDao(): OrganizationDao {
        return AppDatabase.getDatabase(App.context).organiaztionDao()

    }

    fun getRouterDao(): RouterDao {
        return AppDatabase.getDatabase(App.context).routerDao()

    }
}