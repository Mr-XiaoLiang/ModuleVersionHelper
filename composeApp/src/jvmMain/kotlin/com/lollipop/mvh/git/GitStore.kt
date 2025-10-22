package com.lollipop.mvh.git

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.MvhConfig
import com.lollipop.mvh.data.ProjectInfo
import java.io.File

class GitStore(
    val file: File
) {

    companion object {
        private val currentMutable = mutableStateOf<GitStore?>(null)
        val current: State<GitStore?>
            get() {
                return currentMutable
            }
        val repository = SnapshotStateList<GitRepository>()

        fun changeCurrent(gitStore: GitStore?) {
            currentMutable.value = gitStore
            gitStore?.flush()
        }

        fun addRepository(info: ProjectInfo) {
            current.value?.let {
                it.addRepository(info)
                it.flush()
            }
        }
    }

    val name: String by lazy {
        file.name
    }

    private val repositoryList = ArrayList<GitRepository>()

    fun addRepository(info: ProjectInfo) {
        val remote = info.remote
        if (repositoryList.any { it.remoteUrl == remote }) {
            return
        }
        val localPath = File(MvhConfig.homeDir, info.localName)
        repositoryList.add(GitRepository(remote, localPath, info))
    }

    fun flush() {
        val selected = current.value
        if (selected === this) {
            repository.clear()
            repository.addAll(repositoryList)
        }
    }

}