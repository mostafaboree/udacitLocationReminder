package com.udacity.project4.locationreminders.data.localDB

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import com.udacity.project4.locationreminders.data.dto.CaseData
import com.udacity.project4.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param locationDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */
class LocalRepository(
    private val locationDao: LocationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {

    /**
     * Get the reminders list from the local db
     * @return Result the holds a Success with all the reminders or an Error object with the error message
     */
    override suspend fun getReminders(): CaseData<List<ReminderDAO>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                CaseData.Success(locationDao.reminders())
            } catch (ex: Exception) {
                CaseData.Error(ex)
            }
        }
    }

    /**
     * Insert a reminder in the db.
     * @param reminder the reminder to be inserted
     */
    override suspend fun saveReminder(reminder: ReminderDAO) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                locationDao.saveLocation(reminder)
            }
        }

    /**
     * Get a reminder by its id
     * @param id to be used to get the reminder
     * @return Result the holds a Success object with the Reminder or an Error object with the error message
     */
    override suspend fun getReminder(id: String): CaseData<ReminderDAO> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            try {
                val reminder = locationDao.reminderById(id)
                if (reminder != null) {
                    return@withContext CaseData.Success(reminder)
                } else {
                    return@withContext CaseData.Error(Exception("Reminder not found!"))
                }
            } catch (e: Exception) {
                return@withContext CaseData.Error(e)
            }
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllReminders() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                locationDao.clearAllReminders()
            }
        }
    }
}
