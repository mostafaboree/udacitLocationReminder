package com.udacity.project4.locationreminders.data.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.project4.locationreminders.data.dto.ReminderDAO

/**
 * Data Access Object for the reminders table.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM reminders")
    suspend fun reminders(): List<ReminderDAO>

    @Query("SELECT * FROM reminders where entry_id = :reminderId")
    suspend fun reminderById(reminderId: String): ReminderDAO?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(reminder: ReminderDAO)


    @Query("DELETE FROM reminders")
    suspend fun clearAllReminders()

}