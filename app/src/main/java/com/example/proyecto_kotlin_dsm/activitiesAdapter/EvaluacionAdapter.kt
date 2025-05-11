package com.example.proyecto_kotlin_dsm.activitiesAdapter

import Evaluacion
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.R

class EvaluacionAdapter(private val evaluaciones: List<Evaluacion>) :
    RecyclerView.Adapter<EvaluacionAdapter.EvaluacionViewHolder>() {

    inner class EvaluacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvPorcentaje: TextView = view.findViewById(R.id.tvPorcentaje)
        val tvMateria: TextView = view.findViewById(R.id.tvMateria)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvNota: TextView = view.findViewById(R.id.tvNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evaluacion_activities, parent, false)
        return EvaluacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvaluacionViewHolder, position: Int) {
        val evaluacion = evaluaciones[position]

        // Configurar los campos básicos
        holder.tvTitulo.text = evaluacion.titulo
        holder.tvFecha.text = evaluacion.fecha
        holder.tvPorcentaje.text = evaluacion.porcentaje
        holder.tvMateria.text = evaluacion.materia
        holder.tvHora.text = evaluacion.hora

        // Configurar la nota con formato según el estado
        val notaFormateada = String.format("%.1f", evaluacion.nota)
        holder.tvNota.text = notaFormateada

        if (evaluacion.estado == "Pendiente") {
            // Para evaluaciones pendientes, mostrar nota en gris
            holder.tvNota.setTextColor(Color.GRAY)
        } else {
            // Para evaluaciones realizadas, mostrar nota en negro
            holder.tvNota.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int = evaluaciones.size
}