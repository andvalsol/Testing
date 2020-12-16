package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    // Since we're executing tests on a ViewModel (which is part of Android Architecture components)
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Need to setup previously the view model
    @Before
    fun setupViewModel() {
        val context = ApplicationProvider.getApplicationContext() as Context

        val database = RemindersDatabase.getInstance(context)

        val repository = RemindersLocalRepository(database.reminderDao)

        FirebaseApp.initializeApp(context)

        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), repository)
    }

    @Test
    fun onClearData_nullLiveData() {
        // Clear the LiveData from the ViewModel
        saveReminderViewModel.onClear()

        // Set null values to LiveData
        assertEquals(null, saveReminderViewModel.reminderTitle.value)
        assertEquals(null, saveReminderViewModel.reminderDescription.value)
        assertEquals(null, saveReminderViewModel.reminderSelectedLocationStr.value)
        assertEquals(null, saveReminderViewModel.latitude.value)
        assertEquals(null, saveReminderViewModel.longitude.value)
        assertEquals(null, saveReminderViewModel.selectedPOI.value)
    }
}