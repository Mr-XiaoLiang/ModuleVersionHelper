package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage

object SamplePageState {

    val headerModule by lazy {
        ChooserModule.optModule("sample_header") {
            it.onContentChangedCallback = ::onHeaderChanged
        }
    }
    val footerModule by lazy {
        ChooserModule.optModule("sample_footer") {
            it.onContentChangedCallback = ::onFooterChanged
        }
    }

    private fun onHeaderChanged(content: String) {
        // TODO
    }

    private fun onFooterChanged(content: String) {
        // TODO
    }

}

@Composable
fun SamplePage() {
    ContentPage {
        ConfigPanel(
            firstTitle = "头部",
            secondTitle = "脚部",
            firstModule = SamplePageState.headerModule,
            secondModule = SamplePageState.footerModule,
            firstTemplate = "",
            secondTemplate = ""
        )
    }
}