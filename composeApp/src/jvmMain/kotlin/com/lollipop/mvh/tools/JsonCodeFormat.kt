package com.lollipop.mvh.tools

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.util.*

object JsonCodeFormat {

    val defaultOption = Option(
        indentFactor = 4,
        bracketColorArray = arrayOf(
            Color(0xFFB71C1C),
            Color(0xFF880E4F),
            Color(0xFF311B92),
            Color(0xFF1A237E),
            Color(0xFF0D47A1),
            Color(0xFF01579B),
            Color(0xFF006064),
            Color(0xFF004D40),
            Color(0xFF1B5E20),
            Color(0xFF33691E),
            Color(0xFF827717),
            Color(0xFFF57F17),
            Color(0xFFFF6F00),
            Color(0xFFE65100),
            Color(0xFFBF360C),
            Color(0xFF3E2723),
            Color(0xFF212121),
            Color(0xFF263238),
        ),
        keyColor = Color(0xFF0CA771),
        valueColor = Color(0xFF212121),
        colonColor = Color(0xFF424242)
    )

    fun format(json: String, option: Option = defaultOption, deepColor: Boolean = false): AnnotatedString {
        try {
            val jsonValue = getJsonValue(json, option.indentFactor)
            return buildAnnotatedString {
                val inputStream = StringReader(jsonValue)
                var bracketColorIndex = 0
                val colorList = LinkedList<Color>()
                inputStream.forEachLine { line ->
                    var startBracket = findIndex(line, 0, "{")
                    if (startBracket < 0) {
                        startBracket = findIndex(line, 0, "[")
                    }
                    if (startBracket >= 0) {
                        val bracketColor = option.getBracketColor(bracketColorIndex)
                        // 括号的颜色要成对
                        bracketColorIndex++
                        if (!deepColor) {
                            colorList.addLast(bracketColor)
                        }
                        withStyle(SpanStyle(bracketColor)) { append(line) }
                    }
                    var endBracket = findIndex(line, 0, "}")
                    if (endBracket < 0) {
                        endBracket = findIndex(line, 0, "]")
                    }
                    if (endBracket >= 0) {
                        val bracketColor = if (deepColor) {
                            bracketColorIndex--
                            option.getBracketColor(bracketColorIndex)
                        } else {
                            if (colorList.isEmpty()) {
                                option.getBracketColor(bracketColorIndex++)
                            } else {
                                colorList.removeLast()
                            }
                        }
                        if (bracketColorIndex < 0) {
                            bracketColorIndex = 0
                        }
                        withStyle(SpanStyle(bracketColor)) { append(line) }
                    }
                    val colonIndex = findIndex(line, 0, ":")
                    if (colonIndex >= 0) {
                        val key = line.take(colonIndex)
                        val value = line.substring(colonIndex + 1)
                        withStyle(SpanStyle(option.keyColor)) { append(key) }
                        withStyle(SpanStyle(option.colonColor)) { append(":") }
                        withStyle(SpanStyle(option.valueColor)) { append(value) }
                    }
                    append("\n")
                }
            }
        } catch (e: Throwable) {
            val errorInfo = e.stackTraceToString()
            return buildAnnotatedString {
                withStyle(SpanStyle(Color.Red)) {
                    append(errorInfo)
                }
                withStyle(SpanStyle(Color.Black)) {
                    append(json)
                }
            }
        }
    }

    private fun getJsonValue(json: String, indentFactor: Int): String {
        val trimValue = json.trim()
        if (trimValue.startsWith("{") && trimValue.endsWith("}")) {
            return JSONObject(trimValue).toString(indentFactor)
        }
        if (trimValue.startsWith("[") && trimValue.endsWith("]")) {
            return JSONArray(trimValue).toString(indentFactor)
        }
        throw IllegalArgumentException("Content is not json")
    }


    private fun findIndex(line: String, before: Int, key: String): Int {
        return line.indexOf(string = key, startIndex = before, ignoreCase = false)
    }

    class Option(
        val indentFactor: Int = 4,
        val bracketColorArray: Array<Color>,
        val keyColor: Color,
        val valueColor: Color,
        val colonColor: Color = Color.Black,
    ) {

        private val bracketColorCount = bracketColorArray.size

        fun getBracketColor(index: Int): Color {
            if (bracketColorArray.isEmpty()) {
                return Color.Black
            }
            if (index < 0) {
                return bracketColorArray[0]
            }
            return bracketColorArray[index % bracketColorCount]
        }

    }

}