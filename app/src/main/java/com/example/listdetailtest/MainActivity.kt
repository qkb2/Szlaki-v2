package com.example.listdetailtest

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            // TODO: menu screen and loading animation
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
    // Currently selected item (trail)
    var selectedItem: TrailItem? by rememberSaveable(stateSaver = TrailItem.Saver) {
        mutableStateOf(null)
    }

    // Create the ListDetailPaneScaffoldState
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    BackHandler(navigator.canNavigateBack()) {
        // TODO: funny atypical back handler
        navigator.navigateBack()
    }

    // State for showing/hiding the drawer
    var isDrawerOpen by remember { mutableStateOf(false) }

    // State for managing theme mode
    var isDarkMode by remember { mutableStateOf(false) }

    // Main layout with top app bar and drawer
    Column(modifier = Modifier.fillMaxSize()) {
        // Top app bar
        // TODO: change app bar so that it hides on some occasions
        AppBar(
            onHamburgerClick = { isDrawerOpen = !isDrawerOpen },
            onNightModeClick = {
                isDarkMode = !isDarkMode
                val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Drawer
            // TODO: change to modal navigation drawer
            if (isDrawerOpen) {
                Drawer()
            }

            // List detail pane scaffold
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


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onHamburgerClick: () -> Unit,
    onNightModeClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "App Title") },
        navigationIcon = {
            IconButton(onClick = { onHamburgerClick() }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            // TODO: swap to day/night mode
            IconButton(onClick = { onNightModeClick() }) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Light/Dark Mode")
            }
        }
    )
}

@Composable
fun Drawer() {
    Text("Drawer Content")
}

@Composable
fun TrailList(
    onItemClick: (TrailItem) -> Unit,
    items: List<Trail>
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items.forEachIndexed { _, trail ->
            item {
                TrailCard(trail, onItemClick)
            }
        }
    }
}

@Composable
fun TrailCard(trail: Trail, onItemClick: (TrailItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick(TrailItem(trail.id)) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(photoList[trail.id]),
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trail.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
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

                // TODO: FAB for photos
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

    // TODO: make it so that recorded times are loaded from db

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

val photoList: Array<Int> = arrayOf(R.drawable.trail_image_0, R.drawable.trail_image_1)