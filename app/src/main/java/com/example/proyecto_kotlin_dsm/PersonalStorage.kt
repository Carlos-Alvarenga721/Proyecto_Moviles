package com.example.proyecto_kotlin_dsm

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import kotlin.compareTo


class PersonalStorage : AppCompatActivity() {

    private lateinit var adapter: FolderAdapter
    private lateinit var folderList: MutableList<Folder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var storageRoot: File
    private val PICK_FILE_REQUEST_CODE = 1001
    private var selectedFolderForFile: File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_storage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: ""

        if (uid.isNotEmpty()) {
            storageRoot = File(getExternalFilesDir(null), "CarpetasUsuario/$uid")
            if (!storageRoot.exists()) storageRoot.mkdirs()
        } else {
            // Manejar el caso cuando no hay usuario logueado (por seguridad o mostrar error)
            Toast.makeText(this, "No se encontró usuario autenticado", Toast.LENGTH_SHORT).show()
            finish()  // Opcional: cerrar actividad o redirigir
        }


        folderList = getFoldersFromStorage()

        adapter = FolderAdapter(folderList, { folder ->
            val intent = Intent(this, FolderContentsActivity::class.java)
            intent.putExtra("folder_name", folder.name)
            intent.putExtra("user_uid", uid)
            startActivity(intent)
        }, { folder ->
            selectedFolderForFile = File(storageRoot, folder.name)
            openFilePicker()
        })


        recyclerView = findViewById(R.id.recyclerViewFolders)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        registerForContextMenu(recyclerView)

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar)
        fabAgregar.setOnClickListener {
            showCreateFolderDialog()
        }


    }

    private fun openFolderContents(folder: Folder) {
        val intent = Intent(this, FolderContentsActivity::class.java)
        intent.putExtra("folder_name", folder.name)
        startActivity(intent)
    }

    private fun showCreateFolderDialog() {
        val input = EditText(this)
        input.hint = "Nombre de la carpeta"

        AlertDialog.Builder(this)
            .setTitle("Nueva carpeta")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val newFolder = File(storageRoot, folderName)
                    if (!newFolder.exists()) {
                        if (newFolder.mkdir()) {
                            folderList.add(Folder(folderName))
                            adapter.notifyItemInserted(folderList.size - 1)
                        } else {
                            Toast.makeText(this, "No se pudo crear la carpeta", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, "La carpeta ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun getFoldersFromStorage(): MutableList<Folder> {
        val folders = mutableListOf<Folder>()
        val files = storageRoot.listFiles()
        files?.filter { it.isDirectory }?.forEach { folders.add(Folder(it.name)) }
        return folders
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // puedes filtrar a "image/*" para solo imágenes, por ejemplo
        }
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFolderForFile?.let { folder ->
                    saveFileToFolder(uri, folder)
                }
            }
        }
    }

    private fun saveFileToFolder(uri: Uri, folder: File) {
        val contentResolver = contentResolver
        try {
            val fileName = getFileName(uri) ?: "archivo_desconocido"
            val destFile = File(folder, fileName)

            contentResolver.openInputStream(uri).use { inputStream ->
                destFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "Archivo guardado en ${folder.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar archivo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    // Menú contextual
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.recyclerViewFolders) {
            menu.setHeaderTitle("Opciones")
            menu.add(0, v.id, 0, "Eliminar carpeta")
            menu.add(0, v.id, 1, "Subir archivo")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = adapter.getPositionForContextMenu()
        return when (item.title) {
            "Eliminar carpeta" -> {
                deleteFolderAt(position)
                true
            }
            "Subir archivo" -> {
                selectedFolderForFile = File(storageRoot, folderList[position].name)
                openFilePicker()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun deleteFolderAt(position: Int) {
        val folder = folderList[position]
        val folderFile = File(storageRoot, folder.name)
        if (folderFile.exists()) {
            if (deleteFolder(folderFile)) {
                folderList.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "Carpeta eliminada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo eliminar la carpeta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFolder(folder: File): Boolean {
        if (folder.isDirectory) {
            folder.listFiles()?.forEach { deleteFolder(it) }
        }
        return folder.delete()
    }
}