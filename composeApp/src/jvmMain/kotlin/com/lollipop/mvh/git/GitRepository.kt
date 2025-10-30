package com.lollipop.mvh.git

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.lollipop.mvh.data.ProjectInfo
import com.lollipop.mvh.git.GitLog.Error
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File

class GitRepository(
    val remoteUrl: String,
    val localDir: File,
    val projectInfo: ProjectInfo
) {

    private val stateMutable = mutableStateOf<GitState>(GitState.Pending)

    private val logListMutable = ArrayList<GitLog>()
    private var outList: MutableList<GitLog>? = null

    private val progressCallback by lazy {
        ProgressCallback(stateCallback = ::setState, logCallback = ::putLog)
    }

    val state: State<GitState>
        get() {
            return stateMutable
        }

    private var gitInstance: Git? = null

    fun bindLogOut(outList: MutableList<GitLog>?) {
        outList?.clear()
        outList?.addAll(logListMutable)
        this.outList = outList
    }

    private fun setState(state: GitState) {
        stateMutable.value = state
        when (state) {
            is GitState.Error -> {
                putLog(Error(state.logValue))
            }

            GitState.Pending -> {
                log("--> Pending")
            }

            GitState.Success -> {
                log("--> Complete")
            }

            is GitState.Running -> {
            }
        }
    }

    private fun onError(msg: String, throwable: Throwable? = null) {
        setState(GitState.Error(msg, throwable))
    }

    private fun putLog(msg: GitLog) {
        logListMutable.add(0, msg)
        outList?.add(0, msg)
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
            .bindAuth(projectInfo)
            .setProgressMonitor(progressCallback)
            .call()
        return git
    }

    private fun pullGit(dir: File): Git? {
        val git = Git.open(dir)
        pull(git)
        return git
    }

    private fun pull(git: Git) {
        val result = git.pull()
            .bindAuth(projectInfo)
            .setProgressMonitor(progressCallback)
            .call()
        if (result.isSuccessful) {
            log("Pull Success")
        } else {
            log("Pull Failed")
            result.mergeResult.conflicts.forEach { conflict ->
                log("冲突文件：${conflict.key} : ${conflict.value}")
            }
        }
    }

    fun update() {
        if (state.value is GitState.Running) {
            return
        }
        setState(GitState.Running(0, -1))
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

    private class ProgressCallback(
        val stateCallback: (GitState) -> Unit,
        val logCallback: (GitLog) -> Unit
    ) : ProgressMonitor {

        private var totalTasksCount = 0
        private var currentTaskIndex = 0
        private var currentTaskWork = 0

        private var lastUpdateTime = 0L

        private val updateInterval = 1000L

        override fun start(totalTasks: Int) {
            stateCallback(GitState.Running(current = 0, total = totalTasks))
            totalTasksCount = totalTasks
            currentTaskIndex = 0
            logCallback(GitLog.Info("任务开始, 总计 $totalTasksCount ------------------------"))
        }

        override fun beginTask(title: String?, totalWork: Int) {
            currentTaskWork = totalWork
            logCallback(GitLog.Info("开始更新(${currentTaskIndex}/${totalTasksCount})，$totalWork : $title"))
        }

        override fun update(completed: Int) {
            currentTaskIndex = completed
            val now = System.currentTimeMillis()
            if (now - lastUpdateTime > updateInterval) {
                lastUpdateTime = now
                logCallback(GitLog.Info("更新进度(${completed}/${totalTasksCount})"))
            }
        }

        override fun endTask() {
            currentTaskIndex++
            logCallback(GitLog.Info("更新完成(${currentTaskIndex}/${totalTasksCount})"))
            stateCallback(GitState.Running(current = currentTaskIndex, total = totalTasksCount))
        }

        override fun isCancelled(): Boolean {
            return false
        }

        override fun showDuration(enabled: Boolean) {
        }

    }

}