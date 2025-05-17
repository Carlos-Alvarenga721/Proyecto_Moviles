package com.example.proyecto_kotlin_dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private val folders: List<Folder>,
    private val onClick: (Folder) -> Unit,
    private val onLongClick: (Folder) -> Unit // Añadimos la acción de click largo
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    private var positionForContextMenu = -1
    fun getPositionForContextMenu(): Int = positionForContextMenu

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewFolder)
        val textView: TextView = itemView.findViewById(R.id.textViewFolderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.textView.text = folder.name

        holder.itemView.setOnClickListener { onClick(folder) }

        /*
        holder.itemView.setOnLongClickListener {
            positionForContextMenu = holder.adapterPosition  // Usar adapterPosition aquí
            onLongClick(folder)
            false // para que se muestre también el menú contextual
        }*/

        holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
            positionForContextMenu = holder.adapterPosition  // Y aquí también
        }
    }

    override fun getItemCount() = folders.size


}