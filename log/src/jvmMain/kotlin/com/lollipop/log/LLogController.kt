package com.lollipop.log

object LLogController {

    var printer: Printer = EmptyPrinter

    @JvmStatic
    val isEnable: Boolean
        get() {
            return printer != EmptyPrinter
        }

    interface Printer {

        fun print(level: LogLevel, msg: String)

    }

    private object EmptyPrinter : Printer {
        override fun print(level: LogLevel, msg: String) {
        }
    }

}