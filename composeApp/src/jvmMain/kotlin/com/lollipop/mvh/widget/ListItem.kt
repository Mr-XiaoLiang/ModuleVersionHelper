package com.lollipop.mvh.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    clickCallback: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = MaterialTheme.colors.primarySurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .optClick(clickCallback)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        content()
    }
}

private fun Modifier.optClick(clickCallback: (() -> Unit)?): Modifier {
    if (clickCallback == null) {
        return this
    }
    return clickable(onClick = clickCallback)
}
