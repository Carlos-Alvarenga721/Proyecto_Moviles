package com.example.proyecto_kotlin_dsm.activitiesAdapter

import Actividad
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.R

class ActividadAdapter(private val actividades: List<Actividad>) :
    RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvPrioridad: TextView = view.findViewById(R.id.tvPrioridad)
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad_activities, parent, false)
        return ActividadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = actividades[position]
        holder.tvTitulo.text = actividad.titulo
        holder.tvFecha.text = actividad.fecha
        holder.tvPrioridad.text = actividad.prioridad  // Asegúrate de tener este campo
        holder.tvMateria.text = actividad.materia
        holder.tvHora.text = actividad.hora
    }

    override fun getItemCount(): Int = actividades.size
}
