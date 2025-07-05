package com.example.farmer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface LabourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabour(labour: Labour): Long

    @Query("SELECT * FROM labour_table")
    suspend fun getAllLabours(): List<Labour>

    @Delete
    suspend fun deleteLabour(labour: Labour)
}

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weightEntry: WeightEntry)

    @Query("SELECT * FROM weight_table WHERE labourOwnerId = :labourId")
    suspend fun getWeightsForLabour(labourId: Int): List<WeightEntry>

    @Delete
    suspend fun deleteWeight(weightEntry: WeightEntry)
}