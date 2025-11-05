package com.lollipop.mvh.data

class TemplateInfo(
    val groupId: String,
    val artifactId: String,
    val displayName: String
) {

    val alias = mutableListOf<TemplateInfo>()

    val module: String by lazy {
        "$groupId:$artifactId"
    }

    fun addAlias(group: String, artifact: String) {
        alias.add(TemplateInfo(group, artifact, displayName))
    }

    fun aliasContains(module: String): Boolean {
        return alias.any { it.module == module }
    }

}