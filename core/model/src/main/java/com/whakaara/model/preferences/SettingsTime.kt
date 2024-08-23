package com.whakaara.model.preferences

import androidx.annotation.StringRes
import com.whakaara.core.model.R

enum class SettingsTime(val value: Int) {
    TWO_SECONDS(5),
    FIVE_SECONDS(7),
    SEVEN_SECONDS(9),
    NINE_SECONDS(11),
    TEN_SECONDS(13),
    TWELVE_SECONDS(15),
    FIFTEEN_SECONDS(17);

    @StringRes
    fun getStringResource(ordinal: Int): Int {
        return when (ordinal) {
            0 -> R.string.settings_screen_time_two_seconds
            1 -> R.string.settings_screen_time_five_seconds
            2 -> R.string.settings_screen_time_seven_seconds
            3 -> R.string.settings_screen_time_nine_seconds
            4 -> R.string.settings_screen_time_ten_seconds
            5 -> R.string.settings_screen_time_twelve_seconds
            6 -> R.string.settings_screen_time_fifteen_seconds
            else -> R.string.settings_screen_time_ten_seconds
        }
    }

    companion object {
        fun fromOrdinalInt(value: Int) = entries.first { it.ordinal == value }
    }
}
