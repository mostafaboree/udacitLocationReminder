package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.udacity.project4.MyApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.RemindersTestUtil
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake repository to be injected into the viewmodel
    private val repository = FakeDataSource()

    // Subject under test (sut)
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // setup before running each test
    @Before
    fun setup() = runBlocking {

        // initialize firebase app
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)

        // initialize viewModel
        viewModel = RemindersListViewModel(MyApp(), repository)
    }

    @After
    fun cleanup() = runBlocking {
        repository.deleteAllReminders()
        stopKoin() // ensure single instance fof Koin
    }

    // Verify showLoading value is set to true when loading, then false after loading
    @Test
    fun loadReminders_showLoading() = runBlocking {

        // GIVEN - initialise reminders repo with some reminders (3)
        repository.saveReminder(RemindersTestUtil.createMockReminderDto())
        repository.saveReminder(RemindersTestUtil.createMockReminderDto())
        repository.saveReminder(RemindersTestUtil.createMockReminderDto())

        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // WHEN - Load the reminders in the view model.
        viewModel.loadReminders()

        // THEN - assert showLoading value
        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )

        // GIVEN - Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // THEN - assert showLoading value
        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

    // Verify Snackbar error message value is triggered when loading reminders fails
    @Test
    fun loadRemindersWhenRemindersAreUnavailable_showSnackBar() = runBlocking {

        //GIVEN - Simulate repo error response
        repository.setReturnError(true)

        // WHEN - Load reminders
        viewModel.loadReminders()
        val x:Exception= Exception("Test exception")

        // THEN - assert showSnackBar value is set with error message
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`(x.toString()))
    }

    @Test
    fun loadRemindersWithNoReminders_showNoData() = runBlocking{

        // GIVEN - clear all reminders
        repository.deleteAllReminders()

        // WHEN - fetch reminders
        viewModel.loadReminders()

        //THEN - assert showNoData value
        assertThat(viewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    @Test
    fun loadRemindersWithReminders_showNoData() = runBlocking{

        // GIVEN - initialise reminders repo with a reminders
        repository.saveReminder(RemindersTestUtil.createMockReminderDto())

        // WHEN - fetch reminders
        viewModel.loadReminders()

        //THEN - assert showNoData value, and remindersList size
        assertThat(viewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(false))
        assertThat(viewModel.remindersList.value?.size, CoreMatchers.`is`(1))
    }

}

/*

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var application: Application

    private lateinit var dataSource: FakeDataSource

    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        stopKoin()
        application = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(application)
        dataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(application, dataSource)
    }

    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDAO("Title", "Description", "Location", 19.0, 20.2)
        dataSource.saveReminder(reminder)

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()
        assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true)
        )

        mainCoroutineRule.resumeDispatcher()
        assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false)
        )
    }
    @Test
    fun withReminders_resultNotEmpty() = runBlockingTest {
        dataSource.saveReminder(
            ReminderDAO(
                "Test",
                "testing",
                null,
                2.89893,
                1.98893
            )
        )

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isEmpty()).isFalse()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue()).isFalse()
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue()).isFalse()
    }

    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`(notNullValue())
        )
    }
    }

*/

