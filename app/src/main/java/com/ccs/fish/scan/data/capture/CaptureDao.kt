package com.ccs.fish.scan.data.capture

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaptureDao {
    @Insert
    suspend fun insertCapture(capture: Capture)

    @Query("SELECT * FROM captures ORDER BY timestamp DESC")
    fun getAllCaptures(): Flow<List<Capture>>

    // Get single capture details by ID
    @Query("SELECT * FROM captures WHERE id = :id")
    fun getCaptureById(id: Int): Flow<Capture>

    // Delete single capture by ID
    @Query("DELETE FROM captures WHERE id = :id")
    suspend fun deleteCaptureById(id: Int)

    // Get unique capture dates (in "yyyy-MM-dd" format)
    @Query("SELECT DISTINCT date_string FROM captures ORDER BY date_string DESC")
    fun getUniqueCaptureDates(): Flow<List<String>>

    // Delete ALL captures
    @Query("DELETE FROM captures")
    suspend fun deleteAllCaptures()

    // Get entries by date
    @Query("SELECT * FROM captures WHERE date_string = :date ORDER BY timestamp DESC")
    fun getCapturesByDate(date: String): Flow<List<Capture>>

    // Get total ladyfish count by date
    @Query("SELECT SUM(ladyfish_count) FROM captures WHERE date_string = :date")
    fun getTotalLadyFishCountByDate(date: String): Flow<Int>

    @Query("SELECT * FROM captures ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCapture(): Flow<Capture>
}
