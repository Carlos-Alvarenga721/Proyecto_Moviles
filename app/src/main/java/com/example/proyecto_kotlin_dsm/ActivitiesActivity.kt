package com.example.proyecto_kotlin_dsm

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.activitiesAdapter.Actividad
import com.example.proyecto_kotlin_dsm.activitiesAdapter.ActividadAdapter
import com.example.proyecto_kotlin_dsm.activitiesAdapter.Evaluacion
import com.example.proyecto_kotlin_dsm.activitiesAdapter.EvaluacionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

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

        val recyclerViewActividad = findViewById<RecyclerView>(R.id.recyclerViewActividades)
        recyclerViewActividad.layoutManager = LinearLayoutManager(this)

        // Datos temporales
        val listaActividades = listOf(
            Actividad("Tarea 1", "12/05", "10:00", "Matemática", "Alta"),
            Actividad("Tarea 2", "13/05", "14:30", "Historia", "Baja"),
            Actividad("Tarea 3", "14/05", "08:45","Física", "Media")
        )

        recyclerViewActividad.adapter = ActividadAdapter(listaActividades)

        val recyclerViewEvaluacion = findViewById<RecyclerView>(R.id.recyclerViewEvaluaciones)
        recyclerViewEvaluacion.layoutManager = LinearLayoutManager(this)

        // Datos temporales
        val listaEvaluaciones = listOf(
            Evaluacion("Parcial 1", "Calculo II", "12/05", "10:00", "20%"),
            Evaluacion("Parcial 1", "POO", "18/05", "16:00", "15%"),
            Evaluacion("Laboratorio 2", "Física II", "22/05", "08:45", "10%")
        )

        recyclerViewEvaluacion.adapter = EvaluacionAdapter(listaEvaluaciones)

        // Boton agregar
        val addButton = findViewById<FloatingActionButton>(R.id.fab_agregar)
        addButton.setOnClickListener {
            mostrarModalAgregarActividad()
        }
    }

    private fun mostrarModalAgregarActividad() {
        val dialogView = layoutInflater.inflate(R.layout.modal_agregar_actividad, null)
        val dialog = Dialog(this)

        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(), // 90% del ancho de pantalla
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        // Inicializar elementos
        val etTitulo = dialogView.findViewById<EditText>(R.id.etTitulo)
        val contadorTitulo = dialogView.findViewById<TextView>(R.id.contadorTitulo)
        etTitulo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                contadorTitulo.text = "${s?.length ?: 0}/32"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val contadorDescripcion = dialogView.findViewById<TextView>(R.id.contadorDescripcion)
        etDescripcion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                contadorDescripcion.text = "${s?.length ?: 0}/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Spinner
        val spinnerPrioridad = dialogView.findViewById<Spinner>(R.id.spinnerPrioridad)
        val opciones = listOf("Alta", "Media", "Baja")
        spinnerPrioridad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)

        // DatePicker
        val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)
        etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etFecha.setText("$d/${m+1}/$y")
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Botones
        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            // lógica para agregar actividad
            dialog.dismiss()
        }

        dialog.show()

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