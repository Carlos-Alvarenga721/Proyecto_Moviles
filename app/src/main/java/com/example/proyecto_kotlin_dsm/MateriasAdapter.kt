package com.example.proyecto_kotlin_dsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MateriasAdapter(
    private var listaMaterias: List<Materia>
) : RecyclerView.Adapter<MateriasAdapter.MateriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_materia, parent, false)
        return MateriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val materia = listaMaterias[position]
        holder.textCodigo.text = materia.nombre
        holder.textGrupo.text = materia.grupo
        holder.textUbicacion.text = materia.ubicacion
        holder.textDocente.text = materia.docente
        holder.textHorario.text = materia.horario
        holder.textDias.text = materia.dia

        // Listener para editar
        holder.buttonEditar.setOnClickListener {
            onEditarClickListener?.invoke(materia)
        }

        // Listener para eliminar
        holder.buttonEliminar.setOnClickListener {
            onEliminarClickListener?.invoke(materia)
        }
    }

    override fun getItemCount(): Int = listaMaterias.size

    fun actualizarLista(nuevaLista: List<Materia>) {
        listaMaterias = nuevaLista
        notifyDataSetChanged()
    }

    // Listeners para editar y eliminar
    var onEditarClickListener: ((Materia) -> Unit)? = null
    var onEliminarClickListener: ((Materia) -> Unit)? = null

    class MateriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCodigo: TextView = itemView.findViewById(R.id.textCodigo)
        val textGrupo: TextView = itemView.findViewById(R.id.textGrupo)
        val textUbicacion: TextView = itemView.findViewById(R.id.textUbicacion)
        val textDocente: TextView = itemView.findViewById(R.id.textDocente)
        val textHorario: TextView = itemView.findViewById(R.id.textHorario)
        val textDias: TextView = itemView.findViewById(R.id.textDias)
        val buttonEditar: Button = itemView.findViewById(R.id.buttonEditar)
        val buttonEliminar: Button = itemView.findViewById(R.id.buttonEliminar)
    }
}