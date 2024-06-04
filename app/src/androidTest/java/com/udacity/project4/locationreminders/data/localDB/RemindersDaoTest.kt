package com.udacity.project4.locationreminders.data.localDB

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
// Unit test the DAO
@SmallTest
class RemindersDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var application: Application
    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun setupDatabase() {
        application = ApplicationProvider.getApplicationContext()
        remindersDatabase = Room.inMemoryDatabaseBuilder(application, RemindersDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @Test
    fun insertEqualsRetrieve() = runBlocking {
        val reminder = ReminderDAO("Title", "Description", "Location", 19.0, 20.2)

        remindersDatabase.reminderDao().saveLocation(reminder)
        val reminder2: ReminderDAO? = remindersDatabase.reminderDao().reminderById(reminder.id)

        assertThat(reminder2, `is`(reminder))
    }

    @Test
    fun noReminderForDeleted() = runBlocking {
        val reminder = ReminderDAO("Title", "Description", "Location", 19.0, 20.2)
        val id = reminder.id
        remindersDatabase.reminderDao().saveLocation(reminder)
        remindersDatabase.reminderDao().clearAllReminders()

        val result = remindersDatabase.reminderDao().reminderById(id)

        assertThat(result, `is`(nullValue()))
    }
}
