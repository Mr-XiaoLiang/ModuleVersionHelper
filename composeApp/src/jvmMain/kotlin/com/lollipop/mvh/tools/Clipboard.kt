package com.lollipop.mvh.tools

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object Clipboard {

    fun copy(content: String) {
        Toolkit.getDefaultToolkit().systemClipboard
            .setContents(StringSelection(content), null)
    }

}