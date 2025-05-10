package com.example.proyecto_kotlin_dsm

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.appcompat.app.AlertDialog
import android.widget.LinearLayout
import android.widget.EditText
import com.google.firebase.database.DatabaseError

class MateriasActivity : BaseActivity() {

    private lateinit var recyclerMaterias: RecyclerView
    private lateinit var adapter: MateriasAdapter
    private lateinit var listaMaterias: MutableList<Materia>
    private lateinit var spinnerDia: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_materias)

        recyclerMaterias = findViewById(R.id.recyclerMaterias)
        spinnerDia = findViewById(R.id.spinnerDia)
        val buttonAgregar = findViewById<Button>(R.id.buttonAgregar)

        listaMaterias = mutableListOf()
        adapter = MateriasAdapter(listaMaterias)
        recyclerMaterias.layoutManager = LinearLayoutManager(this)
        recyclerMaterias.adapter = adapter

        adapter.onEditarClickListener = { materia ->
            mostrarDialogoEditar(materia)
        }

        adapter.onEliminarClickListener = { materia ->
            eliminarMateria(materia)
        }

        obtenerMateriasDeFirebase()

        val dias = listOf("Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dias)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDia.adapter = spinnerAdapter

        spinnerDia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val diaSeleccionado = dias[position]
                filtrarMaterias(diaSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonAgregar.setOnClickListener {
            startActivity(Intent(this, AddMateriaActivity::class.java))
        }
    }

    private fun mostrarDialogoEditar(materia: Materia) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val editTextNombre = EditText(this).apply {
            hint = "Nombre"
            setText(materia.nombre)
        }
        val editTextGrupo = EditText(this).apply {
            hint = "Grupo"
            setText(materia.grupo)
        }
        val editTextDocente = EditText(this).apply {
            hint = "Docente"
            setText(materia.docente)
        }
        val editTextHorario = EditText(this).apply {
            hint = "Horario"
            setText(materia.horario)
        }
        val editTextUbicacion = EditText(this).apply {
            hint = "Ubicación"
            setText(materia.ubicacion)
        }

        layout.addView(editTextNombre)
        layout.addView(editTextGrupo)
        layout.addView(editTextDocente)
        layout.addView(editTextHorario)
        layout.addView(editTextUbicacion)

        AlertDialog.Builder(this)
            .setTitle("Editar Materia")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = editTextNombre.text.toString()
                val grupo = editTextGrupo.text.toString()
                val docente = editTextDocente.text.toString()
                val horario = editTextHorario.text.toString()
                val ubicacion = editTextUbicacion.text.toString()

                if (nombre.isNotEmpty() && grupo.isNotEmpty() && docente.isNotEmpty() && horario.isNotEmpty() && ubicacion.isNotEmpty()) {
                    val nuevaMateria = materia.copy(
                        nombre = nombre,
                        grupo = grupo,
                        docente = docente,
                        horario = horario,
                        ubicacion = ubicacion
                    )
                    actualizarMateriaEnFirebase(materia.key, nuevaMateria)
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarMateria(materia: Materia) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            val materiasRef = database.getReference("materias").child(user.uid)
            materiasRef.child(materia.key).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Materia eliminada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun actualizarMateriaEnFirebase(key: String, materia: Materia) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            val materiasRef = database.getReference("materias").child(user.uid)
            materiasRef.child(key).setValue(materia)
                .addOnSuccessListener {
                    Toast.makeText(this, "Materia actualizada", Toast.LENGTH_SHORT).show()
                    obtenerMateriasDeFirebase()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun obtenerMateriasDeFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            val materiasRef = database.getReference("materias").child(user.uid)

            materiasRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMaterias.clear()
                    for (materiaSnapshot in snapshot.children) {
                        val materia = materiaSnapshot.getValue(Materia::class.java)
                        materia?.let {
                            it.key = materiaSnapshot.key ?: ""
                            listaMaterias.add(it)
                        }
                    }
                    adapter.actualizarLista(listaMaterias)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MateriasActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun filtrarMaterias(dia: String) {
        if (dia == "Todos") {
            adapter.actualizarLista(listaMaterias)
        } else {
            val filtradas = listaMaterias.filter { it.dia.contains(dia) }
            adapter.actualizarLista(filtradas)
        }
    }
}