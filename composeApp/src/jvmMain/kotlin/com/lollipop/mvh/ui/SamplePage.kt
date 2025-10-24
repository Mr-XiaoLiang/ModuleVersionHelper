package com.lollipop.mvh.ui

import androidx.compose.runtime.Composable
import com.lollipop.mvh.data.ConfigChooseManager
import com.lollipop.mvh.widget.ConfigPanel
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
    ContentPage {
        ConfigPanel(
            firstTitle = "头部",
            secondTitle = "脚部",
            firstModule = SamplePageState.headerModule,
            secondModule = SamplePageState.footerModule,
            firstTemplate = "",
            secondTemplate = ""
        )
    }

//    var sampleHeader by remember { SamplePageState.headerModule.currentContent }
//    var sampleFooter by remember { SamplePageState.footerModule.currentContent }
//    ContentPage {
//        Column(
//            modifier = Modifier.fillMaxSize().padding(12.dp)
//        ) {
//
//            Column(
//                modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
//                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = "头部",
//                        color = MaterialTheme.colorScheme.onBackground
//                    )
//                    ConfigChooser(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 6.dp),
//                        module = SamplePageState.headerModule,
//                        contentColor = MaterialTheme.colorScheme.onBackground
//                    ) {
//                    }
//                }
//
//                Text(
//                    modifier = Modifier.fillMaxWidth().weight(1F)
//                        .padding(horizontal = 4.dp, vertical = 4.dp)
//                        .border(
//                            width = 1.dp,
//                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
//                            shape = RoundedCornerShape(14.dp)
//                        )
//                        .padding(horizontal = 8.dp, vertical = 8.dp)
//                        .verticalScroll(rememberScrollState()),
//                    text = sampleHeader
//                )
//            }
//
//            Column(
//                modifier = Modifier.fillMaxWidth().weight(1F).padding(4.dp)
//                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        text = "脚部",
//                        color = MaterialTheme.colorScheme.onBackground
//                    )
//                    ConfigChooser(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 6.dp),
//                        module = SamplePageState.footerModule,
//                        contentColor = MaterialTheme.colorScheme.onBackground
//                    ) {
//                    }
//                }
//
//                Text(
//                    modifier = Modifier.fillMaxWidth().weight(1F)
//                        .padding(horizontal = 4.dp, vertical = 4.dp)
//                        .border(
//                            width = 1.dp,
//                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
//                            shape = RoundedCornerShape(14.dp)
//                        )
//                        .padding(horizontal = 8.dp, vertical = 8.dp)
//                        .verticalScroll(rememberScrollState()),
//                    text = sampleFooter
//                )
//            }
//        }
//    }
}