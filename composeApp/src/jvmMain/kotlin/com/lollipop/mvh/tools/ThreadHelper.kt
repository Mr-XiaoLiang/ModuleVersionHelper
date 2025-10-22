package com.lollipop.mvh.tools

import java.util.concurrent.Executors

object ThreadHelper {

    private val asyncExecutor by lazy {
        Executors.newCachedThreadPool()
    }
    private val syncExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun postAsync(
        error: (Throwable) -> Unit,
        block: () -> Unit
    ) {
        postAsync(SafeRunnable(error, block))
    }

    fun postSync(
        error: (Throwable) -> Unit,
        block: () -> Unit
    ) {
        postSync(SafeRunnable(error, block))
    }

    fun postAsync(
        runnable: Runnable
    ) {
        asyncExecutor.execute(runnable)
    }

    fun postSync(
        runnable: Runnable
    ) {
        syncExecutor.execute(runnable)
    }

    class SafeRunnable(
        private val error: (Throwable) -> Unit,
        private val block: () -> Unit
    ) : Runnable {
        override fun run() {
            try {
                block()
            } catch (e: Throwable) {
                error(e)
            }
        }
    }

}


class Flow<T>(
    val block: () -> T
) : Runnable {
    private var errorCallback: FlowError<T> = DefaultFlowError()
    private var finallyCallback: ((FlowResult<T>) -> Unit) = { }

    fun onError(callback: FlowError<T>): Flow<T> {
        errorCallback = callback
        return this
    }

    fun onFinally(callback: (FlowResult<T>) -> Unit): Flow<T> {
        finallyCallback = callback
        return this
    }

    override fun run() {
        try {
            val result = block()
            finallyCallback(FlowResult.Success(result))
        } catch (e: Throwable) {
            finallyCallback(errorCallback.onError(e))
        }
    }

}

sealed class FlowResult<T> {

    class Success<T>(val data: T) : FlowResult<T>()

    class Error<T>(val error: Throwable) : FlowResult<T>()

}

fun interface FlowError<T> {
    fun onError(error: Throwable): FlowResult<T>
}

private class DefaultFlowError<T> : FlowError<T> {
    override fun onError(error: Throwable): FlowResult<T> {
        return FlowResult.Error(error)
    }
}


fun <T> doAsync(
    block: () -> T
): Flow<T> {
    val flow = Flow(block)
    ThreadHelper.postAsync(flow)
    return flow
}

fun <T> onSync(
    block: () -> T
): Flow<T> {
    val flow = Flow(block)
    ThreadHelper.postSync(flow)
    return flow
}