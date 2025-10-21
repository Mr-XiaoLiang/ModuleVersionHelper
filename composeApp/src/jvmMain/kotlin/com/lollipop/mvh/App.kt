package com.lollipop.mvh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lollipop.mvh.ui.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        val startDestination = Destination.GIT_STATE
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .safeContentPadding()
                .fillMaxSize(),
        ) { contentPadding ->
            Row {
                NavigationRail(modifier = Modifier.padding(contentPadding)) {
                    Destination.entries.forEachIndexed { index, destination ->
                        NavigationRailItem(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(route = destination.name)
                                selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    getDestinationIcon(destination),
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
                AppNavHost(navController, startDestination)
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.name
    ) {
        composable(Destination.GIT_STATE.name) {
            Box(Modifier.fillMaxSize().background(Color.Red)) {
                Text(text = Destination.GIT_STATE.label)
            }
        }
        composable(Destination.REPOSITORY_LIST.name) {
            Box(Modifier.fillMaxSize().background(Color.Blue)) {
                Text(text = Destination.REPOSITORY_LIST.label)
            }
        }
        composable(Destination.ADD_REPOSITORY.name) {
            Box(Modifier.fillMaxSize().background(Color.Black))

        }
        composable(Destination.VERSION_COLUMN_MANAGER.name) {
            Box(Modifier.fillMaxSize().background(Color.Yellow))
        }
        composable(Destination.ADD_VERSION_COLUMN.name) {
            Box(Modifier.fillMaxSize().background(Color.Cyan))
        }
        composable(Destination.MODULE_INFO_OUTPUT.name) {
            Box(Modifier.fillMaxSize().background(Color.Magenta))
        }
    }
}

private fun getDestinationIcon(destination: Destination): ImageVector {
    return when (destination) {
        Destination.GIT_STATE -> Icons.Filled.Commit
        Destination.REPOSITORY_LIST -> Icons.AutoMirrored.Filled.List
        Destination.ADD_REPOSITORY -> Icons.Filled.Add
        Destination.VERSION_COLUMN_MANAGER -> Icons.Filled.Info
        Destination.ADD_VERSION_COLUMN -> Icons.Filled.Add
        Destination.MODULE_INFO_OUTPUT -> Icons.Filled.Output
    }
}
