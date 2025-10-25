package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage

object RepositoryPageState {

    val module by lazy {
        ChooserModule.optModule("repository") {
            it.onContentChangedCallback = ::onContentChanged
        }
    }

    val template by lazy {
        """
        [
            {
                "url":"Git仓库链接",
                "displayName":"展示名称，支持中文与符号"
            }
        ]
        """.trimIndent()
    }

    private fun onContentChanged(content: String) {
        // TODO
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