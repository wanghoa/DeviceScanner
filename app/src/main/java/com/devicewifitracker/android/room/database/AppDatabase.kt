package com.devicewifitracker.android.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devicewifitracker.android.room.dao.OrganizationDao
import com.devicewifitracker.android.room.dao.RouterDao
import com.devicewifitracker.android.room.entity.Organization
import com.devicewifitracker.android.room.entity.Router

@Database(version = 1, entities = [Router::class,Organization::class])
abstract class AppDatabase :RoomDatabase() {
    abstract fun routerDao() : RouterDao
    abstract fun organiaztionDao():OrganizationDao


    companion object {

        val MIGRATION_1_2 by lazy {
            object :Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
//                    database.execSQL("CREATE TABLE IF NOT EXISTS `Organization` (`id` INTEGER NOT NULL, `organization` TEXT,  PRIMARY KEY(`id`))");
                    database.execSQL("CREATE TABLE IF NOT EXISTS `Organization` (`organization` TEXT, `id` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                    // 创建临时表
//                    database.execSQL(
//                        "CREATE TABLE users_new (userid TEXT, username TEXT, last_update INTEGER, PRIMARY KEY(userid))");
                }
            }
        }


        private var instance: AppDatabase?= null

        @Synchronized
        fun getDatabase(context: Context):AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"app_database")
//                .addMigrations(MIGRATION_1_2)
                .build()
                .apply { instance = this }
        }
    }
}