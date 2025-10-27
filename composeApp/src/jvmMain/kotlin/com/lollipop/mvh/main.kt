package com.lollipop.mvh

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Initialize.init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "模块版本助手",
    ) {
        App()
    }
}