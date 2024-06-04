package com.udacity.project4.locationreminders.data.localDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.locationreminders.data.dto.ReminderDAO


@Database(entities = [ReminderDAO::class], version = 1, exportSchema = false)
abstract class RemindersDatabase : RoomDatabase() {

    abstract fun reminderDao(): LocationDao
}