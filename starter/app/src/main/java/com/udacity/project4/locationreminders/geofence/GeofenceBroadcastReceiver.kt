package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.data.dto.toDataModel
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*


/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SaveReminderFragment.ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                // Early exit
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Get the geofence ID from the list of geofences
                val geofenceID = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() ->
                        geofencingEvent.triggeringGeofences[0].requestId
                    else -> return
                }

                // Get an instance of the database
                val reminderDao = RemindersDatabase.getInstance(context).reminderDao

                // Match this ID with an ID from the local database
                GlobalScope.launch {
                    val reminderDTO = reminderDao.getReminderById(geofenceID)
                    reminderDTO?.toDataModel()?.let { reminderDataItem ->
                        // NOTE: The reminderDTO could be null
                        withContext(Dispatchers.Main) {
                            // Send the notification
                            sendNotification(context, reminderDataItem)
                        }
                    }
                }
            }
        }
    }
}