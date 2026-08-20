package com.sauryah.graion.ui.util

import android.content.Context
import android.content.Intent

object ShareHelper {

    /**
     * Shares content via Android system share sheet.
     */
    fun shareContent(context: Context, title: String, content: String, mimeType: String = "text/plain") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
