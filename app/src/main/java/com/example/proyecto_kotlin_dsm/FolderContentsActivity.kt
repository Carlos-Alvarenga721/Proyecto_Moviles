package com.example.proyecto_kotlin_dsm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderContentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileAdapter
    private lateinit var folder: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_contents)

        val folderName = intent.getStringExtra("folder_name") ?: return finish()
        val uid = intent.getStringExtra("user_uid") ?: return finish()

  //ESTO SERVIA ANTES
/*
        folder = File(getExternalFilesDir(null), "CarpetasUsuario/$folderName")
        if (!folder.exists() || !folder.isDirectory) {
            Toast.makeText(this, "La carpeta no existe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }*/

        folder = File(getExternalFilesDir(null), "CarpetasUsuario/$uid/$folderName")
        if (!folder.exists() || !folder.isDirectory) {
            Toast.makeText(this, "La carpeta no existe", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewFiles)
        val files = folder.listFiles()?.map { FileItem(it) } ?: listOf()
        adapter = FileAdapter(files) { fileItem ->
            openFile(fileItem.file)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun openFile(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, contentResolver.getType(uri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No hay app para abrir este archivo", Toast.LENGTH_SHORT).show()
        }
    }
}
