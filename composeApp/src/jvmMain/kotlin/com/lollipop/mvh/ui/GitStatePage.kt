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
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.mvh.git.GitLog
import com.lollipop.mvh.git.GitRepository
import com.lollipop.mvh.git.GitState
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.ListItem
import java.awt.Desktop

object GitFetchState {

    val logList = SnapshotStateList<GitLog>()
    val selectedRepository = mutableStateOf<GitRepository?>(null)

    fun selectChange(repository: GitRepository?) {
        val current = selectedRepository.value
        if (current === repository) {
            repository?.bindLogOut(null)
            selectedRepository.value = null
            logList.clear()
        } else {
            selectedRepository.value?.bindLogOut(null)
            selectedRepository.value = repository
            repository?.bindLogOut(logList)
        }
    }

}

@Composable
fun GitFetchPage() {
    ContentPage {
        val repositoryList = remember { RepositoryPageState.repositoryList }
        val selectedRepository by remember { GitFetchState.selectedRepository }
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
                                GitStateIcon(gitState)
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
                                // 获取桌面实例并打开文件夹
//                                Desktop.getDesktop().open(folder);
                                Icon(
                                    imageVector = Icons.Filled.FolderOpen,
                                    contentDescription = "CloudDownload",
                                    modifier = Modifier.size(56.dp)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(onClick = {
                                            Desktop.getDesktop().open(repository.localDir)
                                        })
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
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
                                            GitFetchState.selectChange(repository)
                                        })
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
            if (selectedRepository != null) {
                val logList = remember { GitFetchState.logList }
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
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

@Composable
private fun GitStateIcon(gitState: GitState) {
    val iconModifier = Modifier.size(width = 56.dp, height = 40.dp).padding(horizontal = 16.dp, vertical = 8.dp)
    when (gitState) {
        is GitState.Error -> {
            Icon(
                imageVector = Icons.Outlined.Error,
                contentDescription = "",
                modifier = iconModifier,
                tint = MaterialTheme.colorScheme.error
            )
        }

        GitState.Pending -> {
            Icon(
                imageVector = Icons.Outlined.Pending,
                contentDescription = "",
                modifier = iconModifier,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        GitState.Success -> {
            Icon(
                imageVector = Icons.Outlined.Done,
                contentDescription = "",
                modifier = iconModifier,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        is GitState.Running -> {
            if (gitState.total < 0) {
                CircularProgressIndicator(
                    modifier = iconModifier,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                CircularProgressIndicator(
                    modifier = iconModifier,
                    progress = { gitState.progress },
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
