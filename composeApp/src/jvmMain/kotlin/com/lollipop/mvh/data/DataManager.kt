package com.lollipop.mvh.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.Initialize
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import java.io.File

object DataManager {

    private const val DIR_WORKSPACE = "workspace"
    private val workspaceDir by lazy {
        File(MvhConfig.homeDir, DIR_WORKSPACE)
    }

    private fun fetchWorkspaceDir(): List<File> {
        return workspaceDir.listFiles()?.filter {
            it.isDirectory
        } ?: emptyList()
    }

}