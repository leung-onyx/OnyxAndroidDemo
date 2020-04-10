package com.onyx.gallery.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onyx.gallery.extensions.addPathToDB
import com.simplemobiletools.commons.helpers.REFRESH_PATH

class RefreshMediaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val path = intent.getStringExtra(REFRESH_PATH) ?: return
        context.addPathToDB(path)
    }
}
