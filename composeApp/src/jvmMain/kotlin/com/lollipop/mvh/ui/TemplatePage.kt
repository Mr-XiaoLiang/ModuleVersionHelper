package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import com.lollipop.mvh.data.ConfigChooseManager
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage

object TemplatePageState {
    val module by lazy {
        ConfigChooseManager.optModule("template")
    }

    val template by lazy {
        """
            [
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
        """.trimIndent()
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