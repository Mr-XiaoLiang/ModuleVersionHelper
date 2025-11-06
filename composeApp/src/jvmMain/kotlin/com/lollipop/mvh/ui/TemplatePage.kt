package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.data.ProjectInfo
import com.lollipop.mvh.data.TemplateInfo
import com.lollipop.mvh.data.VersionParser
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onUI
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage
import org.json.JSONObject

object TemplatePageState {
    val module by lazy {
        ChooserModule.optModule("template") {
            it.onContentChangedCallback = ::onContentChanged
        }
    }

    val outputTemplate = mutableStateOf("")

    val moduleList = SnapshotStateList<TemplateInfo>()

    const val PROJECT_NAME = "%PROJECT_NAME%"
    const val MODULE_NAME = "%MODULE_NAME%"
    const val LIBRARY_NAME = "%LIBRARY_NAME%"
    const val GROUP_ID = "%GROUP_ID%"
    const val ARTIFACT_ID = "%ARTIFACT_ID%"
    const val VERSION_NAME = "%VERSION_NAME%"

    val template by lazy {
        """
        {
            "output": "|${PROJECT_NAME}|${MODULE_NAME}|${LIBRARY_NAME}|${GROUP_ID}|${ARTIFACT_ID}|${VERSION_NAME}|",
            "modules": [
                {
                    "groupId": "Maven坐标前缀",
                    "artifactId": "项目名称",
                    "displayName": "展示名称，支持中文与符号",
                    "alias": [
                        {
                            "groupId": "Maven坐标前缀别名",
                            "artifactId": "项目名称别名"
                        }
                    ]
                }
            ]
        }
        """.trimIndent()
    }

    private var hasProjectName = false
    private var hasModuleName = false
    private var hasLibraryName = false
    private var hasGroupId = false
    private var hasArtifactId = false
    private var hasVersionName = false

    private fun onContentChanged(content: String) {
        doAsync {
            val json = JSONObject(content)
            val templateLine = json.optString("output") ?: ""
            outputTemplate.value = templateLine
            checkTemplateState(templateLine)
            val modules = json.optJSONArray("modules")
            val moduleList = mutableListOf<TemplateInfo>()
            for (i in 0 until modules.length()) {
                val module = modules.optJSONObject(i) ?: continue
                val groupId = module.optString("groupId") ?: ""
                val artifactId = module.optString("artifactId") ?: ""
                val displayName = module.optString("displayName") ?: ""
                if (groupId.isEmpty() || artifactId.isEmpty()) {
                    continue
                }
                val info = TemplateInfo(
                    groupId = groupId,
                    artifactId = artifactId,
                    displayName = displayName.ifEmpty { artifactId }
                )
                val alias = module.optJSONArray("alias")
                if (alias != null) {
                    for (j in 0 until alias.length()) {
                        val aliasObj = alias.optJSONObject(j) ?: continue
                        val aliasGroupId = aliasObj.optString("groupId")
                        val aliasArtifactId = aliasObj.optString("artifactId")
                        if (aliasGroupId.isNotEmpty() && aliasArtifactId.isNotEmpty()) {
                            info.addAlias(aliasGroupId, aliasArtifactId)
                        }
                    }
                }
                moduleList.add(info)
            }
            this.moduleList.clear()
            this.moduleList.addAll(moduleList)
        }.onFinally {
            onUI {
                OutputPageState.notifyChanged()
            }
        }
    }

    private fun checkTemplateState(value: String) {
        hasProjectName = value.contains(PROJECT_NAME)
        hasModuleName = value.contains(MODULE_NAME)
        hasLibraryName = value.contains(LIBRARY_NAME)
        hasGroupId = value.contains(GROUP_ID)
        hasArtifactId = value.contains(ARTIFACT_ID)
        hasVersionName = value.contains(VERSION_NAME)
    }

    fun getLine(projectInfo: ProjectInfo, moduleName: String, versionInfo: VersionParser.VersionInfo): String {
        val templateInfo = checked(versionInfo) ?: return ""
        var output = outputTemplate.value
        if (hasProjectName) {
            output = output.replace(PROJECT_NAME, projectInfo.displayName)
        }
        if (hasModuleName) {
            output = output.replace(MODULE_NAME, moduleName)
        }
        if (hasLibraryName) {
            output = output.replace(LIBRARY_NAME, templateInfo.displayName)
        }
        if (hasGroupId) {
            output = output.replace(GROUP_ID, templateInfo.groupId)
        }
        if (hasArtifactId) {
            output = output.replace(ARTIFACT_ID, templateInfo.artifactId)
        }
        if (hasVersionName) {
            output = output.replace(VERSION_NAME, versionInfo.version)
        }
        return output
    }

    private fun checked(versionInfo: VersionParser.VersionInfo): TemplateInfo? {
        val moduleName = versionInfo.module
        return moduleList.find {
            it.module == moduleName || it.aliasContains(moduleName)
        }
    }

}

@Composable
fun TemplatePage() {
    ContentPage {
        ConfigPanel(
            title = "",
            module = TemplatePageState.module,
            template = TemplatePageState.template
        )
    }
}