package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import com.lollipop.mvh.data.ConfigChooseManager
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage

object RepositoryPageState {

    val module by lazy {
        ConfigChooseManager.optModule("repository")
    }

    val template by lazy {
        """
        [
            {
                "url":"Git仓库链接",
                "dirName":"文件夹名称(仅英文字母，不要包含空格)",
                "displayName":"展示名称，支持中文与符号"
            }
        ]
        """.trimIndent()
    }

}

@Composable
fun RepositoryPage() {
    ContentPage {
        ConfigPanel(
            title = "",
            module = RepositoryPageState.module,
            template = RepositoryPageState.template
        )
    }
}