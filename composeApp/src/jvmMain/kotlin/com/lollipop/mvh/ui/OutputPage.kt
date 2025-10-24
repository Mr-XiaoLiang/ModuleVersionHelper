package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.lollipop.mvh.widget.ContentPage
import com.lollipop.mvh.widget.OutputPanel

object OutputPageState {

    val outputValue = mutableStateOf("")

}

@Composable
fun OutputPage() {
    ContentPage {
        val outputValue by remember { OutputPageState.outputValue }
        OutputPanel(outputValue)
    }
}