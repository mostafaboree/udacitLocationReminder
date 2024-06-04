package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.CaseData
import com.udacity.project4.locationreminders.data.dto.ReminderDAO

// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource:ReminderDataSource {

    var remindersServiceData: LinkedHashMap<String, ReminderDAO> = LinkedHashMap()

    private var shouldReturnError = false

    //update error flag
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): CaseData<List<ReminderDAO>> {
        if (shouldReturnError) {
            return CaseData.Error(Exception("Test exception"))
        }
        return CaseData.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDAO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): CaseData<ReminderDAO> {
        if (shouldReturnError) {
            return CaseData.Error(Exception("Test exception"))
        }

        remindersServiceData[id]?.let {
            return CaseData.Success(it)
        }
        return CaseData.Error(Exception("Could not find reminder"))
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }
}
/*(var reminders: MutableList<ReminderDAO>? = mutableListOf()) : ReminderDataSource {

    private var returnsError = false

    fun setReturnsError(value: Boolean) {
        returnsError = value
    }

    *//*override suspend fun getReminders(): CaseData<List<ReminderDAO>> {
        if (returnsError) return CaseData.Error(Exception("ERROR"))
        else {
            reminders?.let { return CaseData.Success(ArrayList(it)) }
            return CaseData.Error(
                Exception(
                    "Reminders not found now"
                )
            )
        }
    }*//*



    override suspend fun saveReminder(reminder: ReminderDAO) {
        reminders?.add(reminder)
    }

   *//* override suspend fun getReminder(id: String): CaseData<ReminderDAO> {
        if (returnsError) return CaseData.Error(Exception("ERROR"))
        else {
            reminders?.let {
                for (reminder in it) {
                    if (reminder.id == id) return CaseData.Success(reminder)
                }
            }
            return CaseData.Error(
                Exception(
                    "Reminder not found"
                )
            )
        }
    }*//*
    override suspend fun getReminder(id: String):CaseData<ReminderDAO> =
        if (returnsError) {
            CaseData.Error(Exception("Error in getReminder()"))
        } else {
            val reminder = reminders!!.find { it.id ==  id}

            if (reminder != null){
                CaseData.Success(reminder)
            } else {
                CaseData.Error(Exception("Did not found reminder"))
            }
        }


    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    private var shouldReturnError = false

    //update error flag
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
/////////////////////////////////////////


 *//*   override suspend fun getReminders(): CaseData<List<ReminderDAO>> {
        if (shouldReturnError) {
            return CaseData.Error(
                Exception("Test exception"))
        }
        return CaseData.Success(remindersServiceData.values.toList())
    }



    override suspend fun getReminder(id: String): CaseData<ReminderDAO> {
        if (shouldReturnError) {
            return CaseData.Error(Exception("Test exception"))
        }

        remindersServiceData[id]?.let {
            return CaseData.Success(it)
        }
        return CaseData.Error(Exception("Could not find reminder"))
    }*//*


}
*/