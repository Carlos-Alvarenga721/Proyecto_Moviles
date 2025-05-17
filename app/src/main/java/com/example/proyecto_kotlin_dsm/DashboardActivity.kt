package com.example.proyecto_kotlin_dsm

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.adapters.ActividadAdapter
import com.example.proyecto_kotlin_dsm.adapters.HorarioAdapter
import com.example.proyecto_kotlin_dsm.adapters.NotasAdapter
import com.example.proyecto_kotlin_dsm.models.Evaluacion
import com.example.proyecto_kotlin_dsm.models.Materia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

class DashboardActivity : BaseActivity() {

    private lateinit var rvActividades: RecyclerView
    private lateinit var rvHorarios: RecyclerView
    private lateinit var rvNotas: RecyclerView
    private lateinit var spinnerMaterias: Spinner

    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String

    private var listaMaterias = mutableListOf<Materia>()
    private var listaNotas = mutableListOf<Evaluacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Enlazar vistas
        rvActividades = findViewById(R.id.rvActividades)
        rvHorarios = findViewById(R.id.rvHorarios)
        rvNotas = findViewById(R.id.rvNotas)
        spinnerMaterias = findViewById(R.id.spinnerMaterias)

        // Layouts
        rvActividades.layoutManager = LinearLayoutManager(this)
        rvHorarios.layoutManager = LinearLayoutManager(this)
        rvNotas.layoutManager = LinearLayoutManager(this)

        // Firebase
        dbRef = FirebaseDatabase.getInstance().reference
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        cargarMaterias()
        cargarEvaluaciones()
    }

    private fun cargarMaterias() {
        dbRef.child("materias").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMaterias.clear()
                    val nombresMaterias = mutableListOf<String>()

                    for (materiaSnapshot in snapshot.children) {
                        val materia = materiaSnapshot.getValue(Materia::class.java)
                        if (materia != null) {
                            listaMaterias.add(materia)
                            nombresMaterias.add(materia.nombre.trim())
                        }
                    }

                    rvHorarios.adapter = HorarioAdapter(listaMaterias)

                    val adapterSpinner = ArrayAdapter(
                        this@DashboardActivity,
                        android.R.layout.simple_spinner_item,
                        nombresMaterias
                    )
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMaterias.adapter = adapterSpinner

                    spinnerMaterias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            filtrarNotasPorMateria(nombresMaterias[position])
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun cargarEvaluaciones() {
        dbRef.child("evaluaciones").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pendientes = mutableListOf<Evaluacion>()
                    listaNotas.clear()

                    for (evaSnapshot in snapshot.children) {
                        val evaluacion = evaSnapshot.getValue(Evaluacion::class.java)
                        if (evaluacion != null) {
                            if (evaluacion.estado == "Pendiente") {
                                pendientes.add(evaluacion)
                            } else if (evaluacion.estado == "Realizada") {
                                listaNotas.add(evaluacion)
                            }
                        }
                    }

                    rvActividades.adapter = ActividadAdapter(pendientes)
                    filtrarNotasPorMateria(spinnerMaterias.selectedItem?.toString() ?: "")
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun filtrarNotasPorMateria(nombreMateria: String) {
        val notasFiltradas = listaNotas.filter {
            it.materia.trim().equals(nombreMateria.trim(), ignoreCase = true)
        }
        rvNotas.adapter = NotasAdapter(notasFiltradas)
    }
}
