package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.data.MvhConfig
import com.lollipop.mvh.data.ProjectInfo
import com.lollipop.mvh.git.GitRepository
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onUI
import com.lollipop.mvh.widget.ConfigPanel
import com.lollipop.mvh.widget.ContentPage
import org.json.JSONArray

object RepositoryPageState {

    val module by lazy {
        ChooserModule.optModule("repository") {
            it.onContentChangedCallback = ::onContentChanged
        }
    }

    val repositoryList = SnapshotStateList<GitRepository>()

    val template by lazy {
        """
        [
            {
                "url": "Git仓库链接",
                "displayName": "展示名称，支持中文与符号",
                "sshKeyPath": "SSH私钥的完整路径，与邮箱密码二选一",
                "sshHostPath": "SSH的Known Hosts文件路径，默认为~/.ssh/known_hosts，与邮箱密码二选一",
                "email": "邮箱地址，与SSH私钥二选一",
                "password": "邮箱密码，与SSH私钥二选一"
            }
        ]
        """.trimIndent()
    }

    private fun onContentChanged(content: String) {
        doAsync {
            val jsonArray = JSONArray(content)
            val resultList = ArrayList<GitRepository>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.optJSONObject(i) ?: continue
                val url = jsonObject.optString("url")
                val displayName = jsonObject.optString("displayName")
                val localName = getGitName(url)
                val sshHostPath = jsonObject.optString("sshHostPath")
                val sshKeyPath = jsonObject.optString("sshKeyPath")
                val email = jsonObject.optString("email")
                val password = jsonObject.optString("password")
                val info = ProjectInfo(
                    remote = url,
                    localName = localName,
                    displayName = displayName,
                    sshHostPath = sshHostPath,
                    sshKeyPath = sshKeyPath,
                    userEmail = email,
                    userPwd = password
                )
                val repository = GitRepository(
                    remoteUrl = url,
                    localDir = MvhConfig.getGitRepository(localName),
                    projectInfo = info
                )
                resultList.add(repository)
            }
            resultList
        }.onFinally { result ->
            onUI {
                OutputPageState.notifyChanged()
                repositoryList.clear()
                result.getOrNull()?.let {
                    repositoryList.addAll(it)
                }
            }
        }
    }

    private fun getGitName(url: String): String {
        return url.substringAfterLast("/").substringBeforeLast(".")
    }

}

@Composable
fun RepositoryPage() {
    ContentPage {
        ConfigPanel(
            title = "",
            module = RepositoryPageState.module,
            template = RepositoryPageState.template
        )
    }
}