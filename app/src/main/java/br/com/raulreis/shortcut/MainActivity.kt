package br.com.raulreis.shortcut

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_ADD_FAVORITE = "br.com.raulreis.shortcut.ADD_FAVORITE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.action == KEY_ADD_FAVORITE) {
            findViewById<TextView>(R.id.txvMain).text = getString(R.string.shortcut_message)
        }
    }
}