package com.lollipop.log

import java.io.PrintWriter
import java.io.StringWriter

class LLog(private val tag: String) {

    companion object {

        @JvmOverloads
        fun with(obj: Any? = null, tag: String = ""): LLog {
            if (obj != null) {
                val name = obj.javaClass.simpleName
                val id = System.identityHashCode(obj)
                return if (tag.isEmpty()) {
                    LLog("${name}@${id.toString(16).uppercase()}")
                } else {
                    LLog("${name}@${id.toString(16).uppercase()}#$tag")
                }
            } else {
                return if (tag.isEmpty()) {
                    LLog("")
                } else {
                    LLog(tag)
                }
            }
        }

        val isEnable: Boolean
            get() {
                return LLogController.isEnable
            }

        private fun log(tag: String, level: LogLevel, msg: String) {
            try {
                if (isEnable) {
                    LLogController.printer.print(level, "LLog: $tag -> $msg")
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }

    fun v(msg: String) {
        tryDo {
            log(tag, LogLevel.VERBOSE, msg)
        }
    }

    fun d(msg: String) {
        tryDo {
            log(tag, LogLevel.DEBUG, msg)
        }
    }

    fun i(msg: String) {
        tryDo {
            log(tag, LogLevel.INFO, msg)
        }
    }

    fun w(msg: String) {
        tryDo {
            log(tag, LogLevel.WARN, msg)
        }
    }

    fun e(msg: String) {
        tryDo {
            log(tag, LogLevel.ERROR, msg)
        }
    }

    fun e(msg: String, e: Throwable) {
        tryDo {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            pw.println(msg)
            e.printStackTrace(pw)
            pw.flush()
            e(sw.toString())
        }
    }

    fun wtf(msg: String) {
        tryDo {
            log(tag, LogLevel.WTF, msg)
        }
    }

    fun v(block: () -> String) {
        tryDo {
            v(block())
        }
    }

    fun d(block: () -> String) {
        tryDo {
            d(block())
        }
    }

    fun i(block: () -> String) {
        tryDo {
            i(block())
        }
    }

    fun w(block: () -> String) {
        tryDo {
            w(block())
        }
    }

    fun e(block: () -> String) {
        tryDo {
            e(block())
        }
    }

    fun e(block: () -> String, error: Throwable) {
        tryDo {
            e(block(), error)
        }
    }

    fun wtf(block: () -> String) {
        tryDo {
            wtf(block())
        }
    }

    private inline fun tryDo(block: () -> Unit) {
        if (!isEnable) {
            return
        }
        try {
            block()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}

inline fun <reified T : Any> T.withThis(tag: String = ""): LLog {
    return LLog.with(this, tag)
}
