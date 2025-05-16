package com.example.proyecto_kotlin_dsm

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
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
    private lateinit var fileList: MutableList<FileItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_contents)

        val folderName = intent.getStringExtra("folder_name") ?: return finish()
        val uid = intent.getStringExtra("user_uid") ?: return finish()

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
        fileList = files.toMutableList()

        adapter = FileAdapter(fileList) { fileItem -> openFile(fileItem.file) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        registerForContextMenu(recyclerView)

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.recyclerViewFiles) {
            menu.setHeaderTitle("Opciones")
            menu.add(0, v.id, 0, "Eliminar archivo")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.getPositionForContextMenu() // Obtén la posición del archivo
        return when (item.title) {
            "Eliminar archivo" -> {
                deleteFileAt(position) // Elimina el archivo en esa posición
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun deleteFileAt(position: Int) {
        val fileItem = fileList[position]
        if (fileItem.file.exists() && fileItem.file.delete()) {
            fileList.removeAt(position) // Elimina el archivo de la lista
            adapter.notifyItemRemoved(position) // Notifica al adapter
            Toast.makeText(this, "Archivo eliminado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo eliminar el archivo", Toast.LENGTH_SHORT).show()
        }
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
