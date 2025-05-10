package com.example.proyecto_kotlin_dsm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.R
import com.example.proyecto_kotlin_dsm.models.Horario

class HorarioAdapter(private val lista: List<Horario>) : RecyclerView.Adapter<HorarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCodigo: TextView = view.findViewById(R.id.txtCodigo)
        val txtMateria: TextView = view.findViewById(R.id.txtMateria)
        val txtDocente: TextView = view.findViewById(R.id.txtDocente)
        val txtHora: TextView = view.findViewById(R.id.txtHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_horario, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.txtCodigo.text = item.codigo
        holder.txtMateria.text = item.materia
        holder.txtDocente.text = item.docente
        holder.txtHora.text = item.hora
    }

    override fun getItemCount(): Int = lista.size
}
