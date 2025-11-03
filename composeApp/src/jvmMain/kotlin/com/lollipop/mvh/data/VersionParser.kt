package com.lollipop.mvh.data

import java.io.File

object VersionParser {

    fun readBuildGradle(file: File, libs: LibsVersionToml): List<VersionInfo> {
        val outList = mutableListOf<VersionInfo>()
        try {
            file.forEachLine { line ->
                val trimLine = line.trim()
                val suffix = file.name.substringAfterLast(".")
                val isKts = suffix == "kts"
                var key = ""
                if (isKts || trimLine.contains("(")) {
                    key = trimLine.substringBefore("(")
                } else {
                    key = trimLine.substringBefore(" \"").trim()
                }
                if (key == "implementation"
                    || key == "api"
                    || key == "compileOnly"
                    || key == "annotationProcessor"
                    || key == "kapt"
                    || key == "testImplementation"
                    || key == "debugImplementation"
                    || key == "releaseImplementation"
                ) {
                    val module = parse(libs, trimLine)
                    if (module != null) {
                        outList.add(module)
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return outList
    }

    fun parse(libs: LibsVersionToml, line: String): VersionInfo? {
        // 双引号代码
        if (line.contains("\"")) {
            // 带有引号，就取引号的
            val content = line.substringAfter("\"").substringBeforeLast("\"").trim()
            return buildVersion(content)
        }
        // 单引号代码
        if (line.contains("'")) {
            // 带有引号，就取引号的
            val content = line.substringAfter("'").substringBeforeLast("'").trim()
            return buildVersion(content)
        }
        return libs.findByGradleCode(line)?.mapVersion()
    }

    private fun ModuleInfo.mapVersion(): VersionInfo {
        return VersionInfo(group, name, version)
    }

    private fun buildVersion(content: String): VersionInfo? {
        val split = content.split(":")
        if (split.size != 3) {
            return null
        }
        return VersionInfo(group = split[0], name = split[1], version = split[2])
    }

    class VersionInfo(
        val group: String,
        val name: String,
        val version: String
    ) {
        val module: String by lazy {
            "$group:$name"
        }
    }

}