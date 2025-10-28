package com.lollipop.mvh.git

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ProjectInfo
import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory
import java.io.File

class GitRepository(
    val remoteUrl: String,
    val localDir: File,
    val projectInfo: ProjectInfo
) {

    private val stateMutable = mutableStateOf<GitState>(GitState.Pending)

    private val logListMutable = mutableStateListOf<GitLog>()

    val state: State<GitState>
        get() {
            return stateMutable
        }

    val logList: SnapshotStateList<GitLog>
        get() {
            return logListMutable
        }

    private var gitInstance: Git? = null

    private val log by lazy {
        LoggerFactory.getLogger("")
    }

    private fun setState(state: GitState) {
        stateMutable.value = state
        when (state) {
            is GitState.Error -> {
                putLog(GitLog.Error(state.logValue))
            }

            GitState.Pending -> {
                log("Pending")
            }

            GitState.Success -> {
                putLog(GitLog.Surprise("Success"))
            }
        }
    }

    private fun onError(msg: String, throwable: Throwable? = null) {
        setState(GitState.Error(msg, throwable))
    }

    private fun putLog(msg: GitLog) {
        logListMutable.add(0, msg)
    }

    private fun log(msg: String) {
        putLog(GitLog.Info(msg))
    }

    private fun updateGit(): Git? {
        if (!localDir.exists()) {
            localDir.mkdirs()
            return cloneGit(localDir)
        }
        val gitDir = File(localDir, ".git")
        if (!gitDir.exists()) {
            if (localDir.listFiles().isNotEmpty()) {
                onError("本地目录 ${localDir.path} 不为空，无法初始化 Git 仓库")
                return null
            }
            return cloneGit(localDir)
        }
        return pullGit(localDir)
    }

    private fun cloneGit(dir: File): Git? {
        val git = Git.cloneRepository()
            .setURI(remoteUrl)
            .setDirectory(dir)
//            .setBranch(remoteBranchName)
            .setTransportConfigCallback(SshFactory.createConfigCallback())
//            .setCallback()
            .call()
        return git
    }

    private fun pullGit(dir: File): Git? {
        val git = Git.open(dir)
        git.pull().call()
        return git
    }

    fun update() {
        try {
            val git = gitInstance
            if (git != null) {
                git.pull().call()
            } else {
                gitInstance = updateGit()
            }
        } catch (e: Throwable) {
            onError("Update.ERROR", e)
        }
    }

}