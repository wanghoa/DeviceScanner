package com.devicewifitracker.android.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Router(var ip: String){
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}


