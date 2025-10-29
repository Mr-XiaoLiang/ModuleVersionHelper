package com.lollipop.mvh.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ContentPage(content: @Composable () -> Unit) {
    Box(modifier = Modifier) {
        content()
    }
}