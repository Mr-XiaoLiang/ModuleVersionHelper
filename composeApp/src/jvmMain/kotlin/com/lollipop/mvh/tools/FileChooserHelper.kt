package com.lollipop.mvh.tools

import java.io.File
import javax.swing.JFileChooser

object FileChooserHelper {

    private var lastOpenDir: File? = null

    private val userHomeDir by lazy {
        File(System.getProperty("user.home"))
    }

    fun openFileChooser(title: String? = null, mode: Mode = Mode.FILE_ONLY, callback: (List<String>) -> Unit) {
        val chooser = JFileChooser()
        chooser.currentDirectory = lastOpenDir ?: userHomeDir
        chooser.setDialogTitle(title ?: mode.title)
        chooser.setFileSelectionMode(mode.code)

        // 禁用“所有文件”选项
        chooser.setAcceptAllFileFilterUsed(false)

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val files = chooser.selectedFiles?.map { it.path }
            if (files != null && files.isNotEmpty()) {
                callback(files)
                return
            }
            val file = chooser.selectedFile
            if (file != null && file.exists()) {
                callback(listOf(file.path))
            }
        }
    }

    enum class Mode(val code: Int, val title: String) {
        FILE_ONLY(code = JFileChooser.FILES_ONLY, title = "File"),
        DIRECTORY_ONLY(code = JFileChooser.DIRECTORIES_ONLY, title = "Directory"),
        FILE_AND_DIRECTORY(code = JFileChooser.FILES_AND_DIRECTORIES, title = "File or Directory")
    }

}