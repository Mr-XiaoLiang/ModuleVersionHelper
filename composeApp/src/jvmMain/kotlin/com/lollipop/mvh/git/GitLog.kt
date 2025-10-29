package com.lollipop.mvh.git

sealed class GitLog(val msg: String) {

    class Info(msg: String) : GitLog(msg)
    class Surprise(msg: String) : GitLog(msg)
    class Error(msg: String) : GitLog(msg)

}