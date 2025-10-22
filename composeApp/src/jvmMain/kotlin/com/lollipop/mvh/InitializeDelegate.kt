package com.lollipop.mvh

import com.lollipop.mvh.data.DataManager
import com.lollipop.mvh.tools.doAsync
import com.lollipop.mvh.tools.onSync

object Initialize {

    private var isInit = false
    private val taskList = mutableListOf<Task>(
        DataManager.InitTask
    )

    fun init() {
        if (isInit) {
            return
        }
        isInit = true
        taskList.forEach {
            onSync {
                it.init()
            }
            doAsync {
                it.asyncInit()
            }
        }
        taskList.clear()
    }

    interface Task {
        fun init() {}
        fun asyncInit() {}
    }

}