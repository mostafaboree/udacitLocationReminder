package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import com.udacity.project4.locationreminders.reminderslist.ReminderData
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val rTitle = MutableLiveData<String>()
    val rDescription = MutableLiveData<String>()
    val rSelectedLocationStr = MutableLiveData<String>()
    val selectPOI = MutableLiveData<PointOfInterest>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun clear() {
        rTitle.value = null
        rDescription.value = null
        rSelectedLocationStr.value = null
        selectPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderData): Boolean {
        if (validateEnteredD(reminderData)) {
            saveRe(reminderData)
            return true
        }
        return false
    }

    fun saveRe(data: ReminderData) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDAO(
                    data.title,
                    data.description,
                    data.location,
                    data.latitude,
                    data.longitude,
                    data.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredD(reminderData: ReminderData): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

}
