package com.example.listdetailtest.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable

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
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    )
}