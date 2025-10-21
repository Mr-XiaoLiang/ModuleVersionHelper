package com.lollipop.mvh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lollipop.mvh.ui.*

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
            GitStatePage()
        }
        composable(Destination.REPOSITORY_LIST.name) {
            RepositoryListPage()
        }
        composable(Destination.ADD_REPOSITORY.name) {
            AddRepositoryPage()
        }
        composable(Destination.VERSION_COLUMN_MANAGER.name) {
            VersionColumnManagerPage()
        }
        composable(Destination.ADD_VERSION_COLUMN.name) {
            AddVersionColumnPage()
        }
        composable(Destination.MODULE_INFO_OUTPUT.name) {
            ModuleInfoOutputPage()
        }
    }
}

private fun getDestinationIcon(destination: Destination): ImageVector {
    return when (destination) {
        Destination.GIT_STATE -> Icons.Filled.Commit
        Destination.REPOSITORY_LIST -> Icons.AutoMirrored.Filled.List
        Destination.ADD_REPOSITORY -> Icons.Filled.AddHome
        Destination.VERSION_COLUMN_MANAGER -> Icons.Filled.Info
        Destination.ADD_VERSION_COLUMN -> Icons.Filled.NewLabel
        Destination.MODULE_INFO_OUTPUT -> Icons.Filled.Output
    }
}
