package com.udacity.project4.locationreminders.data.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@SmallTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase

    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule =
        InstantTaskExecutorRule() // This makes each task synchronously when using Architecture Components

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_remindersNotEmpty() = mainCoroutineRule.runBlockingTest {
        val repository = RemindersLocalRepository(database.reminderDao, Dispatchers.Main)

        val reminderDTO = ReminderDTO(
            "title",
            "description",
            "location",
            0.0,
            0.0
        )

        repository.saveReminder(reminderDTO)

        val result = repository.getReminders() as Result.Success

        // Check that the result size is not empty
        assertEquals(1, result.data.size)
    }
}