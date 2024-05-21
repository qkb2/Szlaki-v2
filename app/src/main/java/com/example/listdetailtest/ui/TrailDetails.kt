package com.example.listdetailtest.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.listdetailtest.Trail
import com.example.listdetailtest.TrailItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetails(
    itemProvided: TrailItem,
    items: List<Trail>,
    onSwipe: (TrailItem) -> Unit,
    onHamburger: () -> Unit,
    onMode: () -> Unit
) {
    var item = itemProvided
    // Box can theoretically be enabled for swiping, but I don't know if it's a good idea to do so
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { _, delta ->
//                    Log.w("MY_TAG", "ID: " + item.id.toString())
//                    if (delta < -50) {
//                        if (item.id < items.size - 1) {
//                            val id = item.id + 1
//                            Log.w("MY_TAG", "DELTA-: $delta")
//                            item = TrailItem(id)
//                            onSwipe(item)
//
//                        }
//                    } else if (delta > 50) {
//                        if (item.id > 0) {
//                            val id = item.id - 1
//                            Log.w("MY_TAG", "DELTA+: $delta")
//                            item = TrailItem(id)
//                            onSwipe(item)
//                        }
//                    }
//                }
//            }
//    ) {
        val trail = items[item.id]
        val context = LocalContext.current

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppBar(
                    trail = trail,
                    scrollBehavior = scrollBehavior,
                    onHamburgerClick = onHamburger,
                    onNightModeClick = onMode
                ) },
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
            floatingActionButtonPosition = FabPosition.End
        ) {
            DetailsCard(trail = trail, it)
        }
//    }
}

@Composable
fun DetailsCard(trail: Trail, paddingValues: PaddingValues) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
//        Text(
//            text = "Details page for ${trail.name}",
//            style = MaterialTheme.typography.headlineLarge,
//        )
        Spacer(modifier = Modifier.padding(paddingValues))
//        Spacer(Modifier.size(16.dp))
        Text(
            text = "Description: ${trail.description}",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Rating: ${trail.rating}",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Difficulty: ${trail.difficulty}",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Phases:",
            style = MaterialTheme.typography.displaySmall
        )
        trail.phases.forEach { phase ->
            Text(
                text = "- ${phase.name}: ${phase.description}",
                style = MaterialTheme.typography.displaySmall
            )
        }
        Spacer(Modifier.size(8.dp))
        Stopwatch()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    trail: Trail,
    scrollBehavior: TopAppBarScrollBehavior,
    onHamburgerClick: () -> Unit,
    onNightModeClick: () -> Unit) {

    /*
    TODO: fix app bar to have properly overlaying image of greater size
    Probably should be done by treating photo as its own element that takes
    scroll behavior as its argument and only generates an image if > 0.5 or sth
    This should be stacked in a box with app bar and passed as app bar to top app bar in scaffold
     */

    val height = 400.dp
    if (scrollBehavior.state.collapsedFraction > 0.5) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    text = trail.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { onHamburgerClick() }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(onClick = { onNightModeClick() }) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Light/Dark Mode")
                }
            },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        )
    } else {
        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Image(
                painter = painterResource(photoList[trail.id]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = trail.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onHamburgerClick() }) {
                        Icon(Icons.Filled.Menu,
                            contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { onNightModeClick() }) {
                        Icon(Icons.Filled.CheckCircle,
                            contentDescription = "Light/Dark Mode", tint = Color.White)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    }
}