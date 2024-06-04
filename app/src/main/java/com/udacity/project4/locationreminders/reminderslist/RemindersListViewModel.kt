package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.udacity.project4.authentication.FirebaseUserLiveData
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.CaseData
import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import kotlinx.coroutines.launch

class RemindersListViewModel(
    app: Application,
    private val dataSource: ReminderDataSource
) : BaseViewModel(app) {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    // list that holds the reminder data to be displayed on the UI
    val remindersList = MutableLiveData<List<ReminderData>>()

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        showLoading.value = true
        viewModelScope.launch {
            // interacting with the dataSource has to be through a coroutine
            val result = dataSource.getReminders()
            showLoading.postValue(false)
            when (result) {
                is CaseData.Success<*> -> {
                    val dataList = ArrayList<ReminderData>()
                    var addAll = dataList.addAll(
                        (result.data as List<ReminderDAO>).map { reminder ->
                            // map the reminder data from the DB to the be ready to be displayed on the UI
                            ReminderData(
                                reminder.description,
                                reminder.longitude,
                                reminder.title,
                                reminder.latitude,
                                reminder.location

                            )
                        }
                    )
                    remindersList.value = dataList
                }
                is CaseData.Error ->
                    showSnackBar.value = result.message.toString()
            }

            // check if no data has to be shown
            invalidateShowNoData()
        }
    }

    /**
     * Inform the user that there's not any data if the remindersList is empty
     */
    private fun invalidateShowNoData() {
        showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()
    }
}
