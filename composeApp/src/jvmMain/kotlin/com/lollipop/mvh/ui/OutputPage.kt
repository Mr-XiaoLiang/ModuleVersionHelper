package com.lollipop.mvh.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lollipop.mvh.data.LibsVersionToml
import com.lollipop.mvh.data.VersionParser
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onUI
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.OutputPanel
import com.lollipop.mvh.widget.clickableIcon
import java.io.File

object OutputPageState {

    val isChanged = mutableStateOf(false)

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

    fun notifyChanged() {
        isChanged.value = true
    }

    fun refresh() {
        val projectList = ArrayList(RepositoryPageState.repositoryList)
        allCount.value = projectList.size
        successCount.value = 0
        isLoading.value = true
        doAsync {
            val builder = StringBuilder()
            builder.append(SamplePageState.getHeader())
            for (project in projectList) {
                val projectDir = project.localDir
                val tomlInfo = findVersionLibs(projectDir) ?: LibsVersionToml.EMPTY
                val listFiles = projectDir.listFiles()
                val versionList = mutableListOf<VersionParser.VersionInfo>()
                for (file in listFiles) {
                    if (file.isDirectory) {
                        val moduleName = file.name
                        if (whitelist.contains(moduleName)) {
                            continue
                        }
                        val buildGradleFile = File(file, "build.gradle")
                        if (buildGradleFile.exists() && buildGradleFile.isFile) {
                            versionList.addAll(VersionParser.readBuildGradle(file = file, libs = tomlInfo))
                        } else {
                            val buildGradleKtsFile = File(file, "build.gradle.kts")
                            if (buildGradleKtsFile.exists() && buildGradleKtsFile.isFile) {
                                versionList.addAll(VersionParser.readBuildGradle(file = file, libs = tomlInfo))
                            }
                        }
                        versionList.forEach { version ->
                            builder.append("\n")
                            val line = TemplatePageState.getLine(
                                projectInfo = project.projectInfo,
                                moduleName = moduleName,
                                versionInfo = version
                            )
                            builder.append(line)
                        }
                    }
                }
                successCount.value++
            }
            builder.append(SamplePageState.getFooter())
            builder.toString()
        }.onFinally { result ->
            when (result) {
                is FlowResult.Success -> {
                    outputValue.value = result.data
                }

                is FlowResult.Error -> {
                    outputValue.value = "Error: ${result.error}"
                }
            }
            onUI {
                isLoading.value = false
                isChanged.value = false
            }
        }
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
        val isChanged by remember { OutputPageState.isChanged }
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
            } else if (isChanged) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.clickableIcon {},
                    tint = iconColor
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