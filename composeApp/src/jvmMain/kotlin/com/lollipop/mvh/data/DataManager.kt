package com.lollipop.mvh.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.Initialize
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import java.io.File

object DataManager {

    private const val DIR_WORKSPACE = "workspace"
    private const val DIR_PROPERTIES = "properties"

    private const val SUFFIX_PROPERTIES = ".properties"

    const val DEFAULT_PROPERTIES = "default${SUFFIX_PROPERTIES}"

    private val workspaceDir by lazy {
        File(MvhConfig.homeDir, DIR_WORKSPACE)
    }

    private val propertiesDir by lazy {
        File(MvhConfig.homeDir, DIR_PROPERTIES)
    }

    val propertiesList = SnapshotStateList<PropertiesHolder>()

    fun updatePropertiesList() {
        doAsync<List<PropertiesHolder>> {
            val pList = mutableListOf<PropertiesHolder>()
            for (file in fetchPropertiesFile()) {
                val holder = PropertiesHolder(file)
                pList.add(holder)
            }
            pList
        }.onFinally {
            propertiesList.clear()
            val list: List<PropertiesHolder> = when (it) {
                is FlowResult.Success -> {
                    val data = it.data
                    data.ifEmpty {
                        listOf(PropertiesHolder(File(propertiesDir, DEFAULT_PROPERTIES)))
                    }
                }

                else -> {
                    listOf(PropertiesHolder(File(propertiesDir, DEFAULT_PROPERTIES)))
                }
            }
            propertiesList.addAll(list)
        }
    }

    private fun fetchPropertiesFile(): List<File> {
        return propertiesDir.listFiles()?.filter {
            it.isFile && it.name.endsWith(SUFFIX_PROPERTIES)
        } ?: emptyList()
    }

    private fun fetchWorkspaceDir(): List<File> {
        return workspaceDir.listFiles()?.filter {
            it.isDirectory
        } ?: emptyList()
    }

    object InitTask : Initialize.Task {
        override fun init() {
            updatePropertiesList()
        }
    }

}