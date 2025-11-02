package com.lollipop.mvh.data

import com.lollipop.mvh.tools.Flow
import com.lollipop.mvh.tools.doAsync
import org.json.JSONObject
import java.io.File

class LibsVersionToml {

    companion object {
        fun parse(file: File): Flow<LibsVersionToml> {
            return doAsync {
                TomlReader.read(file)
            }
        }

        private fun getModuleInfo(api: String, value: TomlValue, versions: TomlBuffer): ModuleInfo? {
            if (value is TomlValue.Obj) {
                val module = value.opt("module") ?: ""
                val versionRef = value.opt("version.ref") ?: ""
                val version = if (versionRef.isNotEmpty()) {
                    versions.opt(versionRef)?.contentValue ?: ""
                } else {
                    value.opt("version") ?: ""
                }
                if (module.isNotEmpty()) {
                    return ModuleInfo(
                        group = module.substringBefore(":"),
                        name = module.substringAfter(":"),
                        api = api,
                        version = version
                    )
                }
                val group = value.opt("group") ?: ""
                if (group.isNotEmpty()) {
                    val name = value.opt("name") ?: ""
                    return ModuleInfo(
                        group = group,
                        name = name,
                        api = api,
                        version = version
                    )
                }
            }
            return null
        }

        private fun getLibraryApi(value: String): String {
            return value.replace("-", ".")
        }


        fun create(
            versions: TomlBuffer,
            libraries: TomlBuffer,
            plugins: TomlBuffer,
            bundles: TomlBuffer,
            metadata: TomlBuffer
        ): LibsVersionToml {
            val libs = LibsVersionToml()
            libs.versionMap.putAll(versions.bufferMap)
            libs.librariesMap.putAll(libraries.bufferMap)
            libs.pluginsMap.putAll(plugins.bufferMap)
            libs.bundlesMap.putAll(bundles.bufferMap)
            libs.metadataMap.putAll(metadata.bufferMap)
            libraries.bufferMap.entries.forEach { entry ->
                val api = "libs." + getLibraryApi(entry.key)
                val moduleInfo = getModuleInfo(api, entry.value, versions)
                if (moduleInfo != null) {
                    libs.addModule(moduleInfo)
                }
            }
            // 不支持 plugins，因为用不上
//            plugins.bufferMap.entries.forEach { entry ->
//                val api = "libs.plugins." + getLibraryApi(entry.key)
//                val moduleInfo = getModuleInfo(api, entry.value, versions)
//                if (moduleInfo != null) {
//                    libs.addModule(moduleInfo)
//                }
//            }
            // 不支持 bundles
//            bundles.bufferMap.entries.forEach { entry ->
//                val api = "libs.bundles." + getLibraryApi(entry.key)
//                val moduleInfo = getModuleInfo(api, entry.value, versions)
//                if (moduleInfo != null) {
//                    libs.addModule(moduleInfo)
//                }
//            }
            return libs
        }

    }

    // [versions] # 定义版本号
    // [libraries] # 定义依赖库
    // [bundles] # 定义依赖集合
    // [plugins] # 定义插件
    // [metadata] # 描述信息

    private val modules = mutableListOf<ModuleInfo>()
    private val libsApiMap = mutableMapOf<String, ModuleInfo>()
    private val moduleApiMap = mutableMapOf<String, ModuleInfo>()

    private val versionMap = mutableMapOf<String, TomlValue>()
    private val librariesMap = mutableMapOf<String, TomlValue>()
    private val pluginsMap = mutableMapOf<String, TomlValue>()
    private val bundlesMap = mutableMapOf<String, TomlValue>()
    private val metadataMap = mutableMapOf<String, TomlValue>()

    private fun addModule(moduleInfo: ModuleInfo) {
        modules.add(moduleInfo)
        libsApiMap[moduleInfo.api] = moduleInfo
        moduleApiMap[moduleInfo.module] = moduleInfo
    }

    fun find(module: String): ModuleInfo? {
        return moduleApiMap[module] ?: libsApiMap[module]
    }

    fun findByGradleCode(gradleCode: String): ModuleInfo? {
        if (gradleCode.contains("libs")) {
            val content = gradleCode.substringAfter("(").substringBefore(")").trim()
            return find(content)
        }
        return null
    }

}

