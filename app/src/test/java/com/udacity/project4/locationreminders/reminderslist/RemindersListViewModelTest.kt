package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.DummyReminderData
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // provide testing to the RemindersListViewModel and its live data objects
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var remindersRepository: FakeDataSource

    @Before
    fun setupViewModel() {
        stopKoin()
        // Initialise the repository with no reminders.
        remindersRepository = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), remindersRepository
        )
    }

    // Verify that showLoading value is set to true when loading
    // and false after loading
    @Test
    fun loadReminders_loading() {
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // WHEN load reminders
        remindersListViewModel.loadReminders()

        // THEN: the progress indicator is shown.
            assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // THEN: the progress indicator is hidden.
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }


    @Test
    fun loadReminders_Success() = runBlockingTest {
        // GIVEN items
        DummyReminderData.items.forEach { reminderDTO ->
            remindersRepository.saveReminder(reminderDTO)
        }

        // WHEN load reminders
        remindersListViewModel.loadReminders()

        // THEN Data is the same with source
        val loadedItems = remindersListViewModel.remindersList.getOrAwaitValue()
        MatcherAssert.assertThat(loadedItems.size, `is`(DummyReminderData.items.size))

        for (i in loadedItems.indices) {
            MatcherAssert.assertThat(loadedItems[i].title, `is`(DummyReminderData.items[i].title))
        }
        // showNoData is false
        MatcherAssert.assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(false))
    }


    // Make sure that Snack bar error message value is triggered when loading reminders fails
    // should Return Error
    @Test
    fun loadReminders_DataSource_Error() {
         runBlockingTest {
             // GIVEN the DataSource return errors.
             remindersRepository.setShouldReturnError(true)
             saveReminder()

             // WHEN load reminders
             remindersListViewModel.loadReminders()

             // THEN Show error message in SnackBar
             MatcherAssert.assertThat(
                 remindersListViewModel.showSnackBar.value,
                 CoreMatchers.`is`("Returning testing error!")

             )
         }
     }


    //test if no reminder are there
    @Test
    fun loadReminders_resultSuccess_noReminders() = runBlockingTest {
        // GIVEN no items
        remindersRepository.deleteAllReminders()

        // WHEN load reminders
        remindersListViewModel.loadReminders()

        // THEN Size is zero
        val loadedItems = remindersListViewModel.remindersList.getOrAwaitValue()
        MatcherAssert.assertThat(loadedItems.size, CoreMatchers.`is`(0))

        // showNoData is true
        MatcherAssert.assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    private suspend fun saveReminder() {
        remindersRepository.saveReminder(
            ReminderDTO("title",
                "description",
                "location",
                100.00,
                10.00)
        )
    }
}