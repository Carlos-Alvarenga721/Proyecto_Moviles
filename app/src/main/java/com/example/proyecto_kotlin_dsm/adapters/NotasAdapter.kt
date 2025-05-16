package com.example.proyecto_kotlin_dsm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.R
import com.example.proyecto_kotlin_dsm.models.Nota

class NotasAdapter(private val lista: List<Nota>) : RecyclerView.Adapter<NotasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtActividad: TextView = view.findViewById(R.id.txtActividad)
        val txtCalificacion: TextView = view.findViewById(R.id.txtCalificacion)
        val txtPorcentaje: TextView = view.findViewById(R.id.txtPorcentaje)
        val txtNotaGlobal: TextView = view.findViewById(R.id.txtNotaGlobal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_nota, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.txtActividad.text = item.nombreActividad
        holder.txtCalificacion.text = "%.2f".format(item.calificacion)
        holder.txtPorcentaje.text = "%.2f%%".format(item.porcentaje)
        holder.txtNotaGlobal.text = "%.3f".format(item.notaGlobal)
    }

    override fun getItemCount(): Int = lista.size
}
