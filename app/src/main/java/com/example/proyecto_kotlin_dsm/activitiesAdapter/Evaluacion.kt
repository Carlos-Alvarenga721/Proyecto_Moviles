
data class Evaluacion(
    val id: String = "",        // <--- este campo es el key de Firebase
    val titulo: String = "",
    val materia: String = "",
    val fecha: String = "",
    val hora: String = "",
    val porcentaje: String = "",
    val estado: String = "",
    val nota: Double = 0.0
)
