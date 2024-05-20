package com.example.listdetailtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listdetailtest.ui.ListDetailPaneScaffoldFull
import com.example.listdetailtest.ui.theme.ListDetailTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ViewModel to hold the loading state
            val mainViewModel: MainViewModel = viewModel()

            // State to handle dark mode toggle
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            ListDetailTestTheme(isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    // Load items asynchronously
                    LaunchedEffect(Unit) {
                        if (mainViewModel.isLoading) {
                            val items = loadItemsFromXml(context)
                            val itemsByType = loadHelper(items)
                            kotlinx.coroutines.delay(1000)
                            mainViewModel.isLoading = false
                        }
                    }

                    if (mainViewModel.isLoading) {
                        LoadingAnimation()
                    } else {
                        val items = remember { loadItemsFromXml(context) }
                        val itemsByType = remember { loadHelper(items) }

                        Column {
                            ListDetailPaneScaffoldFull(items, itemsByType) {
                                // Toggle dark mode
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
}

// ViewModel to hold the loading state
class MainViewModel : ViewModel() {
    var isLoading by mutableStateOf(true)

}