package com.example.proyecto_kotlin_dsm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.R
import com.example.proyecto_kotlin_dsm.models.Evaluacion

class ActividadAdapter(private val lista: List<Evaluacion>) :
    RecyclerView.Adapter<ActividadAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.txtTitulo)
        val mensaje: TextView = view.findViewById(R.id.txtMensaje)
        val fecha: TextView = view.findViewById(R.id.txtFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.titulo.text = item.titulo
        holder.mensaje.text = item.descripcion
        holder.fecha.text = item.fecha
    }

    override fun getItemCount(): Int = lista.size
}
