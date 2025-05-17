package com.example.proyecto_kotlin_dsm.models

data class Evaluacion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "",
    val materia: String = "",
    val nota: Double = 0.0,
    val porcentaje: String = "0%"
)
