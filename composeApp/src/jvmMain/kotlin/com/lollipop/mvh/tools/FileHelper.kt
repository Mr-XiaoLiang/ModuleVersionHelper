package com.lollipop.mvh.tools

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object FileHelper {

    fun writeFile(file: File, content: String) {
        try {
            file.writeText(content)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun writeJson(file: File, json: JSONObject) {
        writeFile(file, json.toString())
    }

    fun writeJson(file: File, json: JSONArray) {
        writeFile(file, json.toString())
    }

    fun readFile(file: File): String {
        return try {
            file.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun readJsonObject(file: File): JSONObject {
        return try {
            JSONObject(readFile(file))
        } catch (e: Exception) {
            e.printStackTrace()
            JSONObject()
        }
    }

    fun readJsonArray(file: File): JSONArray {
        return try {
            JSONArray(readFile(file))
        } catch (e: Exception) {
            e.printStackTrace()
            JSONArray()
        }
    }

}