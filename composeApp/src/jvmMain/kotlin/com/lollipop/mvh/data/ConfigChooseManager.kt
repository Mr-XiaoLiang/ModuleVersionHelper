package com.lollipop.mvh.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.tools.FileHelper
import com.lollipop.mvh.tools.FlowResult
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onSync
import org.json.JSONArray
import java.io.File

object ConfigChooseManager {

    private const val DIR_PROPERTIES = "history"
    private const val SUFFIX_PROPERTIES = ".properties"

    private val propertiesDir by lazy {
        File(MvhConfig.homeDir, DIR_PROPERTIES)
    }

    private val moduleMap = mutableMapOf<String, ChooserModule>()

    fun optModule(name: String): ChooserModule {
        val module = moduleMap[name]
        if (module != null) {
            return module
        }
        val newModule = ChooserModule(name)
        newModule.fetchList()
        moduleMap[name] = newModule
        return newModule
    }

    class ChooserModule(
        val name: String
    ) {

        val currentFile = mutableStateOf<File?>(null)

        val historyList = SnapshotStateList<File>()

        val currentContent = mutableStateOf("")

        private val propertiesFile by lazy {
            File(propertiesDir, name + SUFFIX_PROPERTIES)
        }

        fun choose(file: File, save: Boolean) {
            currentFile.value = file
            doAsync {
                FileHelper.readFile(file)
            }.onFinally {
                onSync {
                    currentContent.value = it.getOrNull() ?: ""
                }
            }
            historyList.forEach {
                if (it.absolutePath == file.absolutePath) {
                    return
                }
            }
            historyList.add(file)
            if (save) {
                save()
            }
        }

        fun fetchList() {
            val file = propertiesFile
            if (!file.exists()) {
                historyList.clear()
                return
            }
            doAsync {
                val jsonArray = FileHelper.readJsonArray(file)
                val length = jsonArray.length()
                val outList = ArrayList<File>()
                for (i in 0 until length) {
                    val path = jsonArray.optString(i)
                    val file = File(path)
                    outList.add(file)
                }
                outList
            }.onFinally {
                onSync {
                    historyList.clear()
                    if (it is FlowResult.Success) {
                        historyList.addAll(it.data)
                    }
                }
            }
        }

        fun save() {
            doAsync {
                val file = propertiesFile
                file.parentFile?.mkdirs()
                val jsonArray = JSONArray()
                for (file in historyList) {
                    jsonArray.put(file.path)
                }
                FileHelper.writeJson(file, jsonArray)
            }
        }

    }

}