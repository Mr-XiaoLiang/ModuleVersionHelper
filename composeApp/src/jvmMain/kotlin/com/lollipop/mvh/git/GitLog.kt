package com.lollipop.mvh.git

sealed class GitLog {

    class Info(val msg: String) : GitLog()
    class Surprise(val msg: String) : GitLog()
    class Error(val msg: String) : GitLog()

}