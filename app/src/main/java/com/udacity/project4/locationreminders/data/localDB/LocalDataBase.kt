package com.udacity.project4.locationreminders.data.localDB

import android.content.Context
import androidx.room.Room


/**
 * Singleton class that is used to create a reminder db
 */
object LocalDataBase {

    /**
     * static method that creates a reminder class and returns the DAO of the reminder
     */
    fun createRemindersDao(context: Context): LocationDao {
        return Room.databaseBuilder(
            context.applicationContext,
            RemindersDatabase::class.java, "locationReminders.db"
        ).build().reminderDao()
    }

}