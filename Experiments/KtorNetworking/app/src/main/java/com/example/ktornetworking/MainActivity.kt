package com.example.ktornetworking

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ktornetworking.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Note(
    var id: Int,
    var title: String = "",
    var body: String = "",
    var createTime: Long = 0,
    var modifyTime: Long = 0,
    var folderID: Int = -1
)

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            println(getAllNotes())
            addNote()
            println(getAllNotes())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
    }

    private suspend fun getAllNotes(): List<Note> {
        val client = HttpClient(Android)
        try {
            return Json.decodeFromString(client.get("http://10.0.2.2:8080/notes"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emptyList()
    }

    private suspend fun addNote() {
        val client = HttpClient(Android)
        try {
            client.post("http://10.0.2.2:8080/notes") {
                contentType(ContentType.Application.Json)
                body = Json.encodeToString(Note(123, "NEW TITLE", "NEW BODY", 456, 789, 1))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}