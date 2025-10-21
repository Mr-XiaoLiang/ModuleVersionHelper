package com.lollipop.mvh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.lollipop.mvh.ui.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val startDestination = Destination.GIT_STATE
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
        ) { contentPadding ->

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
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
            AppNavHost(navController, startDestination)
//            PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
//                Destination.entries.forEachIndexed { index, destination ->
//                    Tab(
//                        selected = selectedDestination == index,
//                        onClick = {
//                            navController.navigate(route = destination.name)
//                            selectedDestination = index
//                        },
//                        text = {
//                            Text(
//                                text = destination.label,
//                                maxLines = 2,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    )
//                }
//            }
//            AppNavHost(navController, startDestination)
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
