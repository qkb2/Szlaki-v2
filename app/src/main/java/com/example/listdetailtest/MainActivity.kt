package com.example.listdetailtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.listdetailtest.ui.ListDetailPaneScaffoldFull
import com.example.listdetailtest.ui.theme.ListDetailTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO: menu screen and loading animation
            var isDarkMode by remember { mutableStateOf(false) }
            ListDetailTestTheme(isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val items = remember { loadItemsFromXml(context) }


                    Column {
                        /*
                        NOTE TO SELF (DONE):
                        tabs should actually take place of app bar in trail list
                        and app bar should only be generated in app details
                        really ingenious of me, if I say so myself
                         */
                        ListDetailPaneScaffoldFull(items) {
                            isDarkMode = !isDarkMode
                            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                            AppCompatDelegate.setDefaultNightMode(mode)
                        }
                    }
                }
            }
        }
    }
}

class TrailItem(val id: Int) {
    companion object {
        val Saver: Saver<TrailItem?, Int> = Saver(
            { it?.id },
            ::TrailItem,
        )
    }
}