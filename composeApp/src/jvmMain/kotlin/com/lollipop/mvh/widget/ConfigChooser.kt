package com.lollipop.mvh.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.tools.Clipboard
import com.lollipop.mvh.tools.FileChooserHelper
import java.io.File


@Composable
fun ConfigChooser(
    modifier: Modifier,
    contentColor: Color,
    module: ChooserModule,
    template: String = "",
    onFileChooser: (File) -> Unit,
) {
    val historyList = remember { module.historyList }
    val currentFile by remember { module.currentFile }
    var showDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentFile?.path ?: "",
            modifier = Modifier.weight(1F),
            color = contentColor
        )
        Spacer(modifier = Modifier.size(16.dp))
        Icon(
            imageVector = Icons.Filled.FolderOpen,
            contentDescription = "FolderOpen",
            modifier = Modifier.size(24.dp)
                .clickable {
                    FileChooserHelper.openFileChooser { files ->
                        if (files.isNotEmpty()) {
                            val file = File(files[0])
                            onFileChooser(file)
                            module.choose(file, true)
                        }
                    }
                },
            tint = contentColor
        )
        Spacer(modifier = Modifier.size(16.dp))
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = "History",
            modifier = Modifier.size(24.dp)
                .clickable {
                    showDialog = true
                },
            tint = contentColor
        )
        if (template.isNotEmpty()) {
            Spacer(modifier = Modifier.size(16.dp))
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info",
                modifier = Modifier.size(24.dp)
                    .clickable {
                        showTemplateDialog = true
                    },
                tint = contentColor
            )
        }
    }
    HistoryDialog(
        showDialog = showDialog,
        onDismiss = {
            showDialog = false
            module.save()
        },
        onRefresh = {
            module.fetchList()
        },
        historyList = historyList,
        onSelect = {
            onFileChooser(it)
            module.choose(it, false)
            showDialog = false
        }
    )
    TemplateDialog(
        showDialog = showTemplateDialog,
        onDismiss = {
            showTemplateDialog = false
        },
        content = template
    )
}

@Composable
fun HistoryDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    historyList: MutableList<File>,
    onSelect: (File) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8F)
                    .fillMaxHeight(0.8F),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "历史配置",
                            fontSize = 16.sp,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.weight(1F)
                        )
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(24.dp)
                                .clickable {
                                    onRefresh()
                                },
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                    LazyColumn {
                        items(historyList, key = { it.absolutePath }) { file ->
                            ListItem(
                                clickCallback = {
                                    onSelect(file)
                                }
                            ) {
                                Row {
                                    val exists = file.exists()
                                    if (exists) {
                                        Spacer(modifier = Modifier.size(24.dp))
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colors.error
                                        )
                                    }
                                    Text(
                                        text = file.name,
                                        modifier = Modifier.weight(1F).padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.size(16.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(24.dp)
                                            .clickable(onClick = {
                                                historyList.remove(file)
                                            }),
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    content: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            text = {
                SelectionContainer {
                    Text(text = content)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    Clipboard.copy(content)
                }) {
                    Text(text = "复制")
                }
            }
        )
    }
}

