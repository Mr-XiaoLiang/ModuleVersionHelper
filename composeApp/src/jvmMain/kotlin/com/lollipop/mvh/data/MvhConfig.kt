package com.lollipop.mvh.data

import java.io.File

object MvhConfig {

    private var sshHomePath = ""

    private const val DIR_WORKSPACE = "workspace"

    val homeDir by lazy {
        File(System.getProperty("user.home"), "ModuleVersionHelper")
    }

    val workspaceDir by lazy {
        File(homeDir, DIR_WORKSPACE)
    }

    fun getGitRepository(name: String): File {
        return File(workspaceDir, name)
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