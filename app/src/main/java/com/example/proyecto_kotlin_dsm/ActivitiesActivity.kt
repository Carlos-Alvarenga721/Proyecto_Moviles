package com.example.proyecto_kotlin_dsm

import Evaluacion
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_kotlin_dsm.activitiesAdapter.EvaluacionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ActivitiesActivity : AppCompatActivity() {

    private lateinit var recyclerViewEvaluacion: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var listaEvaluaciones: MutableList<Evaluacion>
    private lateinit var listaMaterias: ArrayList<String>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activities)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        recyclerViewEvaluacion = findViewById(R.id.recyclerViewEvaluaciones)
        recyclerViewEvaluacion.layoutManager = LinearLayoutManager(this)
        listaEvaluaciones = mutableListOf()
        listaMaterias = ArrayList()

        // Obtener lista de materias antes de configurar evaluaciones
        obtenerMateriasDeFirebase()
        obtenerEvaluacionesDeFirebase()

        findViewById<FloatingActionButton>(R.id.fab_agregar).setOnClickListener {
            mostrarModalEvaluacion()
        }
    }

    private fun obtenerMateriasDeFirebase() {
        val userId = auth.currentUser?.uid ?: return
        val materiasRef = FirebaseDatabase.getInstance().getReference("materias").child(userId)

        materiasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMaterias.clear()
                // Agregar una opción por defecto
                listaMaterias.add("Seleccione una materia")

                for (materiaSnapshot in snapshot.children) {
                    val nombreMateria = materiaSnapshot.child("nombre").getValue(String::class.java)
                    if (nombreMateria != null) {
                        listaMaterias.add(nombreMateria)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ActivitiesActivity,
                    "Error al cargar materias: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Función unificada para mostrar el modal (agregar o editar)
    private fun mostrarModalEvaluacion(evaluacion: Evaluacion? = null) {
        val esEdicion = evaluacion != null
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.modal_agregar_actividad, null)

        builder.setView(view)
        val dialog = builder.create()

        // Configurar el fondo del diálogo como transparente
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Cambiar el título del botón según sea agregar o editar
        val btnAgregar = view.findViewById<Button>(R.id.btnAgregar)
        if (esEdicion) {
            btnAgregar.text = "Actualizar"
        }

        // Configurar los elementos del modal
        val etTitulo = view.findViewById<EditText>(R.id.etTitulo)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val spinnerMateria = view.findViewById<Spinner>(R.id.spinnerMateria)
        val etFecha = view.findViewById<EditText>(R.id.etFecha)
        val etHora = view.findViewById<EditText>(R.id.etHora)
        val etPorcentaje = view.findViewById<EditText>(R.id.etPorcentaje)
        val spinnerEstado = view.findViewById<Spinner>(R.id.spinnerEstado)
        val layoutNota = view.findViewById<LinearLayout>(R.id.layoutNota)
        val etNota = view.findViewById<EditText>(R.id.etNota)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val contadorTitulo = view.findViewById<TextView>(R.id.contadorTitulo)
        val contadorDescripcion = view.findViewById<TextView>(R.id.contadorDescripcion)

        // Título del modal (cambia según sea agregar o editar)
        val tituloModal = view.findViewById<TextView>(R.id.tituloModal)
        if (tituloModal != null) {
            tituloModal.text = if (esEdicion) "Editar Actividad" else "Agregar Actividad"
        }

        // Configurar el adaptador para el spinner de materias
        val materiaAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaMaterias
        )
        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMateria.adapter = materiaAdapter

        // Configurar el adaptador para el spinner de estados
        val estadosArray = arrayOf("Seleccione un estado", "Pendiente", "Realizada")
        val estadoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estadosArray
        )
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = estadoAdapter

        // Listener para mostrar/ocultar el campo de nota según el estado seleccionado
        spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val estadoSeleccionado = estadosArray[position]
                if (estadoSeleccionado == "Realizada") {
                    layoutNota.visibility = View.VISIBLE
                } else {
                    layoutNota.visibility = View.GONE
                    if (!esEdicion) {
                        etNota.setText("") // Solo resetear si no estamos en modo edición
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                layoutNota.visibility = View.GONE
            }
        }

        // Configurar el contador de caracteres para el título
        etTitulo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                contadorTitulo.text = "${s?.length ?: 0}/32"
            }
        })

        // Configurar el contador de caracteres para la descripción
        etDescripcion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                contadorDescripcion.text = "${s?.length ?: 0}/100"
            }
        })

        // Configurar el selector de fecha
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFecha.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Configurar el selector de hora
        etHora.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val amPm = if (selectedHour < 12) "am" else "pm"
                    val hour12Format = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                    val selectedTime = String.format("%02d:%02d %s", hour12Format, selectedMinute, amPm)
                    etHora.setText(selectedTime)
                },
                hour, minute, false
            )
            timePickerDialog.show()
        }

        // Configurar campo de porcentaje
        etPorcentaje.setOnClickListener {
            val options = arrayOf("5%", "10%", "15%", "20%", "25%", "30%", "40%", "50%")
            val porcentajeDialog = AlertDialog.Builder(this)
                .setTitle("Seleccione un porcentaje")
                .setItems(options) { _, which ->
                    etPorcentaje.setText(options[which])
                }
                .create()
            porcentajeDialog.show()
        }

        // Si estamos en modo edición, rellenar los campos con los datos existentes
        if (esEdicion) {
            etTitulo.setText(evaluacion?.titulo)
            etDescripcion.setText(evaluacion?.descripcion ?: "")  // Asumiendo que añadirás el campo descripción
            etFecha.setText(evaluacion?.fecha)
            etHora.setText(evaluacion?.hora)
            etPorcentaje.setText(evaluacion?.porcentaje)

            // Seleccionar la materia correcta en el spinner
            val materiaIndex = listaMaterias.indexOf(evaluacion?.materia)
            if (materiaIndex >= 0) {
                spinnerMateria.setSelection(materiaIndex)
            }

            // Seleccionar el estado correcto en el spinner
            val estadoIndex = estadosArray.indexOf(evaluacion?.estado)
            if (estadoIndex >= 0) {
                spinnerEstado.setSelection(estadoIndex)
            }

            // Si el estado es "Realizada", mostrar y establecer la nota
            if (evaluacion?.estado == "Realizada") {
                layoutNota.visibility = View.VISIBLE
                etNota.setText(evaluacion.nota.toString())
            }
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnAgregar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val materia = if (spinnerMateria.selectedItemPosition > 0)
                spinnerMateria.selectedItem.toString() else ""
            val fecha = etFecha.text.toString()
            val hora = etHora.text.toString()
            val porcentaje = etPorcentaje.text.toString()
            val estado = if (spinnerEstado.selectedItemPosition > 0)
                spinnerEstado.selectedItem.toString() else ""

            // Valor por defecto para nota
            var nota = 0.0

            // Si el campo de nota está visible, intentar obtener su valor
            if (layoutNota.visibility == View.VISIBLE) {
                val notaStr = etNota.text.toString().trim()
                if (notaStr.isNotEmpty()) {
                    try {
                        nota = notaStr.toDouble()
                        // Validar que la nota esté entre 0 y 10
                        if (nota < 0.0 || nota > 10.0) {
                            Toast.makeText(this, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "La nota debe ser un número válido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            // Validaciones
            if (titulo.isEmpty()) {
                Toast.makeText(this, "Ingrese un título para la evaluación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (materia.isEmpty() || materia == "Seleccione una materia") {
                Toast.makeText(this, "Seleccione una materia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fecha.isEmpty()) {
                Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (hora.isEmpty()) {
                Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (porcentaje.isEmpty()) {
                Toast.makeText(this, "Seleccione un porcentaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (estado.isEmpty() || estado == "Seleccione un estado") {
                Toast.makeText(this, "Seleccione un estado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto evaluación
            val nuevaEvaluacion = Evaluacion(
                id = evaluacion?.id ?: "",  // Usar ID existente en caso de edición
                titulo = titulo,
                descripcion = descripcion,
                materia = materia,
                fecha = fecha,
                hora = hora,
                porcentaje = porcentaje,
                estado = estado,
                nota = nota
            )

            if (esEdicion) {
                actualizarEvaluacionEnFirebase(nuevaEvaluacion)
            } else {
                guardarEvaluacionEnFirebase(nuevaEvaluacion)
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun guardarEvaluacionEnFirebase(evaluacion: Evaluacion) {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("evaluaciones").child(userId)
        val id = database.push().key ?: return

        // Crear una copia de la evaluación con el ID generado
        val evaluacionConId = evaluacion.copy(id = id)

        database.child(id).setValue(evaluacionConId)
            .addOnSuccessListener {
                Toast.makeText(this, "Evaluación agregada", Toast.LENGTH_SHORT).show()
                obtenerEvaluacionesDeFirebase()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agregar evaluación: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarEvaluacionEnFirebase(evaluacion: Evaluacion) {
        val userId = auth.currentUser?.uid ?: return
        val evaluacionId = evaluacion.id
        if (evaluacionId.isEmpty()) return

        database = FirebaseDatabase.getInstance().getReference("evaluaciones").child(userId)

        database.child(evaluacionId).setValue(evaluacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Evaluación actualizada", Toast.LENGTH_SHORT).show()
                obtenerEvaluacionesDeFirebase()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar evaluación: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerEvaluacionesDeFirebase() {
        val userId = auth.currentUser?.uid ?: return
        database = FirebaseDatabase.getInstance().getReference("evaluaciones").child(userId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaEvaluaciones.clear()
                for (evaluacionSnapshot in snapshot.children) {
                    val evaluacion = evaluacionSnapshot.getValue(Evaluacion::class.java)
                    evaluacion?.let { listaEvaluaciones.add(it) }
                }
                // Actualizar el adaptador con la nueva lista y el listener para manejar clicks
                recyclerViewEvaluacion.adapter = EvaluacionAdapter(listaEvaluaciones) { evaluacion ->
                    // Este lambda se ejecuta cuando se hace clic en un elemento
                    mostrarModalEvaluacion(evaluacion)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ActivitiesActivity,
                    "Error al obtener evaluaciones: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}