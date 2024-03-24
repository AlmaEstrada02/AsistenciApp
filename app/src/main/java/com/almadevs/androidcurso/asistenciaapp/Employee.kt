package com.almadevs.androidcurso.asistenciaapp

class Employee (
    val id_usuario: String,
    val nombre_completo: String,
    val status_usuario: Int,
    val punto_encuentro: Int
) {
    fun getEstado(): String {
        return if (status_usuario == 0) {
            "Activo"
        } else {
            "Inactivo"
        }
    }
    fun getPuntoEncuentro(): String {
        return if (punto_encuentro == 0) {
            "Si"
        } else {
            "No"
        }
    }
}

