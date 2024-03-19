package br.com.raulreis.shortcut

import android.content.pm.ShortcutInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.raulreis.shortcut.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var shortcutWrapper : ShortcutWrapper
    private lateinit var shortcuts : MutableList<ShortcutInfo>
    private lateinit var adapter : MyAdapter

    companion object {
        private const val KEY_ADD_FAVORITE = "br.com.raulreis.shortcut.ADD_FAVORITE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shortcutWrapper = ShortcutWrapper(this@MainActivity)

        if (intent.action == KEY_ADD_FAVORITE) {
            showUrlDialog()
        }

        setupList()
        setupButton()
    }

    private fun showUrlDialog() {
        val edtText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(R.string.shortcut_short_label)
            .setMessage(R.string.type_url)
            .setView(edtText)
            .setPositiveButton(android.R.string.ok) { _, _ ->
               val url = edtText.text.toString().trim()
                
                if (url.isNotEmpty()) {
                    shortcutWrapper.addShortcut(url) {
                        refreshList()
                    }
                }
                else {
                    Toast.makeText(this, "Url n!ao pode ser vazia", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun refreshList() {
        shortcuts.clear()
        shortcuts.addAll(shortcutWrapper.getShortcuts())
        adapter.notifyDataSetChanged()
    }

    private fun setupButton() {
        with(binding) {
            fab.setOnClickListener {
                showUrlDialog()
            }
        }
    }
    private fun setupList() {
        shortcuts = arrayListOf()
        adapter = MyAdapter(shortcuts)
        with(binding) {
            rv.adapter = adapter
            rv.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    class MyAdapter(private val items: List<ShortcutInfo>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(
                LayoutInflater.from(parent.context)
                    .inflate(android.R.layout.simple_list_item_1, parent, false)
            )
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(shortcutInfo: ShortcutInfo) {
                itemView.findViewById<TextView>(android.R.id.text1).text = shortcutInfo.shortLabel
            }
        }
    }
}