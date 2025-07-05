package com.example.farmer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "weight_table",
    foreignKeys = [ForeignKey(
        entity = Labour::class,
        parentColumns = ["labourId"],
        childColumns = ["labourOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val weightId: Int = 0,

    val labourOwnerId: Int,  // foreign key to Labour.labourId

    val date: String,        // can be stored as "yyyy-MM-dd"
    val weight: MutableList<Int> = mutableListOf()
)
