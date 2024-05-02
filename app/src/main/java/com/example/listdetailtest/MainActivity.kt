package com.example.listdetailtest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                selectedItem?.let { item ->
                    TrailDetails(item, items)
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
fun TrailDetails(item: TrailItem, items: List<Trail>) {
    val trail = items[item.id]
    Card {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Details page for ${trail.name}",
                fontSize = 24.sp,
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "Description: ${trail.description}"
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Rating: ${trail.rating}"
            )
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
    val rating: Float
)

fun loadItemsFromXml(context: Context): List<Trail> {
    val xmlString = context.resources.openRawResource(R.raw.items).bufferedReader().use { it.readText() }
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(xmlString)))
    val itemsList = mutableListOf<Trail>()

    val items = document.getElementsByTagName("item")
    for (i in 0 until items.length) {
        val itemNode = items.item(i) as Element
        val id = itemNode.getAttribute("id").toInt()
        val name = itemNode.getAttribute("name")
        val description = itemNode.getAttribute("description")
        val rating = itemNode.getAttribute("rating").toFloat()
        itemsList.add(Trail(id, name, description, rating))
    }

    return itemsList
}
