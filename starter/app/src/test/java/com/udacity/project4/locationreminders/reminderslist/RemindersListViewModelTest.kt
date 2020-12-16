package com.udacity.project4.locationreminders.reminderslist
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadReminders_addRemindersToLiveData_ifSuccessful() = mainCoroutineRule.runBlockingTest {
        // Create a FakeDataSource
        val fakeDataSource = FakeDataSource()

        // Insert a reminder in the FakeDataSource so that we can retrieve it later
        fakeDataSource.saveReminder(ReminderDTO(
            "title",
            "description",
            "location",
            0.0,
            0.0
        ))

        val context = ApplicationProvider.getApplicationContext() as Context

        FirebaseApp.initializeApp(context)

        // Given a ReminderListViewModel
        val remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource) // need an application context

        // When loading the reminders, this function has suspending function
        remindersListViewModel.loadReminders()

        // Then the remindersList live data shows the list of reminders
        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertEquals(value.isNotEmpty(), value.isNotEmpty())
    }
}