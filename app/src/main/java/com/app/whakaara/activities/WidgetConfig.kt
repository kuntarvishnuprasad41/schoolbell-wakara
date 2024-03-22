package com.app.whakaara.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.app.whakaara.R
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.ui.widget.ColourPicker
import com.app.whakaara.widget.AppWidget
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class WidgetConfig : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            // unsure if possible to access widget state here to pre-set the slider values to current values
            val alphaBackground = rememberSaveable { mutableFloatStateOf(1f) }
            val redBackground = rememberSaveable { mutableFloatStateOf(0f) }
            val greenBackground = rememberSaveable { mutableFloatStateOf(0f) }
            val blueBackground = rememberSaveable { mutableFloatStateOf(0f) }
            val colorBackground by remember {
                derivedStateOf {
                    Color(redBackground.value, greenBackground.value, blueBackground.value, alphaBackground.value)
                }
            }

            val alphaText = rememberSaveable { mutableFloatStateOf(1f) }
            val redText = rememberSaveable { mutableFloatStateOf(0f) }
            val greenText = rememberSaveable { mutableFloatStateOf(0f) }
            val blueText = rememberSaveable { mutableFloatStateOf(0f) }
            val colorText by remember {
                derivedStateOf {
                    Color(redText.value, greenText.value, blueText.value, alphaText.value)
                }
            }

            WhakaaraTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(spaceMedium),
                            text = stringResource(id = R.string.widget_config_background_colour),
                            style = MaterialTheme.typography.titleLarge
                        )
                        ColourPicker(
                            alpha = alphaBackground,
                            red = redBackground,
                            green = greenBackground,
                            blue = blueBackground,
                            color = colorBackground
                        )

                        Text(
                            modifier = Modifier.padding(spaceMedium),
                            text = stringResource(id = R.string.widget_config_text_colour),
                            style = MaterialTheme.typography.titleLarge
                        )
                        ColourPicker(
                            alpha = alphaText,
                            red = redText,
                            green = greenText,
                            blue = blueText,
                            color = colorText
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spaceMedium),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val resultValue = Intent().apply {
                                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                    }
                                    setResult(Activity.RESULT_OK, resultValue)
                                    finish()
                                }
                            ) {
                                Text(text = stringResource(id = R.string.bottom_sheet_close_button))
                            }
                            Spacer(modifier = Modifier.width(space10))
                            Button(
                                onClick = {
                                    runBlocking {
                                        val glanceAppWidget: GlanceAppWidget = AppWidget()
                                        val serializedBackground = Gson().toJson(colorBackground)
                                        val serializedText = Gson().toJson(colorText)
                                        val glanceId = GlanceAppWidgetManager(this@WidgetConfig).getGlanceIdBy(appWidgetId)
                                        updateAppWidgetState(this@WidgetConfig, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                                            prefs.toMutablePreferences().apply {
                                                this[backgroundKey] = serializedBackground
                                                this[textKey] = serializedText
                                            }
                                        }
                                        glanceAppWidget.update(this@WidgetConfig, glanceId)
                                    }
                                    setResult(
                                        Activity.RESULT_OK,
                                        Intent().apply {
                                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                        }
                                    )
                                    finish()
                                }
                            ) {
                                Text(text = stringResource(id = R.string.bottom_sheet_save_button))
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val backgroundKey = stringPreferencesKey("colour_background")
        val textKey = stringPreferencesKey("colour_text")
    }
}