package com.udacity.project4.locationreminders.reminderslist

import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderData(
    var description: String?,
    var longitude: Double?,
    var title: String?,
    var latitude: Double?,
    var location: String?,

    val id: String = UUID.randomUUID().toString()

) : Serializable