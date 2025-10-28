package com.lollipop.mvh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.mvh.git.GitState
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.ListItem

@Composable
fun GitStatePage() {
    ContentPage {
        val repositoryList = remember { RepositoryPageState.repositoryList }
        var errorMessage by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
                .background(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
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
                    color = MaterialTheme.colors.onBackground
                )

                Icon(
                    modifier = Modifier.size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = {

                        })
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground
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
                    tint = MaterialTheme.colors.onBackground
                )

            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1F).padding(horizontal = 16.dp)
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
                                contentDescription = "",
                                modifier = Modifier.size(56.dp)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {
                                        repository.update()
                                    })
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                }
            }
//            Text(
//                text = errorMessage,
//                modifier = Modifier.fillMaxWidth().fillMaxSize()
//            )
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
    }
}
