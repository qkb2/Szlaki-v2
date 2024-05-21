package com.example.listdetailtest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.listdetailtest.AppDatabase
import com.example.listdetailtest.RecordedTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Stopwatch(stopwatchId: Int) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var job by remember { mutableStateOf<Job?>(null) }
    val recordedTimes = remember { mutableStateListOf<RecordedTime>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val recordedTimeDao = db.recordedTimeDao()

    LaunchedEffect(stopwatchId) {
        recordedTimes.clear()
        val times = recordedTimeDao.getTimesForStopwatch(stopwatchId)
        recordedTimes.addAll(times)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatTime(elapsedSeconds), style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                isRunning = !isRunning
                if (isRunning) {
                    job = coroutineScope.launch {
                        while (true) {
                            delay(1000)
                            if (isRunning) {
                                elapsedSeconds++
                            }
                        }
                    }
                } else {
                    job?.cancel()
                }
            }) {
                Text(text = if (isRunning) "Pause" else "Start")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                isRunning = false
                job?.cancel()
                coroutineScope.launch {
                    recordedTimeDao.insert(RecordedTime(stopwatchId = stopwatchId, timeInSeconds = elapsedSeconds))
                    recordedTimes.add(RecordedTime(stopwatchId = stopwatchId, timeInSeconds = elapsedSeconds))
                    elapsedSeconds = 0
                }
            }) {
                Text(text = "Save Time")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                isRunning = false
                job?.cancel()
                elapsedSeconds = 0
            }) {
                Text(text = "Reset")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recorded Times:")
        Column {
            recordedTimes.forEachIndexed { index, time ->
                Text("$index: ${formatTime(time.timeInSeconds)}")
            }
        }
    }
}

@Composable
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}