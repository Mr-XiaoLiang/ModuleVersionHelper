package com.lollipop.mvh.ui

enum class Destination(val label: String) {

    /**
     * 显示Git状态
     */
    GIT_STATE("同步状态"),

    /**
     * 模板显示
     */
    TEMPLATE("输出模板"),

    /**
     * 仓库
     */
    REPOSITORY("代码仓库"),

    /**
     * 样板
     */
    SAMPLE("文档样板"),

    /**
     * 模块信息的输出
     */
    OUTPUT("文档输出"),

}