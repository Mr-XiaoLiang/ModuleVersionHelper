package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.data.TemplateInfo
import com.lollipop.mvh.tools.doAsync
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

    val template by lazy {
        """
        {
            "output": "|%PROJECT_NAME%|%MODULE_NAME%|%LIBRARY_NAME%|%GROUP_ID%|%ARTIFACT_ID%|%VERSION_NAME%|",
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

    private fun onContentChanged(content: String) {
        doAsync {
            val json = JSONObject(content)
            outputTemplate.value = json.optString("output") ?: ""
            val modules = json.optJSONArray("modules")
            // TODO 解析
        }
    }

    fun getLine(): String {
        TODO()
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