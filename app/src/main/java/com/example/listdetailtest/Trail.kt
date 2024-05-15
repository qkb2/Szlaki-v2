package com.example.listdetailtest

import android.content.Context
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

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
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        InputSource(
            StringReader(xmlString)
        )
    )
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
