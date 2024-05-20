package com.example.listdetailtest.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.listdetailtest.R
import com.example.listdetailtest.Trail
import com.example.listdetailtest.TrailItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailPaneScaffoldFull(
    items: List<Trail>, itemsByType: List<List<Trail>>, onMode: () -> Unit) {
//    val context = LocalContext.current
//    val items = remember { loadItemsFromXml(context) }
    // Currently selected item (trail)
    var selectedItem: TrailItem? by rememberSaveable(stateSaver = TrailItem.Saver) {
        mutableStateOf(null)
    }

    // Create the ListDetailPaneScaffoldState
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    BackHandler(navigator.canNavigateBack()) {
        // TODO: funny atypical back handler (probably won't do that one)
        navigator.navigateBack()
    }

    // State for managing theme mode
    var isDarkMode by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Main layout with top app bar and drawer
    Column(modifier = Modifier.fillMaxSize()) {
        // Drawer with navigation
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("Trails", modifier = Modifier.padding(16.dp))
                    Divider()

                    items.forEachIndexed { _, trail ->
                        NavigationDrawerItem(
                            label = { Text(text = trail.name) },
                            selected = false,
                            onClick = {
                                selectedItem = TrailItem(trail.id)
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                            }
                        )
                    }



                }
            },
        ) {
            // List detail pane scaffold
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane(Modifier) {
                        TrailList(
                            onItemClick = { id: TrailItem ->
                                // Set current item
                                selectedItem = id
                                // Display the detail pane
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                            },
                            items, itemsByType
                        )
                    }
                },
                detailPane = {
                    AnimatedPane(Modifier) {
                        // Show the detail pane content if selected item is available
                        if (selectedItem == null) {
                            selectedItem = TrailItem(0)
                        }
                        TrailDetails(selectedItem!!, items,
                            onSwipe = { id: TrailItem ->
                            selectedItem = id
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }, onHamburger = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                        }, onMode = {onMode()})
                    }
                },
            )
        }
    }
}

val photoList: Array<Int> = arrayOf(
    R.drawable.trail_image_0,
    R.drawable.trail_image_1,
    R.drawable.trail_image_0,
    R.drawable.trail_image_1,
    R.drawable.trail_image_0,
    R.drawable.trail_image_1)

