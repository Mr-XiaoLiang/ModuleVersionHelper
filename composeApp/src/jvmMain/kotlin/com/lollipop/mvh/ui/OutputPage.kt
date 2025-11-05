package com.lollipop.mvh.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lollipop.mvh.data.LibsVersionToml
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.OutputPanel
import com.lollipop.mvh.widget.clickableIcon
import java.io.File

object OutputPageState {

    val outputValue = mutableStateOf("")

    val isLoading = mutableStateOf(false)

    val allCount = mutableStateOf(0)

    val successCount = mutableStateOf(0)

    private val whitelist = arrayOf(
        "buildSrc",
        "gradle",
        "build",
        ".gradle",
        ".idea",
        ".kotlin",
    )

    fun refresh() {
        val projectList = ArrayList(RepositoryPageState.repositoryList)
        allCount.value = projectList.size
        successCount.value = 0
        doAsync {
            for (project in projectList) {
                val projectDir = project.localDir
                val tomlInfo = findVersionLibs(projectDir)
                val listFiles = projectDir.listFiles()
                for (file in listFiles) {
                    if (file.isDirectory) {
                        val moduleName = file.name
                        if (whitelist.contains(moduleName)) {
                            continue
                        }
                        TODO("开始解析模块的内容吧")
                    }
                }
                TODO()
                successCount.value++
            }
        }
        // TODO: 获取输出结果
    }

    private fun findVersionLibs(projectDir: File): LibsVersionToml? {
        try {
            val gradleDir = File(projectDir, "gradle")
            if (!gradleDir.exists() || !gradleDir.isDirectory) {
                return null
            }
            val tomlFile = File(gradleDir, "libs.versions.toml")
            if (!tomlFile.exists() || !tomlFile.isFile) {
                return null
            }
            return LibsVersionToml.parseSync(tomlFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

}

@Composable
fun OutputPage() {
    ContentPage {
        val outputValue by remember { OutputPageState.outputValue }
        val isLoading by remember { OutputPageState.isLoading }
        val allCount by remember { OutputPageState.allCount }
        val successCount by remember { OutputPageState.successCount }
        OutputPanel(contentValue = outputValue) { iconColor ->
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(width = 40.dp, height = 40.dp).padding(horizontal = 8.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$successCount/$allCount"
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done",
                    modifier = Modifier.clickableIcon {},
                    tint = iconColor
                )
            }
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier.clickableIcon {
                    OutputPageState.refresh()
                },
                tint = iconColor
            )
        }
    }
}