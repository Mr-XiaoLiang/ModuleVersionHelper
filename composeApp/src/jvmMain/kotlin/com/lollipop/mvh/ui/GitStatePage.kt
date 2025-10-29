package com.lollipop.mvh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Cyclone
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.mvh.git.GitLog
import com.lollipop.mvh.git.GitRepository
import com.lollipop.mvh.git.GitState
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.ListItem

@Composable
fun GitStatePage() {
    ContentPage {
        val repositoryList = remember { RepositoryPageState.repositoryList }
        var selectedRepository by remember { mutableStateOf<GitRepository?>(null) }
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = "${repositoryList.size}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Icon(
                        modifier = Modifier.size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {

                            })
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )

                    Icon(
                        modifier = Modifier.size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = {
                                repositoryList.forEach { it.update() }
                            })
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        imageVector = Icons.Filled.CloudDownload,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )

                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1F)
                ) {
                    items(repositoryList.size) { index ->
                        ListItem {
                            val repository = repositoryList[index]
                            val gitState by remember { repository.state }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = getGitStateIcon(gitState),
                                    contentDescription = "",
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1F),
                                ) {
                                    Text(
                                        text = repository.projectInfo.displayName,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = repository.projectInfo.remote,
                                        fontSize = 14.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.CloudDownload,
                                    contentDescription = "CloudDownload",
                                    modifier = Modifier.size(56.dp)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = {
                                            repository.update()
                                        })
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Deselect",
                                    modifier = Modifier.size(56.dp)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = {
                                            selectedRepository = if (selectedRepository === repository) {
                                                null
                                            } else {
                                                repository
                                            }
                                        })
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
            selectedRepository?.let { repository ->
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    val logList = remember { repository.logList }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        items(logList) { item ->
                            val color = when (item) {
                                is GitLog.Error -> Color.Red
                                is GitLog.Info -> MaterialTheme.colorScheme.onBackground
                                is GitLog.Surprise -> Color.Red
                            }
                            SelectionContainer {
                                Text(
                                    text = item.msg,
                                    fontSize = 14.sp,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

private fun getGitStateIcon(gitState: GitState): ImageVector {
    return when (gitState) {
        is GitState.Error -> {
            Icons.Outlined.Error
        }

        GitState.Pending -> {
            Icons.Outlined.Pending
        }

        GitState.Success -> {
            Icons.Outlined.Done
        }

        GitState.Running -> {
            Icons.Outlined.Cyclone
        }
    }
}
