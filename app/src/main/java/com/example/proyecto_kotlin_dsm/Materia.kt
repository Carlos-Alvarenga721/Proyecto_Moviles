package com.example.proyecto_kotlin_dsm

data class Materia(
    val nombre: String = "",
    val grupo: String = "",
    val dia: String = "",
    val horario: String = "",
    val docente: String = "",
    val ubicacion: String = "",
    var key: String = "" // Identificador único de Firebase
)