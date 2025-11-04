package com.lollipop.mvh.data

class TemplateInfo(
    val groupId: String,
    val artifactId: String,
    val displayName: String
) {

    val alias = mutableListOf<TemplateInfo>()

    fun addAlias(group: String, artifact: String) {
        alias.add(TemplateInfo(group, artifact, displayName))
    }

}