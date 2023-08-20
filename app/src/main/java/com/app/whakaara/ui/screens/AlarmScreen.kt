package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss

@Composable
fun AlarmScreen(
    alarmState: AlarmState,
    preferencesState: PreferencesState,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    CardContainerSwipeToDismiss(
        alarms = alarmState,
        preferencesState = preferencesState,
        delete = delete,
        disable = disable,
        enable = enable,
        reset = reset
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen(
        alarmState = AlarmState(),
        preferencesState = PreferencesState(),
        delete = {},
        disable = {},
        enable = {},
        reset = {}
    )
}
