package br.com.raulreis.shortcut

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.AsyncTask
import android.os.PersistableBundle
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

class ShortcutWrapper(private val context: Context) {

    private var shortManager = context.getSystemService(ShortcutManager::class.java)

    fun addShortcut(url: String, onUriAdded:() -> Unit) {
        shortManager?.let {
            InsertShortcutTask.run(it, context, url, onUriAdded)
        }
    }

    fun getShortcuts(): Collection<ShortcutInfo> {
        return shortManager.dynamicShortcuts?.filterNot {
            it.isImmutable
        } ?: arrayListOf()
    }

    private class InsertShortcutTask(
        private val shortcutManager: ShortcutManager,
        private val context: Context,
        private val onUriAdded: () -> Unit) : AsyncTask<String, Void, ShortcutInfo>() {

        companion object {
            fun run(
                shortcutManager: ShortcutManager,
                context: Context,
                url: String,
                onUriAdded: () -> Unit
            ) =
                InsertShortcutTask(shortcutManager, context, onUriAdded).apply {
                    execute(url)
                }
        }
        override fun doInBackground(vararg urls: String?): ShortcutInfo {
            val url = urls.first()
            val uri = Uri.parse(url)
            val icon =  try {
                val iconUri = uri.buildUpon().path("favicon.ico").build()
                val conn = URL(iconUri.toString()).openConnection()
                conn.connect()
                val stream = conn.getInputStream()
                val bis = BufferedInputStream(stream, 8192)
                val bitmap = BitmapFactory.decodeStream(bis)

                Icon.createWithBitmap(bitmap)
            }
            catch (e: IOException) {
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground)
            }

            return ShortcutInfo.Builder(context, url)
                .setShortLabel(uri.host!!)
                .setLongLabel(uri.toString())
                .setIntent(Intent(Intent.ACTION_VIEW, uri))
                .setExtras(PersistableBundle().apply {
                    putLong("refresh", System.currentTimeMillis())
                })
                .setIcon(icon)
                .build()
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: ShortcutInfo?) {
            shortcutManager.addDynamicShortcuts(arrayListOf(result))
            onUriAdded.invoke()
        }
    }
}