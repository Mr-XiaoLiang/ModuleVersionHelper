package com.lollipop.mvh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            modifier = Modifier.fillMaxSize()
        ) {
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
                                modifier = Modifier.fillMaxWidth(),
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
                        }
                    }
                }
            }
            Text(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth().fillMaxSize()
            )
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