class ModuleInfo(
    val group: String,
    val name: String,
    val api: String,
    val version: String
) {

    val module: String by lazy {
        "$group:$name"
    }

}

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
        val (key, value) = readKeyValuePair(line)
        if (value.startsWith("\"") && value.endsWith("\"")) {
            out.put(key, value.substring(1, value.length - 1))
        } else if (value.startsWith("'") && value.endsWith("'")) {
            out.put(key, value.substring(1, value.length - 1))
        } else if (value.startsWith("{") && value.endsWith("}")) {
            out.put(key, readRichValue(value))
        }
    }

    private fun readByLibraries(line: String, out: TomlBuffer) {
        val (key, value) = readKeyValuePair(line)
        if (value.startsWith("{") && value.endsWith("}")) {
            out.put(key, readRichValue(value))
        }
    }

    private fun readByPlugins(line: String, out: TomlBuffer) {
        val (key, value) = readKeyValuePair(line)
        if (value.startsWith("{") && value.endsWith("}")) {
            out.put(key, readRichValue(value))
        }
    }

    private fun readByBundles(line: String, out: TomlBuffer) {
        val (key, value) = readKeyValuePair(line)
        if (value.startsWith("{") && value.endsWith("}")) {
            out.put(key, readRichValue(value))
        }
    }

    private fun readByMetadata(line: String, out: TomlBuffer) {
        val (key, value) = readKeyValuePair(line)
        if (value.startsWith("\"") && value.endsWith("\"")) {
            out.put(key, value.substring(1, value.length - 1))
        } else if (value.startsWith("'") && value.endsWith("'")) {
            out.put(key, value.substring(1, value.length - 1))
        } else if (value.startsWith("{") && value.endsWith("}")) {
            out.put(key, readRichValue(value))
        }
    }

    private fun buildLibsInfo(
        versions: TomlBuffer,
        libraries: TomlBuffer,
        plugins: TomlBuffer,
        bundles: TomlBuffer,
        metadata: TomlBuffer
    ): LibsVersionToml {
        return LibsVersionToml.create(versions, libraries, plugins, bundles, metadata)
    }

    private fun readKeyValuePair(line: String): Pair<String, String> {
        val key = line.substringBefore("=").trim()
        val value = line.substringAfter("=").substringBefore("#").trim()
        return Pair(key, value)
    }

    private fun readRichValue(value: String): Map<String, String> {
        val content = value.substringAfter("{").substringBefore("}")
        val entryArray = content.split(",")
        val outMap = mutableMapOf<String, String>()
        entryArray.forEach { entry ->
            val keyValuePair = entry.split("=")
            val key = keyValuePair[0].trim()
            val value = keyValuePair[1].substringAfter("\"").substringBeforeLast("\"").trim()
            outMap[key] = value
        }
        return outMap
    }

}

class TomlBuffer {

    val bufferMap = mutableMapOf<String, TomlValue>()

    fun put(key: String, value: String) {
        bufferMap[key] = TomlValue.Str(value)
    }

    fun put(key: String, value: Map<String, String>) {
        bufferMap[key] = TomlValue.Obj(value)
    }

    fun opt(key: String): TomlValue? {
        return bufferMap[key]
    }

    fun toJson(): JSONObject {
        val jsonObj = JSONObject()
        bufferMap.entries.forEach { entry ->
            val key = entry.key
            when (val value = entry.value) {
                is TomlValue.Obj -> {
                    jsonObj.put(key, JSONObject().also {
                        value.map.entries.forEach { valueEntry ->
                            it.put(valueEntry.key, valueEntry.value)
                        }
                    })
                }

                is TomlValue.Str -> {
                    jsonObj.put(key, value.value)
                }
            }
        }
        return jsonObj
    }

    override fun toString(): String {
        return toJson().toString()
    }

}

sealed class TomlValue {

    abstract val contentValue: String

    class Obj(val map: Map<String, String>) : TomlValue() {

        fun opt(key: String): String? {
            return map[key]
        }

        override val contentValue: String by lazy {
            map.entries.joinToString(separator = ",", prefix = "{", postfix = "}") { entry ->
                "${entry.key} = \"${entry.value}\""
            }
        }

        override fun toString(): String {
            return "TomlValue{$contentValue}"
        }

    }

    class Str(val value: String) : TomlValue() {

        override val contentValue: String
            get() {
                return value
            }

        override fun toString(): String {
            return "TomlValue{$value}"
        }

    }
}