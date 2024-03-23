package com.almadevs.androidcurso.asistenciaapp

class Employee (
    val id_usuario: String,
    val nombre_completo: String,
    val status_usuario: Int
) {
    fun getEstado(): String {
        return if (status_usuario == 0) {
            "Activo"
        } else {
            "Inactivo"
        }
    }
}

