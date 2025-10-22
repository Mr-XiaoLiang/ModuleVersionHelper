package com.lollipop.mvh.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PropertiesHolder(
    val file: File
) {

    companion object {
        private const val KEY_REPOSITORY = "repository"
        private const val KEY_MODULE = "module"

        private const val KEY_LOCAL_PATH = "localPath"
        private const val KEY_REMOTE_PATH = "remotePath"
        private const val KEY_DISPLAY_NAME = "displayName"

        private const val KEY_MODULE_NAME = "moduleName"
        private const val KEY_MODULE_ALIAS = "moduleAlias"
    }

    private val repositoryList = SnapshotStateList<ProjectInfo>()
    private val moduleList = SnapshotStateList<ModuleRules>()

    val name by lazy {
        file.name
    }

    fun read() {
        try {
            val rList = mutableListOf<ProjectInfo>()
            val mList = mutableListOf<ModuleRules>()
            val content = readContent()
            val repositoryJson = content.optJSONArray(KEY_REPOSITORY)
            if (repositoryJson != null) {
                val length = repositoryJson.length()
                for (i in 0 until length) {
                    val item = repositoryJson.optJSONObject(i)
                    val localPath = item.optString(KEY_LOCAL_PATH)
                    val remotePath = item.optString(KEY_REMOTE_PATH)
                    val displayName = item.optString(KEY_DISPLAY_NAME)
                    if (localPath.isNotEmpty() && remotePath.isNotEmpty() && displayName.isNotEmpty()) {
                        rList.add(ProjectInfo(remotePath, localPath, displayName))
                    }
                }
            }
            val moduleJson = content.optJSONArray(KEY_MODULE)
            if (moduleJson != null) {
                val length = moduleJson.length()
                for (i in 0 until length) {
                    val item = moduleJson.optJSONObject(i)
                    val moduleName = item.optString(KEY_MODULE_NAME)
                    if (moduleName.isNotEmpty()) {
                        val moduleRules = ModuleRules(moduleName)
                        mList.add(moduleRules)
                        val moduleAlias = item.optJSONArray(KEY_MODULE_ALIAS)
                        if (moduleAlias != null) {
                            val aliasCount = moduleAlias.length()
                            for (j in 0 until aliasCount) {
                                val alias = moduleAlias.optString(j)
                                if (alias.isNotEmpty()) {
                                    moduleRules.alias.add(alias)
                                }
                            }
                        }
                    }
                }
            }
            repositoryList.clear()
            repositoryList.addAll(rList)
            moduleList.clear()
            moduleList.addAll(mList)
        } catch (e: Throwable) {
            e.printStackTrace()
            repositoryList.clear()
            moduleList.clear()
        }
    }

    fun write() {
        try {
            val rootObj = JSONObject()
            val repositoryArray = JSONArray()
            val moduleArray = JSONArray()
            for (item in repositoryList) {
                val itemObj = JSONObject()
                itemObj.put(KEY_LOCAL_PATH, item.localName)
                itemObj.put(KEY_REMOTE_PATH, item.remote)
                itemObj.put(KEY_DISPLAY_NAME, item.displayName)
                repositoryArray.put(itemObj)
            }
            for (item in moduleList) {
                val itemObj = JSONObject()
                itemObj.put(KEY_MODULE_NAME, item.name)
                val aliasArray = JSONArray()
                for (alias in item.alias) {
                    aliasArray.put(alias)
                }
                itemObj.put(KEY_MODULE_ALIAS, aliasArray)
                moduleArray.put(itemObj)
            }
            rootObj.put(KEY_REPOSITORY, repositoryArray)
            rootObj.put(KEY_MODULE, moduleArray)
            file.writeText(rootObj.toString())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun readContent(): JSONObject {
        try {
            return JSONObject(file.readText())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return JSONObject()
    }

}