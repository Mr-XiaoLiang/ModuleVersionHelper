package com.lollipop.mvh.data

import com.lollipop.mvh.tools.Flow
import com.lollipop.mvh.tools.doAsync
import java.io.File

class LibsVersionToml {

    companion object {
        fun parse(file: File): Flow<LibsVersionToml> {
            return doAsync {
                TomlReader.read(file)
            }
        }

    }

    private val modules = mutableListOf<ModuleInfo>()

    // [versions] # 定义版本号
    // [libraries] # 定义依赖库
    // [bundles] # 定义依赖集合
    // [plugins] # 定义插件

    private val versionMap = mutableMapOf<String, String>()
    private val librariesMap = mutableMapOf<String, ModuleInfo>()
    private val pluginsMap = mutableMapOf<String, ModuleInfo>()
    private val bundlesMap = mutableMapOf<String, List<ModuleInfo>>()


    class ModuleInfo(
        val name: String,
        val path: String,
        val version: String
    )

    object TomlReader {

        // plugins, libraries, bundles, versions, metadata
        private const val VERSIONS = "versions"
        private const val LIBRARIES = "libraries"
        private const val PLUGINS = "plugins"
        private const val BUNDLES = "bundles"
        private const val METADATA = "metadata"

        private fun log(value: String) {
            println("[TomlReader] $value")
        }

        fun read(file: File): LibsVersionToml {
            log("read: ${file.path}")
            var currentMode = ""
            val versionsBuffer = TomlBuffer()
            val librariesBuffer = TomlBuffer()
            val pluginsBuffer = TomlBuffer()
            val bundlesBuffer = TomlBuffer()
            val metadataBuffer = TomlBuffer()
            file.forEachLine { line ->
                val trimLine = line.trim()
                if (trimLine.isBlank()) {
                    // 忽略空白行
                } else if (trimLine.startsWith("#")) {
                    println("note: $trimLine")
                } else if (trimLine.startsWith("[")) {
                    // 切换模块
                    currentMode = trimLine.substringAfter("[").substringBefore("]")
                    println("mode: $currentMode")
                } else {
                    when (currentMode) {
                        TomlReader.VERSIONS -> {
                            readByVersions(line, versionsBuffer)
                        }

                        TomlReader.LIBRARIES -> {
                            readByLibraries(line, librariesBuffer)
                        }

                        TomlReader.PLUGINS -> {
                            readByPlugins(line, pluginsBuffer)
                        }

                        TomlReader.BUNDLES -> {
                            readByBundles(line, bundlesBuffer)
                        }

                        TomlReader.METADATA -> {
                            readByMetadata(line, metadataBuffer)
                        }
                    }
                }
            }
            return buildLibsInfo(versionsBuffer, librariesBuffer, pluginsBuffer, bundlesBuffer, metadataBuffer)
        }

        private fun readByVersions(line: String, out: TomlBuffer) {
            // TODO
        }

        private fun readByLibraries(line: String, out: TomlBuffer) {
            // TODO
        }

        private fun readByPlugins(line: String, out: TomlBuffer) {
            // TODO
        }

        private fun readByBundles(line: String, out: TomlBuffer) {
            // TODO
        }

        private fun buildLibsInfo(
            versions: TomlBuffer,
            libraries: TomlBuffer,
            plugins: TomlBuffer,
            bundles: TomlBuffer,
            metadata: TomlBuffer
        ): LibsVersionToml {
            val libs = LibsVersionToml()
            // TODO
            return libs
        }

        private fun readByMetadata(line: String, out: TomlBuffer) {
            // TODO
        }

    }

    private class TomlBuffer {

        private val bufferMap = mutableMapOf<String, TomlValue>()

        fun put(key: String, value: String) {
            bufferMap[key] = TomlValue.Str(value)
        }

        fun put(key: String, value: Map<String, String>) {
            bufferMap[key] = TomlValue.Obj(value)
        }

    }

    private sealed class TomlValue {

        class Obj(val map: Map<String, String>) : TomlValue()

        class Str(val value: String) : TomlValue()
    }

}