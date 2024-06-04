package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import com.udacity.project4.locationreminders.data.dto.CaseData

/**
 * Main entry point for accessing reminders data.
 */
interface ReminderDataSource {
    suspend fun getReminders(): CaseData<List<ReminderDAO>>
    suspend fun saveReminder(reminder: ReminderDAO)
    suspend fun getReminder(id: String): CaseData<ReminderDAO>
    suspend fun deleteAllReminders()
}