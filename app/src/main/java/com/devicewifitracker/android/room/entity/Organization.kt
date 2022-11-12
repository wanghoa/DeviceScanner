package com.devicewifitracker.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Organization(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long?,
                         @ColumnInfo(name = "mac") val macAdress: String = UUID.randomUUID().toString(),
                        @ColumnInfo(name =  "company") val company: String)


