package com.lollipop.mvh.ui

enum class Destination(val label: String) {

    /**
     * 显示Git状态
     */
    GIT_STATE("Git状态"),

    /**
     * 显示仓库列表
     */
    REPOSITORY_LIST("仓库列表"),

    /**
     * 添加仓库
     */
    ADD_REPOSITORY("添加仓库"),

    /**
     * 版本信息的管理器
     */
    VERSION_COLUMN_MANAGER("信息管理"),

    /**
     * 添加版本列信息
     */
    ADD_VERSION_COLUMN("添加信息"),

    /**
     * 模块信息的输出
     */
    MODULE_INFO_OUTPUT("版本输出"),

}