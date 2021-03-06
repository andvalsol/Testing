package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, private val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String>().apply { value = "" }

    val reminderDescription = MutableLiveData<String>().apply { value = "" }

    val reminderSelectedLocationStr = MutableLiveData<String>().apply { value = "" }

    private val _selectedPOI = MutableLiveData<PointOfInterest>()
    val selectedPOI: LiveData<PointOfInterest>
        get() = _selectedPOI

    private val _latitude = MutableLiveData<Double>().apply { value = 0.0 }
    val latitude: LiveData<Double>
        get() = _latitude

    private val _longitude = MutableLiveData<Double>().apply { value = 0.0 }
    val longitude: LiveData<Double>
        get() = _longitude

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        _selectedPOI.value = null
        _latitude.value = null
        _longitude.value = null
    }

    fun setLatLng(latLng: LatLng) {
        _latitude.value = latLng.latitude
        _longitude.value = latLng.longitude
    }

    fun onPOISelected() {
        _selectedPOI.value =
            PointOfInterest(
                LatLng(
                    _latitude.value!!,
                    _longitude.value!!
                ),
                reminderTitle.value,
                reminderDescription.value
            )
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
        }
    }

    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
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
    private fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.description.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_description
            return false
        }

        if (reminderData.latitude == null || reminderData.longitude == null) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }
}