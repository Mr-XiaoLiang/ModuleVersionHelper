package com.lollipop.mvh.git

sealed class GitState {

    object Pending : GitState()
    object Success : GitState()
    data class Error(val message: String, val throwable: Throwable? = null) : GitState() {

        val logValue by lazy {
            val error = throwable?.stackTraceToString() ?: ""
            "${message}\n${error}"
        }

    }

}