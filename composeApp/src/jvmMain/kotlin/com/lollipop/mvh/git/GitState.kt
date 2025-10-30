package com.lollipop.mvh.git

import kotlin.math.min

sealed class GitState {

    object Pending : GitState()
    object Success : GitState()

    class Running(val current: Int, val total: Int) : GitState() {
        val progress: Float by lazy {
            if (total == 0) {
                0f
            } else {
                min(current.toFloat() / total.toFloat(), 1f)
            }
        }
    }

    data class Error(val message: String, val throwable: Throwable? = null) : GitState() {

        val logValue by lazy {
            val error = throwable?.stackTraceToString() ?: ""
            "${message}\n${error}"
        }

    }

}