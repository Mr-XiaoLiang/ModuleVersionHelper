package com.lollipop.mvh.widget

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lollipop.mvh.data.ChooserModule
import com.lollipop.mvh.tools.Clipboard

@Composable
fun OutputPanel(
    contentValue: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
            .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            val iconColor = MaterialTheme.colorScheme.onBackground
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "ContentCopy",
                modifier = Modifier.size(24.dp)
                    .clickable {
                        Clipboard.copy(contentValue)
                    },
                tint = iconColor
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth().weight(1F)
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            text = contentValue
        )
    }
}

@Composable
fun ColumnScope.ConfigFragment(
    title: String,
    module: ChooserModule,
    template: String
) {
    val moduleContent by remember { module.currentContent }
    Column(
        modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
            .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (title.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            ConfigChooser(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                module = module,
                template = template,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
            }
        }
        SelectionContainer(
            modifier = Modifier.fillMaxWidth().weight(1F)
        ) {
            Text(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                text = moduleContent
            )
        }
    }
}

@Composable
fun ConfigPanel(
    firstTitle: String,
    secondTitle: String,
    firstModule: ChooserModule,
    secondModule: ChooserModule,
    firstTemplate: String,
    secondTemplate: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        ConfigFragment(title = firstTitle, module = firstModule, template = firstTemplate)
        ConfigFragment(title = secondTitle, module = secondModule, template = secondTemplate)
    }
}

@Composable
fun ConfigPanel(
    title: String,
    module: ChooserModule,
    template: String,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        ConfigFragment(title = title, module = module, template = template)
    }
}