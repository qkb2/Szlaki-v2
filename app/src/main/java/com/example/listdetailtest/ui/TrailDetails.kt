package com.example.listdetailtest.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.listdetailtest.Trail
import com.example.listdetailtest.TrailItem

@Composable
fun TrailDetails(
    itemProvided: TrailItem,
    items: List<Trail>,
    onSwipe: (TrailItem) -> Unit,
    onHamburger: () -> Unit,
    onMode: () -> Unit
) {
    var item = itemProvided
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, delta ->
                    Log.w("MY_TAG", "ID: " + item.id.toString())
                    if (delta < 0) {
                        if (item.id < items.size - 1) {
                            val id = item.id + 1
                            Log.w("MY_TAG", "DELTA-: $delta")
                            item = TrailItem(id)
                            onSwipe(item)

                        }
                    } else if (delta > 0) {
                        if (item.id > 0) {
                            val id = item.id - 1
                            Log.w("MY_TAG", "DELTA+: $delta")
                            item = TrailItem(id)
                            onSwipe(item)
                        }
                    }
                }
            }
    ) {
        val trail = items[item.id]
        val context = LocalContext.current
        // State for managing theme mode
//        var isDarkMode by remember { mutableStateOf(false) }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(all = 8.dp),
                    shape = CircleShape,
                    onClick = {
                        Toast.makeText(context, "Take a photo then!", Toast.LENGTH_SHORT).show()
                    },
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "icon")
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            topBar = {
                AppBar(
                    onHamburgerClick = { onHamburger() },
                    onNightModeClick = {
//                        isDarkMode = !isDarkMode
//                        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
//                        AppCompatDelegate.setDefaultNightMode(mode)
                        onMode()
                    }
                )
            }
        ) {
            val whatever = it
            Card {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Details page for ${trail.name}",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(Modifier.size(16.dp))
                    Text(
                        text = "Description: ${trail.description}"
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Rating: ${trail.rating}"
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "Phases:",
                        fontWeight = FontWeight.Bold
                    )
                    trail.phases.forEach { phase ->
                        Text(
                            text = "- ${phase.name}: ${phase.description}"
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    Stopwatch()
                }
            }
        }
    }
}