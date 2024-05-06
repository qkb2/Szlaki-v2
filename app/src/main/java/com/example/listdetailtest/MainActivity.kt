package com.example.listdetailtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListDetailPaneScaffoldFull()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
fun ListDetailPaneScaffoldFull() {
    val context = LocalContext.current
    val items = remember { loadItemsFromXml(context) }
    // Currently selected item
    var selectedItem: TrailItem? by rememberSaveable(stateSaver = TrailItem.Saver) {
        mutableStateOf(null)
    }

    // Create the ListDetailPaneScaffoldState
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane(Modifier) {
                TrailList(
                    onItemClick = { id ->
                        // Set current item
                        selectedItem = id
                        // Display the detail pane
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    },
                    items
                )
            }
        },
        detailPane = {
            AnimatedPane(Modifier) {
                // Show the detail pane content if selected item is available
                if (selectedItem == null) {
                    selectedItem = TrailItem(0)
                }
                TrailDetails(selectedItem!!, items) { id ->
                    selectedItem = id
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            }
        },
    )
}

@Composable
fun TrailList(
    onItemClick: (TrailItem) -> Unit,
    items: List<Trail>
) {
    Card {
        LazyColumn {
            items.forEachIndexed { id, trail ->
                item {
                    ListItem(
                        modifier = Modifier
                            .background(Color.Magenta)
                            .clickable {
                                onItemClick(TrailItem(id))
                            },
                        headlineContent = {
                            Text(
                                text = trail.name,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun TrailDetails(itemProvided: TrailItem, items: List<Trail>, onSwipe: (TrailItem) -> Unit) {
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

class TrailItem(val id: Int) {
    companion object {
        val Saver: Saver<TrailItem?, Int> = Saver(
            { it?.id },
            ::TrailItem,
        )
    }
}

data class Trail(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Float,
    val phases: List<Phase> // Added list of phases
)

data class Phase(
    val name: String,
    val description: String
)

fun loadItemsFromXml(context: Context): List<Trail> {
    val xmlString = context.resources.openRawResource(R.raw.trails).bufferedReader().use { it.readText() }
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(xmlString)))
    val itemsList = mutableListOf<Trail>()

    val items = document.getElementsByTagName("item")
    for (i in 0 until items.length) {
        val itemNode = items.item(i) as Element
        val id = itemNode.getAttribute("id").toInt()
        val name = itemNode.getAttribute("name")
        val description = itemNode.getAttribute("description")
        val rating = itemNode.getAttribute("rating").toFloat()
        val phases = mutableListOf<Phase>()

        val phaseNodes = itemNode.getElementsByTagName("phase")
        for (j in 0 until phaseNodes.length) {
            val phaseNode = phaseNodes.item(j) as Element
            val phaseName = phaseNode.getAttribute("name")
            val phaseDescription = phaseNode.getAttribute("description")
            phases.add(Phase(phaseName, phaseDescription))
        }

        itemsList.add(Trail(id, name, description, rating, phases))
    }

    return itemsList
}

@Composable
fun Stopwatch() {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var job by remember { mutableStateOf<Job?>(null) }
    val recordedTimes = remember { mutableStateListOf<Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()

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
                recordedTimes.add(elapsedSeconds)
                elapsedSeconds = 0
            }) {
                Text(text = "Save Time")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                isRunning = false
                job?.cancel()
                elapsedSeconds = 0
                recordedTimes.clear()
            }) {
                Text(text = "Reset")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recorded Times:")
        Column {
            recordedTimes.forEachIndexed { index, time ->
                Text("$index: ${formatTime(time)}")
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

