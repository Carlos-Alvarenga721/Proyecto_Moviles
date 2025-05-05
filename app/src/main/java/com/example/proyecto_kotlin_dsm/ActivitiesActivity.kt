package com.example.proyecto_kotlin_dsm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.activitiesAdapter.Actividad
import com.example.proyecto_kotlin_dsm.activitiesAdapter.ActividadAdapter

class ActivitiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_activities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewActividades)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Datos temporales
        val listaActividades = listOf(
            Actividad("Tarea 1", "12/05", "Alta", "Matemática", "10:00"),
            Actividad("Tarea 2", "13/05", "Baja", "Historia", "14:30"),
            Actividad("Tarea 3", "14/05", "Media", "Física", "08:45")
        )

        recyclerView.adapter = ActividadAdapter(listaActividades)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                true
            }
            R.id.action_profile -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}