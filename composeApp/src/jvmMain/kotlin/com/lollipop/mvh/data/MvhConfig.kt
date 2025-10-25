package com.lollipop.mvh.data

import com.lollipop.mvh.git.GitStore
import java.io.File

object MvhConfig {

    private var sshHomePath = ""

    val homeDir by lazy {
        File(System.getProperty("user.home"), "ModuleVersionHelper")
    }

    fun optSshHome(): String {
        if (sshHomePath.isEmpty()) {
            sshHomePath = System.getProperty("user.home") + "/.ssh"
        }
        return sshHomePath
    }

    fun setSshHome(path: String) {
        sshHomePath = path
    }

}