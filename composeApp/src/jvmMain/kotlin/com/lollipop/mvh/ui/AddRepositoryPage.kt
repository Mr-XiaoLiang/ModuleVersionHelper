package com.lollipop.mvh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lollipop.mvh.data.ProjectInfo
import com.lollipop.mvh.git.GitStore
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.InputField

@Composable
fun AddRepositoryPage() {

    var remoteUrl by rememberSaveable { mutableStateOf("") }
    var localName by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    val gitStore by remember { GitStore.current }

    ContentPage {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {

            InputField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                value = remoteUrl,
                onValueChange = { remoteUrl = it },
                label = { Text("远程仓库地址") }
            )
            InputField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                value = localName,
                onValueChange = { localName = it },
                label = { Text("本地仓库名称") }
            )
            InputField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("显示名称") }
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier,
                    enabled = remoteUrl.isNotEmpty() && localName.isNotEmpty() && displayName.isNotEmpty() && gitStore != null,
                    onClick = {
                        gitStore?.let {
                            it.addRepository(
                                ProjectInfo(
                                    remote = remoteUrl,
                                    localName = localName,
                                    displayName = displayName
                                )
                            )
                            it.flush()
                        }
                        remoteUrl = ""
                        localName = ""
                        displayName = ""
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                        Text(text = "添加")
                    }
                }
            }
        }
    }
}