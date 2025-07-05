package com.example.farmer.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labour_table")
data class Labour(
    @PrimaryKey(autoGenerate = true)
    val labourId: Int = 0,
    val name: String,
    val cropType: String,
    val workingType: Boolean,
    val date: String
)