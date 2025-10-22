package com.lollipop.mvh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.mvh.git.GitStore
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.ListItem


@Composable
fun RepositoryListPage() {
    val repositoryList = remember { GitStore.repository }
    ContentPage {
        LazyColumn {
            items(repositoryList, key = { it.remoteUrl }) { item ->
                ListItem {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier,
                            text = item.projectInfo.displayName
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = item.projectInfo.remote, fontSize = 16.sp)
                            Text(text = item.projectInfo.localName, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}