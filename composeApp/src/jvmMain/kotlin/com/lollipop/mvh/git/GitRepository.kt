package com.lollipop.mvh.git

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ProjectInfo
import com.lollipop.mvh.git.GitLog.Error
import com.lollipop.mvh.git.GitLog.Surprise
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onUI
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.AnyObjectId
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

    private fun setState(state: GitState) {
        stateMutable.value = state
        when (state) {
            is GitState.Error -> {
                putLog(Error(state.logValue))
            }

            GitState.Pending -> {
                log("Pending")
            }

            GitState.Success -> {
                putLog(Surprise("Success"))
            }

            GitState.Running -> {
                log("Running")
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
            .setTransportConfigCallback(SshFactory.createConfigCallback())
            .setCallback(object : CloneCommand.Callback {
                override fun initializedSubmodules(submodules: Collection<String?>?) {
                    onUI {
                        log("Initialized submodules $submodules")
                    }
                }

                override fun cloningSubmodule(path: String?) {
                    onUI {
                        log("Cloning submodule $path")
                    }
                }

                override fun checkingOut(commit: AnyObjectId?, path: String?) {
                    onUI {
                        log("Checking out $commit : $path")
                    }
                }
            })
            .call()
        return git
    }

    private fun pullGit(dir: File): Git? {
        val git = Git.open(dir)
        pull(git)
        return git
    }

    private fun pull(git: Git) {
        git.pull()
            .setTransportConfigCallback(SshFactory.createConfigCallback())
            .call()
    }

    fun update() {
        if (state.value == GitState.Running) {
            return
        }
        setState(GitState.Running)
        doAsync {
            val git = gitInstance
            if (git != null) {
                pull(git)
            } else {
                gitInstance = updateGit()
            }
        }.onFinally { result ->
            when (result) {
                is FlowResult.Error<*> -> {
                    log(result.error.stackTraceToString())
                    setState(GitState.Error("更新失败"))
                }

                is FlowResult.Success<*> -> {
                    setState(GitState.Success)
                }
            }
        }
    }

}