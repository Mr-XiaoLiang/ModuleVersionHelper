package com.lollipop.mvh

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.mvh.data.LibsVersionToml
import java.io.File

fun main() = application {
    Initialize.init()
    LibsVersionToml.TomlReader.read(file = File("C:\\Users\\lolli\\ModuleVersionHelper\\workspace\\ModuleVersionHelper\\gradle\\libs.versions.toml"))
    Window(
        onCloseRequest = ::exitApplication,
        title = "模块版本助手",
    ) {
        App()
    }
}