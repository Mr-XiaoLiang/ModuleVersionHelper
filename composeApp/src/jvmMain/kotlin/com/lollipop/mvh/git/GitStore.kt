package com.lollipop.mvh.git

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ProjectInfo
import java.io.File

object GitStore {

    private var sshHomePath = ""

    private val homeDir by lazy {
        File(System.getProperty("user.home"), "ModuleVersionHelper")
    }

    val repositoryList = SnapshotStateList<GitRepository>()

    fun optSshHome(): String {
        if (sshHomePath.isEmpty()) {
            sshHomePath = System.getProperty("user.home") + "/.ssh"
        }
        return sshHomePath
    }

    fun setSshHome(path: String) {
        sshHomePath = path
    }

    fun addRepository(info: ProjectInfo) {
        val remote = info.remote
        if (repositoryList.any { it.remoteUrl == remote }) {
            return
        }
        val localPath = File(homeDir, info.localName)
        repositoryList.add(GitRepository(remote, localPath, info))
    }

}