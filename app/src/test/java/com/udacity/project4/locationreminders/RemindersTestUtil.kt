package com.udacity.project4.locationreminders

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import com.udacity.project4.locationreminders.reminderslist.ReminderData
import java.util.*

object RemindersTestUtil {
    fun createMockReminderDto() : ReminderDAO {
        return ReminderDAO(
            "Test Title",
            "Test Description",
            "Test Location",
            37.422160,
            -122.084270,
            id = UUID.randomUUID().toString()
        )
    }

    fun createMockPOI() : PointOfInterest {
        return PointOfInterest(LatLng(37.422160, -122.084270),"Test POI", "Test Name")

    }

    fun reminderDtoToReminder(reminderDTO: ReminderDAO): ReminderData {

        return ReminderData(
            reminderDTO.description,
            reminderDTO.longitude,
            reminderDTO.title,
            reminderDTO.latitude,
            reminderDTO.location,
            reminderDTO.id
        )
    }
}