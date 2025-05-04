package com.example.proyecto_kotlin_dsm

import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var rvActividades: RecyclerView
    private lateinit var rvHorarios: RecyclerView
    private lateinit var rvNotas: RecyclerView
    private lateinit var spinnerMaterias: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        rvActividades = findViewById(R.id.rvActividades)
        rvHorarios = findViewById(R.id.rvHorarios)
        rvNotas = findViewById(R.id.rvNotas)
        spinnerMaterias = findViewById(R.id.spinnerMaterias)

        // Configuración los RecyclerViews con su layout
        rvActividades.layoutManager = LinearLayoutManager(this)
        rvHorarios.layoutManager = LinearLayoutManager(this)
        rvNotas.layoutManager = LinearLayoutManager(this)
    }
}
