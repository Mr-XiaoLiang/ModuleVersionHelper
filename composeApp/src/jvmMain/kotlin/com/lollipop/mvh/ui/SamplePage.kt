package com.lollipop.mvh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lollipop.mvh.data.ConfigChooseManager
import com.lollipop.mvh.widget.ConfigChooser
import com.lollipop.mvh.widget.ContentPage

object SamplePageState {

    val headerModule by lazy {
        ConfigChooseManager.optModule("sample_header")
    }
    val footerModule by lazy {
        ConfigChooseManager.optModule("sample_footer")
    }

}

@Composable
fun SamplePage() {
    var sampleHeader by remember { SamplePageState.headerModule.currentContent }
    var sampleFooter by remember { SamplePageState.footerModule.currentContent }
    ContentPage {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "头部",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    ConfigChooser(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        module = SamplePageState.headerModule,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                    }
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
                    text = sampleHeader
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "脚部",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    ConfigChooser(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        module = SamplePageState.footerModule,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ) {
                    }
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
                    text = sampleFooter
                )
            }
        }
    }
}