package com.example.proyecto_kotlin_dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileAdapter(
    private val files: List<FileItem>,
    private val onClick: (FileItem) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.imageViewFileIcon)
        val name: TextView = itemView.findViewById(R.id.textViewFileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileItem = files[position]
        holder.name.text = fileItem.file.name
        holder.itemView.setOnClickListener { onClick(fileItem) }
        // Opcional: cambiar ícono según tipo (pdf, imagen, etc.)
    }

    override fun getItemCount() = files.size
}
