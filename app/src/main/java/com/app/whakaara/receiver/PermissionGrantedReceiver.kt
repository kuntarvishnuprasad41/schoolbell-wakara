package com.app.whakaara.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.utils.DateUtils.Companion.getTimeInMillis
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PermissionGrantedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repo.getAllAlarms().filter { it.isEnabled }.forEach {
                        createAlarm(alarm = it, context = context)
                    }
                }
            }
        }
    }

    private fun createAlarm(
        alarm: Alarm,
        context: Context?
    ) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setExactAlarm(alarm, alarmManager, context)
    }

    private fun setExactAlarm(
        alarm: Alarm,
        alarmManager: AlarmManager,
        context: Context
    ) {
        val startReceiverIntent = getStartReceiverIntent(alarm, context)
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = context,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getTimeInMillis(alarm.date),
            pendingIntent
        )
    }

    private fun getStartReceiverIntent(alarm: Alarm, context: Context) =
        Intent(context, NotificationReceiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(NotificationUtilsConstants.INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
        }
}
