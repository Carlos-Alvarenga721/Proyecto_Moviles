package com.example.proyecto_kotlin_dsm

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddMateriaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_materia)

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextGrupo = findViewById<EditText>(R.id.editTextGrupo)
        val editTextDocente = findViewById<EditText>(R.id.editTextDocente)
        val spinnerInicio = findViewById<Spinner>(R.id.spinnerInicio)
        val spinnerFin = findViewById<Spinner>(R.id.spinnerFin)
        val spinnerUbicacion = findViewById<Spinner>(R.id.spinnerUbicacion)
        val buttonAdd = findViewById<ImageButton>(R.id.buttonAdd)

        val horarios = listOf("07:00 am", "08:00 am", "09:00 am", "10:00 am", "11:00 am", "12:00 pm")
        val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_spinner_item, horarios)
        adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInicio.adapter = adapterHorarios
        spinnerFin.adapter = adapterHorarios

        val salones = listOf("Aula 101", "Aula 102", "Aula 103", "Laboratorio 1", "Laboratorio 2")
        val adapterSalones = ArrayAdapter(this, android.R.layout.simple_spinner_item, salones)
        adapterSalones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUbicacion.adapter = adapterSalones

        buttonAdd.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val grupo = editTextGrupo.text.toString()
            val docente = editTextDocente.text.toString()
            val horarioInicio = spinnerInicio.selectedItem.toString()
            val horarioFin = spinnerFin.selectedItem.toString()
            val horario = "$horarioInicio - $horarioFin"
            val ubicacion = spinnerUbicacion.selectedItem.toString()

            val dias = mutableListOf<String>()
            if (findViewById<CheckBox>(R.id.checkLunes).isChecked) dias.add("Lunes")
            if (findViewById<CheckBox>(R.id.checkMartes).isChecked) dias.add("Martes")
            if (findViewById<CheckBox>(R.id.checkMiercoles).isChecked) dias.add("Miércoles")
            if (findViewById<CheckBox>(R.id.checkJueves).isChecked) dias.add("Jueves")
            if (findViewById<CheckBox>(R.id.checkViernes).isChecked) dias.add("Viernes")
            if (findViewById<CheckBox>(R.id.checkSabado).isChecked) dias.add("Sábado")
            if (findViewById<CheckBox>(R.id.checkDomingo).isChecked) dias.add("Domingo")

            if (nombre.isNotEmpty() && grupo.isNotEmpty() && docente.isNotEmpty() && dias.isNotEmpty()) {
                val materia = Materia(nombre, grupo, dias.joinToString(", "), horario, docente, ubicacion)
                guardarMateriaEnFirebase(materia)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarMateriaEnFirebase(materia: Materia) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance()
            val materiasRef = database.getReference("materias").child(user.uid)
            val nuevaMateriaRef = materiasRef.push()
            nuevaMateriaRef.setValue(materia)
                .addOnSuccessListener {
                    Toast.makeText(this, "Materia guardada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}