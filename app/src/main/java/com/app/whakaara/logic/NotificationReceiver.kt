package com.app.whakaara.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.core.content.ContextCompat.startActivity
import com.app.whakaara.MainActivity
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    @OptIn(ExperimentalLayoutApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action.equals("cancel")) {
                val alarmId = intent.getStringExtra("alarmId")
                if (alarmId != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.isEnabled(id = UUID.fromString(alarmId), isEnabled = false)
                    }
                    removeNotification(
                        context = context,
                        id = intent.getIntExtra("notificationId", 1)
                    )
                }

                /**
                 * IF we are already in the app, and we get a notification, this will re-open the main activity.
                 * This will cause the UI to refresh. I dont know why the UI doesn't refresh when we update the item in DB.
                 * But re-opening the activity or adding/updating another item in the list will also refresh the UI.
                 * **/
                val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(context, mainActivityIntent, null)
            } else if (intent.action.equals("snooze")) {
                println("TODO")
            }
        } catch (exception: Exception) {
            Log.d("Notification receiver exception", exception.printStackTrace().toString())
        }
    }

    private fun removeNotification(context: Context, id: Int) {
        NotificationUtils(context).getManager().cancel(id)
    }
}